import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.tartarus.snowball.ext.PorterStemmer;
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

       Indexer i =new Indexer();
       i.Indexing();

//       QueryProcessing q=new QueryProcessing();
//       List<String> d=q.Get_Docs_of_Query("javascript");
//
//       for (String st :d)
//           System.out.println(st);

    }
}