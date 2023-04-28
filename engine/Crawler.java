package engine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.print.Doc;
import java.io.IOException;
import java.util.Queue;

public class Crawler implements Runnable {

    Queue<String> linkQ;
    String Link;

    Crawler(String _Link, Queue<String> Q) {
        Link = _Link;
        linkQ = Q;
    }

    public void run() {
        System.out.println(Link);
        Document doc= getDocument();
        if(doc==null)return;
        for(Element link :doc.select("a[href]")){
            String new_link = link.absUrl("href");
//            System.out.println("new link is"+new_link);
            linkQ.add(new_link);
        }
    }

    private Document getDocument() {
        try {
            Connection connect = Jsoup.connect(Link);
            Document doc = connect.get();
            if(connect.response().statusCode()!=200){
                System.out.println("bad Connection in link"+Link);
                return null;
            }
            System.out.println("good connection in link"+Link);
            return doc;
        } catch (IOException e) {
            return null;
        }

    }
}
