
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
        if (query_results.get_Posting_List_size() == 0){
            System.out.println("term not found");
            return;
        }

        List<Tuple> posting_list = query_results.getPosting_list();

        String getPostings_out = "Ordered by docID's: ";
        for (Tuple tup : posting_list){
            getPostings_out += tup.doc_id + ", ";
        }
        System.out.println(getPostings_out);

        Collections.sort(posting_list);
        getPostings_out = "Ordered by TF: ";
        for (Tuple tup : posting_list){
            getPostings_out += tup.doc_id + ", ";
        }
        System.out.println(getPostings_out);

    }


    public static void termAtATimeAND(Map<String, Term_data> index, String[] query_terms){

        List<Integer> resulting_docs = new ArrayList<>();

        int target_score = query_terms.length;
        Map<Integer, Integer> doc_scores = new HashMap<>();

        for (String term : query_terms){
            List<Tuple> doc_list = index.get(term).getPosting_list();

            for (Tuple tup : doc_list){
                if (doc_scores.containsKey(tup.doc_id)){
                    int current_score = doc_scores.get(tup.doc_id);
                    doc_scores.put(tup.doc_id, (current_score + 1));
                } else {
                    doc_scores.put(tup.doc_id, 1);
                }
            }
        }

        for (int key : doc_scores.keySet()){
            if (doc_scores.get(key) == target_score){
                resulting_docs.add(key);
            }
        }

        int num_of_docs = resulting_docs.size();

        String function_output = "FUNCTION: TermAtATimeAnd ";
        for (String term : query_terms){
            function_output += term + " ";
        }
        System.out.println(function_output);

        function_output = "docID's: ";
        for (int id : resulting_docs){
            function_output += id + " ";
        }
        System.out.println(function_output);

        System.out.println(num_of_docs + " documents are found");
    }


    public static void termAtATimeOR(Map<String, Term_data> index, String[] query_terms) {

        List<Integer> resulting_docs = new ArrayList<>();

        int target_score = query_terms.length;
        Map<Integer, Integer> doc_scores = new HashMap<>();

        for (String term : query_terms) {
            List<Tuple> doc_list = index.get(term).getPosting_list();

            for (Tuple tup : doc_list) {
                if (doc_scores.containsKey(tup.doc_id)) {
                    // do nothing
                } else {
                    doc_scores.put(tup.doc_id, 1);
                }
            }
        }


        for (int key : doc_scores.keySet()) {
            resulting_docs.add(key);
        }


        int num_of_docs = resulting_docs.size();

        String function_output = "FUNCTION: TermAtATimeOr ";
        for (String term : query_terms) {
            function_output += term + " ";
        }
        System.out.println(function_output);

        function_output = "docID's: ";
        for (int id : resulting_docs) {
            function_output += id + " ";
        }
        System.out.println(function_output);

        System.out.println(num_of_docs + " documents are found");

    }


    private static boolean continue_scanning_helper(List<ListIterator<Tuple>> list_pointers){
        boolean return_val = false;
        for (ListIterator<Tuple> iter : list_pointers){
            if (iter.hasNext()){ return_val = true; }
        }
        return return_val;
    }

    public static void docAtATimeAND(Map<String, Term_data> index, String[] query_terms){

        class docAtATime_data implements Comparable<docAtATime_data>{

            int index;
            int docId;

            public docAtATime_data(int index, int docId){
                this.index = index;
                this.docId = docId;
            }

            @Override
            public int compareTo(docAtATime_data other_data) {
                return other_data.docId - this.docId;
            }
        }

        List<List<Tuple>> relevant_lists = new LinkedList<>();

        for (String terms : query_terms){
            if (index.containsKey(terms)){
                relevant_lists.add(index.get(terms).getPosting_list());
            } else {
                System.out.println(terms + " not found");
            }
        }

        Map<Integer, Integer> doc_scores = new HashMap<>();

        List<ListIterator<Tuple>> list_pointers = new ArrayList<>(relevant_lists.size());

        for (int i = 0; i < relevant_lists.size(); i++){
            ListIterator<Tuple> iterator = relevant_lists.get(i).listIterator();
            list_pointers.add(i, iterator);
        }

        while (continue_scanning_helper(list_pointers)){

            List<docAtATime_data> current_postings = new LinkedList<>();
            // build sortable index docId pairs
            for (int i = 0; i < relevant_lists.size(); i++){
                ListIterator<Tuple> current_iter = list_pointers.get(i);
                Tuple current_tup = current_iter.next();
                current_iter.previous();
                docAtATime_data new_data = new docAtATime_data(i, current_tup.doc_id);
                current_postings.add(new_data);
            }

            // sort the collection
            Collections.sort(current_postings);

            // get objects with lowest docId's


            // if object list size equals target, add docId to results

            // increment pointers of respective indexes
        }

        Map<Integer, Integer> sorting_indexes_to_ids = new HashMap<>();
        for (int i = 0; i < list_pointers.size(); i++){
            sorting_indexes_to_ids.put(i, list_pointers.get(i).next().doc_id);
            list_pointers.get(i).previous();
        }

    }


    public static void docAtATimeOR(Map<String, Term_data> index, String[] query_terms){

    }


    public static void main(String [] args){

        String index_fileName = args[0];
        String log_fileName = args[1];
        int k_num = Integer.parseInt(args[2]);
        String input_fileName = args[3];

        Map<String, Term_data> index = new HashMap<>();

        try{
            //String file_name = "term.idx";

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
                for (String term : query_terms){
                    getPostings(index, term);
                }

                // Term at a time AND
                termAtATimeAND(index, query_terms);

                // Term at a time OR
                termAtATimeOR(index, query_terms);

                // Doc at a time AND
                docAtATimeAND(index, query_terms);

                // Doc at a time OR

            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
