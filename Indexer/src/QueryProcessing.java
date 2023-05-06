import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryProcessing extends Indexer {
   List<String> Get_Docs_of_Query(String query) {
        try {
            Read_StopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> Words;

        Words = Extract_Words_from_Page(query);

        for (int i=0; i<Words.size(); i++)
            Words.set(i, Words.get(i).toLowerCase());


        Words.removeAll(Stop_Words);

        List<String> stemmedWords = Stemming(Words);
        List<String> Docs=new ArrayList<>();
        for (String word :stemmedWords)
        {
            if (Inverted_Index.containsKey(word))
            {
                HashMap<String, List<Integer>> HashDocs = Inverted_Index.get(word);
                for (Map.Entry<String, List<Integer>> entry : HashDocs.entrySet())
                {
                    Docs.add(entry.getKey());
                }
            }
        }
        return Docs;
        }

}
