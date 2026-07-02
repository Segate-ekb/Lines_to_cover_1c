/*
 * Lines_to_cover_1c.
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.segateekb.linestocover;

import java.util.Map;

/** Рендеринг результата (файл -> покрываемые строки) в JSON или genericCoverage XML. */
final class Reports {

    private Reports() {
    }

    /** JSON: { "путь": [строки] }. */
    static String json(Map<String, int[]> data) {
        StringBuilder sb = new StringBuilder("{\n");
        int index = 0;
        for (Map.Entry<String, int[]> entry : data.entrySet()) {
            if (index++ > 0) {
                sb.append(",\n");
            }
            sb.append("  ").append(quote(entry.getKey())).append(": [");
            int[] lines = entry.getValue();
            for (int i = 0; i < lines.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(lines[i]);
            }
            sb.append("]");
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    /** genericCoverage (SonarQube): все строки как covered="false" (список строк к покрытию). */
    static String genericCoverage(Map<String, int[]> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<coverage version=\"1\">\n");
        for (Map.Entry<String, int[]> entry : data.entrySet()) {
            sb.append("  <file path=\"").append(xml(entry.getKey())).append("\">\n");
            for (int line : entry.getValue()) {
                sb.append("    <lineToCover lineNumber=\"").append(line).append("\" covered=\"false\"/>\n");
            }
            sb.append("  </file>\n");
        }
        sb.append("</coverage>\n");
        return sb.toString();
    }

    private static String quote(String value) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                default -> sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }

    private static String xml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
