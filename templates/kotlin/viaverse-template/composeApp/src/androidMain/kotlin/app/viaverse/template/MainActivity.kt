package app.viaverse.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.viaverse.template.app.ViaverseTemplateApp
import app.viaverse.template.data.storage.AndroidStorageService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storageService = AndroidStorageService(this)
        setContent {
            ViaverseTemplateApp(
                storageService = storageService,
                onCloseRequest = { finish() }
            )
        }
    }
}
