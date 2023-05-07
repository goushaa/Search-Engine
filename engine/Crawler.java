package engine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.lang.model.element.ElementKind;
import javax.print.Doc;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Crawler implements Runnable {
    int c=0;
    static int cnt = 0;
    final HashSet<String> visited;
    Queue<String> linkQ;
    String name;

    Crawler(String _Link, HashSet<String> _visited) {
        visited = _visited;
        linkQ = new LinkedList<>();
        linkQ.add(_Link);
        synchronized (visited) {
            visited.add(_Link);
        }
        incrementCnt();
    }

    public static synchronized Boolean incrementCnt() {

        if (cnt >= 50)
            return false;
        cnt++;
        return true;
    }

    public boolean getLinks(Document doc) {
        for (Element link : doc.select("a[href]")) {
            String new_link = urlNormal.normalizeURL(link.absUrl("href"));
            synchronized (visited) {
                if (visited.contains(new_link))
                    continue;
            }
            if (!incrementCnt())
                return false;
            linkQ.add(new_link);
//            System.out.println("cnt is  "+ cnt+"  link is added by thread "+Thread.currentThread().getName()+" "+new_link);
            synchronized (visited) {
                visited.add(new_link);
            }
        }
        return true;
    }

    public boolean saveFile(Document doc) {
        String file = null;
        synchronized (visited) {
          file = "html/"+name+(c++)+ ".html";
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(doc.html());
            writer.close();
        }catch (IOException e){
            return false;
        }
        return true;
    }

    public void run() {
        name=Thread.currentThread().getName();
        boolean addLinks=true;
        while (!linkQ.isEmpty()) {
            Document doc = getDocument(linkQ.remove());
            if (doc == null) continue;
            saveFile(doc);
            if (addLinks &&!getLinks(doc))
                addLinks=false;
        }
    }

    private Document getDocument(String Link) {
        try {
            Connection connect = Jsoup.connect(Link);
            Document doc = connect.get();
            if (connect.response().statusCode() != 200) {
//                System.out.println("bad Connection in link" + Link);
                return null;
            }
//            System.out.println("good connection in link" + Link);
            return doc;
        } catch (IOException e) {
            return null;
        }

    }
}
