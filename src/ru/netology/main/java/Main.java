package ru.netology.main.java;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static ArrayBlockingQueue<String> blockingQueueA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> blockingQueueB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> blockingQueueC = new ArrayBlockingQueue<>(100);


    public static void main(String[] args) throws InterruptedException {

        Thread textGenerator = new Thread(() ->
        {
            for (int i = 0; i < 10000; i++) {
                String text = generateText("abc", 100000);
                try {
                    blockingQueueA.put(text);
                    blockingQueueB.put(text);
                    blockingQueueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        }
        );
        textGenerator.start();

        Thread a = getThread(blockingQueueA, 'a');
        Thread b = getThread(blockingQueueB, 'b');
        Thread c = getThread(blockingQueueC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = findMaxCharCount(queue, letter);
            System.out.println("Максимальное количество букв " + letter + " составляет: " + max);
        });
    }

    public static int findMaxCharCount(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10000; i++) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + "was interrupted");
            return -1;
        }
        return max;
    }
}