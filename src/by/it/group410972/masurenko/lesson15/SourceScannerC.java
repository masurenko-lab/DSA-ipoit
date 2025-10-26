package by.it.group410972.masurenko.lesson15;

import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class SourceScannerC {

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        try {
            List<Path> javaFiles = findJavaFiles(Paths.get(src));
            Map<Path, String> processedFiles = processFiles(javaFiles);
            findAndPrintDuplicates(processedFiles);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static List<Path> findJavaFiles(Path srcDir) throws IOException {
        List<Path> javaFiles = new ArrayList<>();

        if (!Files.exists(srcDir)) {
            return javaFiles;
        }

        Files.walkFileTree(srcDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".java")) {
                    javaFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return javaFiles;
    }

    private static Map<Path, String> processFiles(List<Path> javaFiles) {
        Map<Path, String> processedFiles = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();
        for (Path file : javaFiles) {
            futures.add(executor.submit(() -> {
                try {
                    if (!isTestFile(file)) {
                        String content = readFileContent(file);
                        String processed = processContent(content);
                        if (!processed.isEmpty()) {
                            processedFiles.put(file, processed);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка обработки файла " + file + ": " + e.getMessage());
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Ошибка выполнения: " + e.getMessage());
            }
        }

        executor.shutdown();
        return processedFiles;
    }

    private static boolean isTestFile(Path file) throws IOException {
        try {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            return content.contains("@Test") || content.contains("org.junit.Test");
        } catch (MalformedInputException e) {
            return tryReadWithFallbackEncoding(file, "@Test") ||
                    tryReadWithFallbackEncoding(file, "org.junit.Test");
        }
    }

    private static boolean tryReadWithFallbackEncoding(Path file, String searchString) {
        try {
            String content = new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1);
            return content.contains(searchString);
        } catch (IOException e) {
            return false;
        }
    }

    private static String readFileContent(Path file) throws IOException {
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            return new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1);
        }
    }

    private static String processContent(String content) {
        content = removePackageAndImports(content);
        content = removeComments(content);
        content = normalizeWhitespace(content);
        return content.trim();
    }

    private static String removePackageAndImports(String content) {
        String[] lines = content.split("\\r?\\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("package") &&
                    !trimmedLine.startsWith("import")) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

    private static String removeComments(String content) {
        StringBuilder result = new StringBuilder();
        int length = content.length();
        boolean inBlockComment = false;
        boolean inLineComment = false;
        boolean inString = false;
        boolean inChar = false;

        for (int i = 0; i < length; i++) {
            char current = content.charAt(i);
            char next = (i < length - 1) ? content.charAt(i + 1) : 0;

            if (inLineComment) {
                if (current == '\n') {
                    inLineComment = false;
                    result.append(current);
                }
                continue;
            }

            if (inBlockComment) {
                if (current == '*' && next == '/') {
                    inBlockComment = false;
                    i++;
                }
                continue;
            }

            if (inString) {
                result.append(current);
                if (current == '\\' && next == '"') {
                    result.append(next);
                    i++;
                } else if (current == '"') {
                    inString = false;
                }
                continue;
            }

            if (inChar) {
                result.append(current);
                if (current == '\\' && next == '\'') {
                    result.append(next);
                    i++;
                } else if (current == '\'') {
                    inChar = false;
                }
                continue;
            }

            if (current == '"') {
                inString = true;
                result.append(current);
            } else if (current == '\'') {
                inChar = true;
                result.append(current);
            } else if (current == '/' && next == '/') {
                inLineComment = true;
                i++;
            } else if (current == '/' && next == '*') {
                inBlockComment = true;
                i++;
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }

    private static String normalizeWhitespace(String content) {
        StringBuilder result = new StringBuilder();
        boolean lastWasWhitespace = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c < 33) {
                if (!lastWasWhitespace) {
                    result.append(' ');
                    lastWasWhitespace = true;
                }
            } else {
                result.append(c);
                lastWasWhitespace = false;
            }
        }

        return result.toString();
    }

    private static void findAndPrintDuplicates(Map<Path, String> processedFiles) {
        List<Path> sortedPaths = new ArrayList<>(processedFiles.keySet());
        Collections.sort(sortedPaths);

        Set<Path> alreadyProcessed = new HashSet<>();

        for (Path file1 : sortedPaths) {
            if (alreadyProcessed.contains(file1)) {
                continue;
            }

            List<Path> duplicates = new ArrayList<>();
            String content1 = processedFiles.get(file1);

            for (Path file2 : sortedPaths) {
                if (file1.equals(file2) || alreadyProcessed.contains(file2)) {
                    continue;
                }

                String content2 = processedFiles.get(file2);
                int distance = levenshteinDistance(content1, content2);

                if (distance < 10) {
                    duplicates.add(file2);
                }
            }

            if (!duplicates.isEmpty()) {
                System.out.println(file1);
                for (Path duplicate : duplicates) {
                    System.out.println(duplicate);
                    alreadyProcessed.add(duplicate);
                }
                System.out.println();
            }
        }
    }

    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        if (len1 == 0) return len2;
        if (len2 == 0) return len1;

        if (Math.abs(len1 - len2) >= 10) {
            return Math.max(len1, len2);
        }

        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];

        for (int j = 0; j <= len2; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            curr[0] = i;

            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(
                        Math.min(curr[j - 1] + 1, prev[j] + 1),
                        prev[j - 1] + cost
                );

                // Ранний выход если расстояние уже слишком большое
                if (curr[j] >= 10 && j < len2) {
                    curr[j] = 10;
                    break;
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[len2];
    }
}