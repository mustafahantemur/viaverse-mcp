[CmdletBinding()]
param(
    [switch] $SkipDockerDesktopStart
)

$ErrorActionPreference = "Stop"

$serviceDatabases = @(
    "viaverse_identity",
    "viaverse_marketplace",
    "viaverse_payment",
    "viaverse_messaging",
    "viaverse_media",
    "viaverse_notification",
    "viaverse_search",
    "viaverse_trust_gamification",
    "viaverse_ads_monetization",
    "viaverse_admin_bff",
    "viaverse_identity_test",
    "viaverse_marketplace_test",
    "viaverse_payment_test",
    "viaverse_messaging_test",
    "viaverse_media_test",
    "viaverse_notification_test",
    "viaverse_search_test",
    "viaverse_trust_gamification_test",
    "viaverse_ads_monetization_test",
    "viaverse_admin_bff_test"
)

function Fail($message) {
    Write-Error $message
    exit 1
}

function Ensure-Command($name, $hint) {
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        Fail "$name was not found. $hint"
    }
}

function Test-DockerReady {
    docker info *> $null
    return $LASTEXITCODE -eq 0
}

function Start-DockerDesktopIfPossible {
    if ($SkipDockerDesktopStart) {
        return
    }

    $dockerDesktop = Join-Path $env:ProgramFiles "Docker\Docker\Docker Desktop.exe"
    if (Test-Path $dockerDesktop) {
        Write-Host "Starting Docker Desktop..."
        Start-Process -FilePath $dockerDesktop -WindowStyle Hidden | Out-Null
    }
}

function Wait-DockerReady {
    if (Test-DockerReady) {
        return
    }

    Start-DockerDesktopIfPossible

    $deadline = (Get-Date).AddSeconds(150)
    do {
        if (Test-DockerReady) {
            Write-Host "Docker daemon is ready."
            return
        }

        Write-Host "Waiting for Docker daemon..."
        Start-Sleep -Seconds 3
    } while ((Get-Date) -lt $deadline)

    Fail "Docker daemon is not running. Start Docker Desktop and try again."
}

function Remove-ExistingCoreContainers {
    $containerNames = @(
        "viaverse-postgres",
        "viaverse-valkey",
        "viaverse-kafka",
        "viaverse-mailpit",
        "viaverse-seaweedfs",
        "viaverse-minio"
    )

    foreach ($containerName in $containerNames) {
        $existing = docker ps -a --filter "name=^/$containerName$" --format "{{.Names}}"
        if ($existing -eq $containerName) {
            Write-Host "Removing existing container $containerName..."
            docker rm -f $containerName | Out-Null
            if ($LASTEXITCODE -ne 0) {
                Fail "Could not remove existing container $containerName."
            }
        }
    }
}

function Wait-ContainerHealthy($containerName, $timeoutSeconds) {
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)
    do {
        $status = docker inspect --format="{{.State.Health.Status}}" $containerName 2>$null
        if ($status -eq "healthy") {
            Write-Host "$containerName is healthy."
            return
        }

        Write-Host "Waiting for $containerName health check..."
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)

    Fail "Timed out waiting for $containerName to become healthy."
}

function Test-TcpPortReady($targetHost, $port) {
    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $async = $client.BeginConnect($targetHost, $port, $null, $null)
        $wait = $async.AsyncWaitHandle.WaitOne(2000, $false)
        if (-not $wait) {
            return $false
        }
        $client.EndConnect($async)
        return $true
    } catch {
        return $false
    } finally {
        $client.Dispose()
    }
}

function Wait-PortReady($targetHost, $port, $timeoutSeconds, $label) {
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)
    do {
        if (Test-TcpPortReady $targetHost $port) {
            Write-Host "$label is accepting connections on ${targetHost}:${port}."
            return
        }

        Write-Host "Waiting for $label on ${targetHost}:${port}..."
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)

    Fail "Timed out waiting for $label on ${targetHost}:${port}."
}

function Get-ContainerEnv($containerName, $name, $fallback) {
    $value = docker exec $containerName printenv $name 2>$null
    if ($LASTEXITCODE -eq 0 -and -not [string]::IsNullOrWhiteSpace($value)) {
        return $value.Trim()
    }

    return $fallback
}

function Ensure-PostgresDatabases {
    $containerName = "viaverse-postgres"
    $postgresUser = Get-ContainerEnv $containerName "POSTGRES_USER" "viaverse"
    $postgresDb = Get-ContainerEnv $containerName "POSTGRES_DB" "viaverse"

    $existing = docker exec $containerName psql -U $postgresUser -d $postgresDb -Atc "SELECT datname FROM pg_database;"
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not read PostgreSQL database list from $containerName."
    }

    foreach ($database in $serviceDatabases) {
        if ($existing -contains $database) {
            continue
        }

        Write-Host "Creating PostgreSQL database $database..."
        docker exec $containerName createdb -U $postgresUser $database
        if ($LASTEXITCODE -ne 0) {
            $check = docker exec $containerName psql -U $postgresUser -d $postgresDb -Atc "SELECT 1 FROM pg_database WHERE datname = '$database';"
            if (($check | Select-Object -First 1) -ne "1") {
                Fail "Could not create PostgreSQL database $database."
            }
        }
    }
}

function Initialize-SeaweedFsBucket {
    $bucketName = "viaverse-media-local"
    $endpoint = "http://host.docker.internal:8333"
    $headCommand = "docker run --rm -e AWS_ACCESS_KEY_ID=viaverse -e AWS_SECRET_ACCESS_KEY=viaverse-local-secret -e AWS_DEFAULT_REGION=local amazon/aws-cli:2.17.57 s3api head-bucket --endpoint-url $endpoint --bucket $bucketName"
    $createCommand = "docker run --rm -e AWS_ACCESS_KEY_ID=viaverse -e AWS_SECRET_ACCESS_KEY=viaverse-local-secret -e AWS_DEFAULT_REGION=local amazon/aws-cli:2.17.57 s3api create-bucket --endpoint-url $endpoint --bucket $bucketName"

    for ($attempt = 1; $attempt -le 8; $attempt++) {
        cmd /c "$headCommand 1>nul 2>nul" | Out-Null
        if ($LASTEXITCODE -eq 0) {
            if ($attempt -eq 1) {
                Write-Host "SeaweedFS bucket $bucketName already exists."
            } else {
                Write-Host "SeaweedFS bucket $bucketName is ready."
            }
            return
        }

        cmd /c "$createCommand 1>nul 2>nul" | Out-Null
        Start-Sleep -Seconds 2
    }

    Fail "SeaweedFS bucket initialization failed."
}

Ensure-Command "docker" "Install Docker Desktop with Compose v2."
Wait-DockerReady
Remove-ExistingCoreContainers

$composeDir = Join-Path $PSScriptRoot "..\..\infra\docker-compose"
$composeDir = (Resolve-Path $composeDir).Path

Push-Location $composeDir
try {
    docker compose --env-file .env.example -f docker-compose.local.yml up -d postgres valkey kafka mailpit seaweedfs
    if ($LASTEXITCODE -ne 0) {
        Fail "Docker Compose could not start the core local infrastructure."
    }

    Wait-ContainerHealthy "viaverse-postgres" 120
    Wait-ContainerHealthy "viaverse-valkey" 90
    Ensure-PostgresDatabases
    Wait-PortReady "127.0.0.1" 8333 120 "SeaweedFS S3"
    Initialize-SeaweedFsBucket

    Write-Host "Core local infrastructure is ready."
}
finally {
    Pop-Location
}
