package by.it.group410972.masurenko.lesson15;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class SourceScannerB {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        Path srcPath = Paths.get(src);
        List<FileResult> results = new ArrayList<>();
        try {
            Files.walk(srcPath)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> {
                        try {
                            String text;
                            try {
                                text = Files.readString(p, StandardCharsets.UTF_8);
                            } catch (MalformedInputException e) {
                                text = Files.readString(p, StandardCharsets.ISO_8859_1);
                            }
                            if (text.contains("@Test") || text.contains("org.junit.Test")) return;
                            text = removePackagesAndImports(text);
                            text = removeComments(text);
                            text = trimControlChars(text);
                            text = removeEmptyLines(text);
                            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                            results.add(new FileResult(
                                    srcPath.relativize(p).toString(),
                                    bytes.length
                            ));
                        } catch (IOException e) {
                            System.err.println("Ошибка при чтении файла: " + p + " (" + e.getMessage() + ")");
                        }
                    });
            results.sort(Comparator.comparingLong(FileResult::size)
                    .thenComparing(FileResult::path));
            for (FileResult r : results) {
                System.out.printf("%d %s%n", r.size(), r.path());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String removePackagesAndImports(String text) {
        StringBuilder sb = new StringBuilder();
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("package ") && !trimmed.startsWith("import ")) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private static String removeComments(String text) {
        StringBuilder sb = new StringBuilder();
        boolean inBlock = false;
        for (int i = 0; i < text.length(); i++) {
            if (!inBlock && i + 1 < text.length() && text.charAt(i) == '/' && text.charAt(i + 1) == '*') {
                inBlock = true;
                i++;
            } else if (inBlock && i + 1 < text.length() && text.charAt(i) == '*' && text.charAt(i + 1) == '/') {
                inBlock = false;
                i++;
            } else if (!inBlock && i + 1 < text.length() && text.charAt(i) == '/' && text.charAt(i + 1) == '/') {
                while (i < text.length() && text.charAt(i) != '\n') i++;
            } else if (!inBlock) {
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    private static String trimControlChars(String text) {
        int start = 0;
        int end = text.length() - 1;

        while (start < text.length() && text.charAt(start) < 33) start++;
        while (end >= start && text.charAt(end) < 33) end--;

        if (start > end) return "";
        return text.substring(start, end + 1);
    }
    private static String removeEmptyLines(String text) {
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\n")) {
            if (!line.trim().isEmpty()) sb.append(line).append("\n");
        }
        return sb.toString();
    }
    private record FileResult(String path, long size) {}
}
