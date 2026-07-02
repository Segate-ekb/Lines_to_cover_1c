/*
 * Lines_to_cover_1c.
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.segateekb.linestocover;

import com.github._1c_syntax.bsl.parser.BSLTokenizer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * CLI: перечисление покрываемых строк BSL-исходников.
 *
 * <pre>
 * lines-to-cover --src &lt;dir&gt; [--src &lt;dir&gt;...] [--base &lt;dir&gt;] [--out &lt;file&gt;] [--format json|generic]
 * </pre>
 *
 * Использует только парсер bsl-parser: без сервера отладки, EDT и платформы 1С.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        List<Path> sources = new ArrayList<>();
        Path base = null;
        Path out = null;
        String format = "json";

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--src", "-s" -> sources.add(Path.of(args[++i]).toAbsolutePath().normalize());
                    case "--base", "-b" -> base = Path.of(args[++i]).toAbsolutePath().normalize();
                    case "--out", "-o" -> out = Path.of(args[++i]).toAbsolutePath().normalize();
                    case "--format", "-f" -> format = args[++i].toLowerCase(java.util.Locale.ROOT);
                    case "--help", "-h" -> {
                        printUsage(System.out);
                        return;
                    }
                    default -> {
                        System.err.println("Unknown argument: " + args[i]);
                        System.exit(2);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Missing value for the last option");
            System.exit(2);
        }

        if (sources.isEmpty()) {
            printUsage(System.err);
            System.exit(2);
        }
        if (!format.equals("json") && !format.equals("generic")) {
            System.err.println("Unknown format: " + format + " (expected json|generic)");
            System.exit(2);
        }

        Map<String, int[]> result = scan(sources, base);
        String rendered = format.equals("generic") ? Reports.genericCoverage(result) : Reports.json(result);
        write(rendered, out);
    }

    private static Map<String, int[]> scan(List<Path> sources, Path base) {
        Map<String, int[]> result = new LinkedHashMap<>();
        for (Path srcDir : sources) {
            if (!Files.isDirectory(srcDir)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(srcDir)) {
                walk.filter(p -> p.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".bsl"))
                        .sorted()
                        .forEach(p -> collect(p, base, result));
            } catch (IOException e) {
                System.err.println("Failed to scan " + srcDir + ": " + e.getMessage());
                System.exit(1);
            }
        }
        return result;
    }

    private static void collect(Path file, Path base, Map<String, int[]> result) {
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            int[] lines = LinesToCoverage.getLines(new BSLTokenizer(content).getAst());
            if (lines.length > 0) {
                result.put(relativeKey(file, base), lines);
            }
        } catch (IOException e) {
            System.err.println("Skip " + file + ": " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Parse error in " + file + ": " + e.getMessage());
        }
    }

    private static String relativeKey(Path file, Path base) {
        Path path = file.toAbsolutePath().normalize();
        if (base != null && path.startsWith(base)) {
            path = base.relativize(path);
        }
        return path.toString().replace('\\', '/');
    }

    private static void write(String text, Path out) {
        try {
            if (out != null) {
                if (out.getParent() != null) {
                    Files.createDirectories(out.getParent());
                }
                Files.writeString(out, text, StandardCharsets.UTF_8);
            } else {
                PrintStream ps = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                ps.println(text);
            }
        } catch (IOException e) {
            System.err.println("Failed to write output: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage(PrintStream ps) {
        ps.println("lines-to-cover - список покрываемых строк BSL-исходников");
        ps.println("Usage: lines-to-cover --src <dir> [--src <dir>...] [--base <dir>] [--out <file>] [--format json|generic]");
    }
}
