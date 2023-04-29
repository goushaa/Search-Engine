package engine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class test {
    public static void main(String args[]) {
        isDisallowedForUserAgent();
    }

    public static boolean isDisallowedForUserAgent() {
        try {
            System.out.println("hi");
            Connection connect = Jsoup.connect("https://www.google.com/robots.txt");
            Document doc = connect.get();
            Elements elements = doc.select("ser-agent: *");
            System.out.println("hi");
            for (org.jsoup.nodes.Element element : elements) {
                element = element.nextElementSibling();
                System.out.println(element);
                if (element != null && element.tagName().equals("disallow")) {
                    System.out.println(element);
                }
            }
        } catch (IOException e) {
            // Handle exceptions
        }

        return false;
    }
}
