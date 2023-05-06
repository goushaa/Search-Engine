import java.security.Key;
import java.sql.SQLOutput;
import java.util.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.tartarus.snowball.ext.PorterStemmer;

public class Indexer implements Runnable {
    final int NumberOfThreads = 2;
    protected static HashMap<String, HashMap<String, List<Integer>>> Inverted_Index;

    protected static List<String> Stop_Words;

    private static String[] Html_List;
    private static String Root_Path;
    private static HashMap<String, HashMap<String, Double>> FileName_WordsScore;
    private static HashMap<String, Double> HTML_Tags;
    private static List<JSONObject> JSON_Inverted_Index;
    public void Indexing() throws InterruptedException , IOException {

        Inverted_Index = new HashMap<>();

        //initialize scores of each tag (ex: title-->3.0)
        Initialize_Tags();

        //read stopWords from text file
        Read_StopWords();

        //Read All html files from RootPath
        Read_Html_Files();

        //Define array of threads to make inverted index in parallel
        Thread[] Threads_Arr = new Thread[NumberOfThreads];

        for (int i = 0; i < NumberOfThreads; i++) {
            Threads_Arr[i] = new Thread(new Indexer());
            Threads_Arr[i].setName(String.valueOf(i));
        }
        for (int i = 0; i < NumberOfThreads; i++) {
            Threads_Arr[i].start();
        }
        //Wait all threads to join with final inverted index
        for (int i = 0; i < NumberOfThreads; i++) {
            Threads_Arr[i].join();
        }

        //convert the inverted index to json files to upload to the database
        JSON_Inverted_Index = Convert_InvertedIndex_to_JSONfile(Inverted_Index);
        //System.out.println(Inverted_Index);
        System.out.println(FileName_WordsScore);
    }

    @Override
    public void run() {
        int Thread_Num=Integer.parseInt(Thread.currentThread().getName());

        int start_index = Thread_Num * (Html_List.length / NumberOfThreads);
        int end_index = (Thread_Num + 1) * (Html_List.length / NumberOfThreads);

        //special case if number of files are not divisible by number of thread
        //the last thread takes the rest of files
        if (Thread_Num == NumberOfThreads - 1 && end_index != Html_List.length)
            end_index = Html_List.length;

        //loop on all html files , each thread takes its segment of files
        for (int i = start_index; i < end_index; i++) {

            //Reading the content of html file as string "plain text"
            StringBuilder HtmlFile_As_String = new StringBuilder("");

            try {
                BufferedReader reader = new BufferedReader(new FileReader(Root_Path + Html_List[i]));
                String line = "";
                StringBuilder temp = new StringBuilder("");
                while ((line = reader.readLine()) != null) {
                    temp.append(line);
                }
                reader.close();

                org.jsoup.nodes.Document Parsed_HTML = Jsoup.parse(temp.toString());
                HtmlFile_As_String.append(Parsed_HTML.body().text());

                //Add score of each word (maybe used in ranker)
                Add_Scores_of_Tags(Parsed_HTML, Html_List[i]);

            } catch (IOException e) {
            }

            //Array of words in each document
            List<String> Words;

            //Extract words from the content of the page
            Words = Extract_Words_from_Page(HtmlFile_As_String.toString());

            //remove stopWords
            Words.removeAll(Stop_Words);

            //stemming
            Words=Stemming(Words);

            //Make inverted index for the Words, Each thread makes part of this inverted index from the segment of files it has
            synchronized (this) {

                HashMap<String, List<Integer>> Word_Docs;
                int TF;
                for (int j = 0; j < Words.size(); j++)
                {
                    if (!Inverted_Index.containsKey(Words.get(j)))
                    {
                        Word_Docs = new HashMap<String, List<Integer>>();
                        Inverted_Index.put(Words.get(j), Word_Docs);
                    }
                    else
                    {
                        Word_Docs = Inverted_Index.get(Words.get(j));
                    }
                    if (!Word_Docs.containsKey(Html_List[i]))
                    {
                        TF = 1;
                        Word_Docs.put(Html_List[i], new ArrayList<Integer>(Arrays.asList(TF, j)));
                    }
                    else
                    {
                        TF = Word_Docs.get(Html_List[i]).get(0) + 1;
                        Word_Docs.get(Html_List[i]).remove(0);
                        Word_Docs.get(Html_List[i]).add(0, TF);
                        Word_Docs.get(Html_List[i]).add(j);
                    }
                }
            }
        }


    }

    private static List<JSONObject> Convert_InvertedIndex_to_JSONfile(HashMap<String, HashMap<String, List<Integer>>> inverted_index)
    {
        List<JSONObject> Word_JSONS_List = new Vector<>();

        for (String word : inverted_index.keySet()) {

            JSONObject word_JSON = new JSONObject();
            word_JSON.put("word", word);
            List<JSONObject> Docs_JSON_List = new Vector<>();

            for (String doc : inverted_index.get(word).keySet()) {
                JSONObject Doc_JSON = new JSONObject();
                List<Integer> Index=inverted_index.get(word).get(doc);
               // Double score=FileName_WordsScore.get(doc).get(word);
                Doc_JSON.put("Document", doc);
                Doc_JSON.put("TF", Index.get(0));
               // Doc_JSON.put("Score", score);
                //JSONArray arrayJSON= (JSONArray) (Index.subList(1,Index.size()));
                Doc_JSON.put("Indexes", Index.subList(1,Index.size()));

                Docs_JSON_List.add(Doc_JSON);
            }
            Word_JSONS_List.add(word_JSON);
            word_JSON.put("documents", Docs_JSON_List);
        }
        return Word_JSONS_List;
    }

    private static void Initialize_Tags()
    {
        HTML_Tags = new HashMap<String, Double>();
        HTML_Tags.put("title", 3.0);
        HTML_Tags.put("h1", 0.9);
        HTML_Tags.put("a", 0.8);
        HTML_Tags.put("h2", 0.7);
        HTML_Tags.put("h3", 0.6);
        HTML_Tags.put("h4", 0.5);
        HTML_Tags.put("h5", 0.4);
        HTML_Tags.put("h6", 0.3);
        HTML_Tags.put("p", 0.1);
    }

    private static void Add_Scores_of_Tags(org.jsoup.nodes.Document html, String fileName) throws IOException {
        PorterStemmer stemmer = new PorterStemmer();
        Pattern pattern = Pattern.compile("\\w+");
        HashMap<String, Double> temp = new HashMap<>();
        FileName_WordsScore = new HashMap<>();
        //filtration most important tags
        for (String line : HTML_Tags.keySet()) {
            String HTML_Line = html.select(line).text();

            if (html != null && !HTML_Line.isEmpty()) {
                Matcher matcher = pattern.matcher(HTML_Line.toLowerCase());
                while (matcher.find()) {
                    stemmer.setCurrent(matcher.group());
                    stemmer.stem();
                    HTML_Line = stemmer.getCurrent();

                    if (!temp.containsKey(HTML_Line))
                        temp.put(HTML_Line, HTML_Tags.get(line));
                    else
                        temp.put(HTML_Line, temp.get(HTML_Line) + HTML_Tags.get(line));
                }
            }
        }
        FileName_WordsScore.put(fileName, temp);
    }
    public void Read_StopWords() throws IOException
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Stop_Words.txt"));
            Stop_Words = new Vector<String>();
            String word;
            while ((word = reader.readLine()) != null) {
                Stop_Words.add(word);
            }
        } catch (IOException e) {
        }
    }
    public void Read_Html_Files() throws IOException
    {
        File file = new File("rootFolder");
        Root_Path = "rootFolder//";
        Html_List = file.list();
    }
    public List<String> Extract_Words_from_Page(String HtmlFile_As_String)
    {
        List<String>Words=new ArrayList<>();

        Pattern pattern = Pattern.compile("\\w+");
        Matcher match = pattern.matcher(HtmlFile_As_String);

        while (match.find()) {
            String word = match.group();
            word = word.toLowerCase();
            Words.add(word);
        }
        return Words;
    }
    public List<String> Stemming (List<String> Words)
    {
        PorterStemmer stemmer = new PorterStemmer();
        for (int j = 0; j < Words.size(); j++)
        {
            stemmer.setCurrent(Words.get(j));
            stemmer.stem();
            Words.set(j, stemmer.getCurrent());
        }
        return Words;
    }

}
