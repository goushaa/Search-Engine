package engine;

import org.jsoup.Jsoup;
import org.jsoup.internal.Normalizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CrawlerMain {

    public static void main(String[] args) throws InterruptedException {

        Queue<String> linkQ = new LinkedList<>();
        HashSet<String> visited = new HashSet<String>();

        try {
            File Links = new File("E:\\CUFE\\2ndYear\\2ndTerm\\APT\\Project\\Search-Engine\\engine\\links.txt");
            Scanner reader = new Scanner(Links);
            while (reader.hasNextLine()) {
                linkQ.add(reader.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("error in reading initial links\nprogram will terminate");
            return;
        }

        int cnt = 0, numThread = 0;

        Scanner scan = new Scanner(System.in);
        numThread = 1;
        numThread = Math.min(linkQ.size(), numThread);

        Thread[] threads = new Thread[numThread];
        System.out.println("starting");
        long startTime = System.nanoTime();
        for (int i = 0; i < numThread; i++) {
            threads[i] = new Thread(new Crawler(linkQ.remove(), visited));
            threads[i].setName(Integer.toString(i+1));
            threads[i].start();
        }
        for (int i = 0; i < numThread; i++) {
            threads[i].join();
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        System.out.println("elapsed time is "+elapsedTime/1e9);
        System.out.println("visited size is  "+visited.size());
        for (String s:
             visited) {
            System.out.println(s);
        }

    }

}
