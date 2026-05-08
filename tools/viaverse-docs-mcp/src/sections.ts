import type { DocItem } from "./docs.js";

export type DocSection = {
  id: string;
  heading: string;
  level: number;
  startLine: number;
  endLine: number;
  content: string;
};

function slugify(input: string): string {
  return input
    .toLowerCase()
    .replace(/`/g, "")
    .replace(/[^a-z0-9ğüşöçıİĞÜŞÖÇ]+/gi, "-")
    .replace(/^-+|-+$/g, "");
}

export function extractSections(content: string): DocSection[] {
  const lines = content.split(/\r?\n/);
  const headings: Array<{ heading: string; level: number; line: number }> = [];

  for (let i = 0; i < lines.length; i++) {
    const match = lines[i].match(/^(#{1,6})\s+(.+)$/);
    if (match) {
      headings.push({
        level: match[1].length,
        heading: match[2].trim(),
        line: i + 1
      });
    }
  }

  if (headings.length === 0) {
    return [{
      id: "document",
      heading: "Document",
      level: 1,
      startLine: 1,
      endLine: lines.length,
      content
    }];
  }

  return headings.map((h, index) => {
    const next = headings[index + 1];
    const start = h.line;
    const end = next ? next.line - 1 : lines.length;
    const sectionContent = lines.slice(start - 1, end).join("\n");
    return {
      id: slugify(h.heading),
      heading: h.heading,
      level: h.level,
      startLine: start,
      endLine: end,
      content: sectionContent
    };
  });
}

export function getDocOutline(doc: DocItem) {
  return extractSections(doc.content).map(section => ({
    id: section.id,
    heading: section.heading,
    level: section.level,
    startLine: section.startLine,
    endLine: section.endLine
  }));
}

export function findSections(doc: DocItem, queries: string[], maxCharsPerSection = 5000) {
  const lowerQueries = queries.map(q => q.toLowerCase());
  const sections = extractSections(doc.content);

  return sections
    .filter(section => {
      const haystack = `${section.id}\n${section.heading}\n${section.content}`.toLowerCase();
      return lowerQueries.some(query => haystack.includes(query));
    })
    .map(section => ({
      id: section.id,
      heading: section.heading,
      level: section.level,
      startLine: section.startLine,
      endLine: section.endLine,
      content:
        section.content.length > maxCharsPerSection
          ? section.content.slice(0, maxCharsPerSection) + "\n\n[SECTION_TRUNCATED_BY_MCP]"
          : section.content
    }));
}
