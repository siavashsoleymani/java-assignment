package tech.az.assignment;

import org.junit.Test;
import tech.az.assignment.impl.WordCounterImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class WordCounterTest {

    @Test
    public void countWordsFromInvalidFilePath() {
        String filePath = "src/main/resources/empty12.txt";
        String encoding = "UTF-8";
        WordCounterImpl wordCounter = new WordCounterImpl();
        assertThrows(FileNotFoundException.class,
                () -> wordCounter.start(new String[]{filePath, filePath, encoding}).join());
    }

    @Test
    public void countWordsFromEmptyFile() throws IOException {
        String filePath = "src/main/resources/empty.txt";
        String encoding = "UTF-8";
        WordCounterImpl wordCounter = new WordCounterImpl();
        wordCounter.start(new String[]{filePath, filePath, encoding}).join();
        assertEquals(0, wordCounter.getWordCounts().size());
    }

    @Test
    public void countWordsFromSameFiles() throws IOException {
        String filePath = "src/main/resources/one.txt";
        String encoding = "UTF-8";
        WordCounterImpl wordCounter = new WordCounterImpl();
        wordCounter.start(new String[]{filePath, filePath, encoding}).join();
        assertEquals(new Integer(896), wordCounter.getWordCounts().get("to"));
        assertEquals(new Integer(504), wordCounter.getWordCounts().get("pleasure"));
        assertEquals(new Integer(224), wordCounter.getWordCounts().get("voluptatem"));
    }

    @Test
    public void countWordsFromDifferentFiles() throws IOException {
        String firstFilePath = "src/main/resources/one.txt";
        String secondFilePath = "src/main/resources/two.txt";
        String encoding = "UTF-8";
        WordCounterImpl wordCounter = new WordCounterImpl();
        wordCounter.start(new String[]{secondFilePath, firstFilePath, encoding}).join();
        assertEquals(new Integer(6272), wordCounter.getWordCounts().get("to"));
        assertEquals(new Integer(3528), wordCounter.getWordCounts().get("pleasure"));
        assertEquals(new Integer(1568), wordCounter.getWordCounts().get("voluptatem"));
    }

    @Test
    public void countWordsFromDifferentFilesOneFileIsOneLineFile() throws IOException {
        String firstFilePath = "src/main/resources/two.txt";
        String secondFilePath = "src/main/resources/three.txt";
        String encoding = "UTF-8";
        WordCounterImpl wordCounter = new WordCounterImpl();
        wordCounter.start(new String[]{secondFilePath, firstFilePath, encoding}).join();
        assertEquals(new Integer(17472), wordCounter.getWordCounts().get("to"));
        assertEquals(new Integer(9828), wordCounter.getWordCounts().get("pleasure"));
        assertEquals(new Integer(4368), wordCounter.getWordCounts().get("voluptatem"));
    }

    @Test
    public void countWordsFromInvalidEncodingFormat() {
        String firstFilePath = "src/main/resources/two.txt";
        String secondFilePath = "src/main/resources/three.txt";
        String encoding = "ASCII";
        WordCounterImpl wordCounter = new WordCounterImpl();
        assertThrows(UnsupportedEncodingException.class,
                () -> wordCounter.start(new String[]{secondFilePath, firstFilePath, encoding}).join());
    }

    @Test
    public void countWordsFromInvalidArguments() {
        String firstFilePath = "src/main/resources/two.txt";
        String secondFilePath = "";
        String encoding = "UTF-8";
        WordCounterImpl wordCounter = new WordCounterImpl();
        assertThrows(IllegalArgumentException.class,
                () -> wordCounter.start(new String[]{secondFilePath, firstFilePath, encoding}).join());
    }


}
