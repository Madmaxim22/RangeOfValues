package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        final ExecutorService threadPool = Executors.newFixedThreadPool(4);
        List<Future<Integer>> threads = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            final Future<Integer> task = threadPool.submit(() -> getMaxSize(text));
            threads.add(task);
        }


        int maxSize = 0;
        for (Future<Integer> thread : threads) {
            final int result = thread.get();
            if(maxSize < result) maxSize = result;
        }
        threadPool.shutdown();
        System.out.println("Максимальный интервал значений среди всех строк:" + maxSize);

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static Integer getMaxSize(String text) {
        int maxSize = 0;
        Matcher m = Pattern.compile("(.)\\1+").matcher(text);
        while (m.find()) {
            String sub = m.group();
            if (sub.length() > maxSize) {
                maxSize = sub.length();
            }
        }
        return maxSize;
    }

    /*
        public static Integer getMaxSize(String text) {
        int maxSize = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < text.length(); j++) {
                if (i >= j) {
                    continue;
                }
                boolean bFound = false;
                for (int k = i; k < j; k++) {
                    if (text.charAt(k) == 'b') {
                        bFound = true;
                        break;
                    }
                }
                if (!bFound && maxSize < j - i) {
                    maxSize = j - i;
                }
            }
        }
        return maxSize;
    }
     */

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}