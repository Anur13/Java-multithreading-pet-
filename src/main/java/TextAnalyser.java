import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class TextAnalyser {
    private static int NUMBER_OF_THREADS = 3;
    private static int TOTAL_WORDS;

    private static final HashMap<String, TextResult> wordsMap = new HashMap<>();


    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        try {
            ClassLoader classLoader = TextAnalyser.class.getClassLoader();
            String TEXT = new String(Files.readAllBytes(Paths.get(classLoader.getResource("war_and_peace.txt").toURI())));
            List<String> listOfWords = List.of(TEXT.split(" "));
            TOTAL_WORDS = listOfWords.size();
            int wordsPerThread = Math.abs(TOTAL_WORDS / NUMBER_OF_THREADS);
            List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                Thread thread = new Thread(new FileHandler(i * wordsPerThread, wordsPerThread * (i + 1), listOfWords));
                threadList.add(thread);
                thread.start();
            }
            for (Thread thread : threadList) {
                thread.join();
            }

        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        wordsMap.forEach((s, textResult) -> {
            float percent = (float) textResult.wordCount / (float) TOTAL_WORDS;
            textResult.setWordPercentage(Float.parseFloat(new DecimalFormat("#.####").format(percent * 1000)));
        });
        System.out.println(wordsMap);

        System.out.println(TOTAL_WORDS);
        System.out.println(System.currentTimeMillis() - start);
    }


    static class FileHandler implements Runnable {
        int start;
        int finish;
        List<String> listOfWords;

        public FileHandler(int start, int finish, List<String> listOfWords) {
            this.start = start;
            this.finish = finish;
            this.listOfWords = listOfWords;
        }

        @Override
        public void run() {
            for (int i = start; i < finish - 1; i++) {
                String word = listOfWords.get(i);
                TextResult res = wordsMap.get(word);
                if (Objects.isNull(res)) {
                    wordsMap.put(word, new TextResult(1));
                } else {
                    res.setWordCount(res.getWordCount() + 1);
                }
            }
        }
    }

    static class TextResult {
        int wordCount;
        float wordPercentage;

        public TextResult(int wordCount) {
            this.wordCount = wordCount;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public float getWordPercentage() {
            return wordPercentage;
        }

        public void setWordPercentage(float wordPercentage) {
            this.wordPercentage = wordPercentage;
        }

        @Override
        public String toString() {
            return "TextResult{" +
                    "wordCount=" + wordCount +
                    ", wordPercentage=" + wordPercentage +
                    '}';
        }
    }


}
