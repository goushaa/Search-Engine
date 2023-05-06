import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Phrase_Searching
{
    private static List<String> Unique_Docs;
    private static String[] Words;
    private static List<String> Docs_With_Phrase;

    public Phrase_Searching(String Phrase, HashMap<String, HashMap<String, List<Integer>>> Inverted_Index) {
        Unique_Docs = new ArrayList<>();
        Docs_With_Phrase = new ArrayList<>();
        Words = Phrase.split("\\s+"); // split the search phrase into individual words
        HashMap<String, Integer> Docs = new HashMap<>();

        for (String word : Words) {
            if (Inverted_Index.containsKey(word)) {
                HashMap<String, List<Integer>> WordIndex = Inverted_Index.get(word);
                for (Map.Entry<String, List<Integer>> entry : WordIndex.entrySet()) {
                    if (!Docs.containsKey(entry.getKey()))
                        Docs.put(entry.getKey(), 1);
                    else
                        Docs.put(entry.getKey(), Docs.get(entry.getKey()) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : Docs.entrySet()) {
            if (entry.getValue() == Words.length)
                Unique_Docs.add(entry.getKey());
        }
    }




    public List<String> Phrase_Search(HashMap<String, HashMap<String, List<Integer>>> Inverted_Index)
    {
        for (int i = 0; i < Unique_Docs.size(); i++)   //loop on docs
        {
            List<List<Integer>> Positions=new ArrayList<>();
            for (String word : Words) {
                if (Inverted_Index.containsKey(word)) {
                    List<Integer> list = Inverted_Index.get(word).get(Unique_Docs.get(i));
                    Positions.add(list);
                }
            }
            for (int z = 0; z < Positions.get(0).size(); z++)   //loop on positions of first word
            {
                // check if the subsequent words in the search phrase occur immediately after the first word in the document
                boolean match = true;
                for (int j = 1; j < Positions.size(); j++) {
                    boolean found = false;
                    for (int k = 1; k < Positions.get(j).size(); k++) {
                        if (Positions.get(j).get(k) == Positions.get(0).get(z) + j) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    Docs_With_Phrase.add(Unique_Docs.get(i));
                    break;
                }
            }
        }
        return Docs_With_Phrase;
    }
}


