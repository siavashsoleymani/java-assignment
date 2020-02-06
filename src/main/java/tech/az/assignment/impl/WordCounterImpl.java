package tech.az.assignment.impl;


import tech.az.assignment.api.WordCounter;
import tech.az.assignment.common.Validations;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

public class WordCounterImpl implements WordCounter {
    private final ConcurrentMap<String, Integer> wordCounts = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Void> start(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        Validations.validateArguments(args);
        Validations.validateInputFilePaths(args, args[2]);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletableFuture<Map<String, Integer>> first = CompletableFuture.supplyAsync(startProcess(args[0], args[2]), executorService);
        CompletableFuture<Map<String, Integer>> second = CompletableFuture.supplyAsync(startProcess(args[1], args[2]), executorService);
        return first.thenCombine(second, getResultsList())
                .thenAcceptAsync(printResultsOrderByOccurrencesDescending(), executorService)
                .thenRun(() -> System.out.println(String.format("\n\nApplication ends in %d milliseconds", System.currentTimeMillis() - start)))
                .thenRun(executorService::shutdown);

    }


    private Supplier<Map<String, Integer>> startProcess(String filePath, String encoding) {
        return () -> {
            try {
                File file = new File(filePath);
                try (InputStream in = new FileInputStream(file);
                     Reader reader = new InputStreamReader(in, encoding);
                     Reader buffer = new BufferedReader(reader)) {
                    return getWordsCount(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private Map<String, Integer> getWordsCount(Reader reader)
            throws IOException {
        System.out.println(String.format("File scanner starting at thread: %s", Thread.currentThread().getName()));
        int r;
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Integer> localWordsCount = new HashMap<>();
        while ((r = reader.read()) != -1) {
            char character = (char) r;
            if (isNotReachingEndOfWord(character)) {
                stringBuilder.append(character);
            } else {
                String word = stringBuilder.toString();
                if (word.isEmpty())
                    continue;
                wordCounts.merge(stringBuilder.toString(), 1, (integer, integer2) -> integer + 1);
                localWordsCount.merge(stringBuilder.toString(), 1, (integer, integer2) -> integer + 1);
                stringBuilder.setLength(0);
            }
        }
        return localWordsCount;
    }

    private BiFunction<Map<String, Integer>, Map<String, Integer>, List<Map<String, Integer>>> getResultsList() {
        return (firstResult, secondResult) -> {
            List<Map<String, Integer>> results = new ArrayList<>();
            results.add(firstResult);
            results.add(secondResult);
            return results;
        };
    }

    private Consumer<List<Map<String, Integer>>> printResultsOrderByOccurrencesDescending() {
        return maps -> {
            System.out.println(String.format("Printer starting at thread: %s \n\n", Thread.currentThread().getName()));
            Map<String, Integer> firstResult = maps.get(0);
            Map<String, Integer> secondResult = maps.get(1);
            wordCounts
                    .entrySet()
                    .parallelStream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new))
                    .forEach((k, v) -> System.out.println(String.format("%s %d = %d  occurrences in file1 + %d occurrences in file2",
                            k,
                            v,
                            (Integer) (Objects.nonNull(firstResult.get(k)) ? firstResult.get(k) : 0),
                            (Integer) (Objects.nonNull(secondResult.get(k)) ? secondResult.get(k) : 0))));
        };
    }


    private static boolean isNotReachingEndOfWord(char c) {
        return c != ' '
                && c != '\n'
                && c != '\t'
                && c != '.'
                && c != ','
                && c != ';'
                && c != '!'
                && c != '"'
                && c != '\''
                && c != '?';
    }

    public ConcurrentMap<String, Integer> getWordCounts() {
        return new ConcurrentHashMap<>(wordCounts);
    }
}
