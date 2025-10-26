package by.it.group410972.masurenko.lesson15;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class SourceScannerA {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        Path srcPath = Paths.get(src);
        List<FileData> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(srcPath)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> {
                        try {
                            String text;
                            try {
                                text = Files.readString(p, StandardCharsets.UTF_8);
                            } catch (MalformedInputException e) {
                                return;
                            }
                            if (text.contains("@Test") || text.contains("org.junit.Test"))
                                return;
                            StringBuilder sb = new StringBuilder();
                            for (String line : text.split("\n")) {
                                if (line.startsWith("package ") || line.startsWith("import "))
                                    continue;
                                sb.append(line).append("\n");
                            }
                            String cleaned = trimLowChars(sb.toString());
                            int size = cleaned.getBytes(StandardCharsets.UTF_8).length;
                            String relPath = srcPath.relativize(p).toString();
                            files.add(new FileData(relPath, size));
                        } catch (IOException e) {
                            System.err.println("Ошибка при обработке файла: " + p);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        files.sort(Comparator.comparingInt(FileData::size)
                .thenComparing(FileData::path));
        for (FileData f : files) {
            System.out.println(f.size + " " + f.path);
        }
    }
    private static String trimLowChars(String s) {
        int start = 0, end = s.length();
        while (start < end && s.charAt(start) < 33) start++;
        while (end > start && s.charAt(end - 1) < 33) end--;
        return s.substring(start, end);
    }
    private record FileData(String path, int size) {}
}
