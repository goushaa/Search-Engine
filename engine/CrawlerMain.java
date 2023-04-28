package engine;

import org.jsoup.Jsoup;
import org.jsoup.internal.Normalizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

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


        int cnt=0;
        Normalizer norm = new Normalizer();

        while(cnt++<100){
            if(linkQ.isEmpty()){
                System.out.println("empty");
            }
            while(visited.contains(linkQ.peek())){
                linkQ.remove();
            }
            String top=linkQ.remove();
            visited.add(top);
            Thread t = new Thread(new Crawler(top,linkQ));
            t.start();
            t.join();
        }

    }

}
