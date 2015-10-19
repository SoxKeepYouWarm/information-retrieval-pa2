import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class runner {


    public static void index_term(Map<String, Term_data> index, String line){
        //System.out.println("current line is : " + line);
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

        String output = "Result: ";

        for (int i = 0; i < k; i++){
            //System.out.println(sorted_list.get(i).getTerm() + " " + sorted_list.get(i).getPosting_list_size());
            output += sorted_list.get(i).getTerm() + ", ";
        }

        System.out.println("FUNCTION: getTopK " + k);
        System.out.println(output);

    }

    public static void getPostings(Map<String, Term_data> index, String query_term){

        System.out.println("FUNCTION: getPostings " + query_term);

        Term_data query_results = index.get(query_term);

        List<Tuple> posting_list = query_results.getPosting_list();

        String getPostings_out = "Ordered by docID's: ";
        for (Tuple tup : posting_list){
            getPostings_out += tup.doc_id + ", ";
        }
        System.out.println(getPostings_out);


        List<Tuple> posting_list_copy = new LinkedList<>();
        for (Tuple tup : posting_list){
            Tuple new_tup = new Tuple(tup.doc_id, tup.frequency);
            posting_list_copy.add(new_tup);
        }

        Collections.sort(posting_list_copy);
        getPostings_out = "Ordered by TF: ";
        for (Tuple tup : posting_list_copy){
            getPostings_out += tup.doc_id + ", ";
        }
        System.out.println(getPostings_out);

    }


    public static void main(String [] args){

        String index_fileName = args[0];
        String log_fileName = args[1];
        int k_num = Integer.parseInt(args[2]);
        String input_fileName = args[3];

        Map<String, Term_data> index = new HashMap<>();

        try{

            FileReader reader = new FileReader(index_fileName);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                index_term(index, line);
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        // all terms have been indexed

        get_top_k(index, k_num);

        // read sample input
        try{
            //String file_name = "sample_input.txt";
            FileReader reader = new FileReader(input_fileName);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] query_terms = line.split(" ");
                List<String> stripped_query_terms = new ArrayList<>(query_terms.length);
                for (String term : query_terms){
                    //System.out.println("Current term is " + term);
                    Term_data query_results = index.get(term);
                    if (query_results == null){
                        System.out.println(term + " not found");
                    } else{
                        getPostings(index, term);
                        stripped_query_terms.add(term);
                    }
                }

                String[] terms = stripped_query_terms.toArray(new String[stripped_query_terms.size()]);

                // Term at a time AND
                long start = System.nanoTime();
                taat_functions.termAtATimeAND(index, terms);
                long elapsedTime = System.nanoTime() - start;
                System.out.println(elapsedTime + " nano seconds are used");

                // Term at a time OR
                start = System.nanoTime();
                taat_functions.termAtATimeOR(index, terms);
                elapsedTime = System.nanoTime() - start;
                System.out.println(elapsedTime + " nano seconds are used");

                // Doc at a time AND
                start = System.nanoTime();
                daat_functions.docAtATimeAND(index, terms);
                elapsedTime = System.nanoTime() - start;
                System.out.println(elapsedTime + " nano seconds are used");

                // Doc at a time OR
                start = System.nanoTime();
                daat_functions.docAtATimeOR(index, terms);
                elapsedTime = System.nanoTime() - start;
                System.out.println(elapsedTime + " nano seconds are used");

            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
