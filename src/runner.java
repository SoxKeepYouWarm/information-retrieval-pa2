
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class runner {


    public static void index_term(Map<String, Term_data> index, String line){
        System.out.println("current line is : " + line);
        String[] line_split = line.split("\\\\");

        Term_data term_data;
        List<Tuple> posting_list = new LinkedList<>();

        String term = line_split[0];
        int frequency = Integer.parseInt(line_split[1].substring(1));      // drops leading 'c'

        String[] posting_elements = line_split[2].substring(2, line_split[2].length() - 1).split(", ");
        for (String element : posting_elements){
            String[] element_split = element.split("/");
            Tuple posting = new Tuple(Integer.parseInt(element_split[0]), Integer.parseInt(element_split[1]));
            posting_list.add(posting);
        }

        term_data = new Term_data(frequency, posting_list);

        index.put(term, term_data);
    }


    public static void get_top_k(Map<String, Term_data> index, int k){

        Set<String> terms = index.keySet();
        Collection<Term_data> values = index.values();

        List<Top_K_Tuple> sorted_list = new LinkedList<>();

        for (Map.Entry<String, Term_data> entry : index.entrySet()){
            int posting_list_size = entry.getValue().get_Posting_List_size();
            String term = entry.getKey();

            Top_K_Tuple tup = new Top_K_Tuple(posting_list_size, term);
            sorted_list.add(tup);
        }

        Collections.sort(sorted_list);

        for (int i = 0; i < k; i++){
            System.out.println(sorted_list.get(i).getTerm() + " " + sorted_list.get(i).getPosting_list_size());
            //System.out.println(sorted_list.get(sorted_list.size() - 1 - i).getTerm());
            //System.out.println(sorted_list.get(sorted_list.size() - 1 - i).getPosting_list_size());
        }

    }


    public static void main(String [] args){

        Map<String, Term_data> index = new HashMap<>();


        try{
            String file_name = "term.idx";
            FileReader reader = new FileReader(file_name);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                index_term(index, line);
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        // all terms have been indexed

        get_top_k(index, 10);

        // read sample input
        try{
            String file_name = "sample_input.txt";
            FileReader reader = new FileReader(file_name);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                index_term(index, line);
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
