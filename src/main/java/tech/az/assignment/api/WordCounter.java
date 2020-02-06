package tech.az.assignment.api;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface WordCounter {
    CompletableFuture<Void> start(String[] args) throws IOException;
}
