
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


    private static boolean continue_scanning_helper(List<postingList_pointer> list_pointers){
        boolean continue_search = false;

        for (int i = 0; i < list_pointers.size(); i++){
            if (list_pointers.get(i).hasNext()){
                continue_search = true;
            }
        }

        return continue_search;
    }

    public static boolean remove_finished_pointers(List<postingList_pointer> list_pointers){
        //List<Integer> pointers_to_remove = new ArrayList<>(list_pointers.size());
        List<postingList_pointer> remove_pointers = new LinkedList<>();
        for (int i = 0; i < list_pointers.size(); i++){
            if (! list_pointers.get(i).hasNext()){
                remove_pointers.add(list_pointers.get(i));
                //pointers_to_remove.add(i);
            }
        }

        for (postingList_pointer remove_pointer : remove_pointers){
            //list_pointers.remove(remove_index);
            list_pointers.remove(remove_pointer);
        }

        if (remove_pointers.size() != 0){
            return true;
        } else {
            return false;
        }
    }


    static class postingList_pointer implements Comparable<postingList_pointer> {
        List<Tuple> posting_list;
        int index;
        String term;

        public postingList_pointer(List<Tuple> posting_list, String term){
            this.posting_list = posting_list;
            this.term = term;
            index = 0;
        }

        public Tuple get_current(){
            return posting_list.get(index);
        }

        public void next(){
            index ++;
        }

        public boolean hasNext(){
            return (index + 1) < posting_list.size();
        }

        @Override
        public int compareTo(postingList_pointer other_pointer) {
            return this.posting_list.get(index).doc_id - other_pointer.posting_list.get(other_pointer.index).doc_id;
        }
    }


    public static void docAtATimeAND(Map<String, Term_data> index, String[] query_terms){

        Map<Integer, Integer> doc_scores = new HashMap<>();         // holds results

        List<postingList_pointer> postingList_pointers = new LinkedList<>();        // #
        //List<List<Tuple>> query_posting_lists = new LinkedList<>();

        for (String terms : query_terms){           // initialize postingList pointers
            if (index.containsKey(terms)){
                postingList_pointer pointer = new postingList_pointer(index.get(terms).getPosting_list(), terms); // #
                postingList_pointers.add(pointer);                                                          // #
                //query_posting_lists.add(index.get(terms).getPosting_list());
            } else {
                System.out.println(terms + " not found");
            }
        }

        while (continue_scanning_helper(postingList_pointers)){
            //System.out.println("DEBUG: docscores is currently " + doc_scores.size());

            Collections.sort(postingList_pointers);

            // LOG CURRENT POINTERS
            //System.out.println("POINTER STATUS");
            //for (postingList_pointer pointer : postingList_pointers){
            //    System.out.println(pointer.term + " " + pointer.get_current().doc_id);
            //}



            int temp_lowest_id = -1;
            int temp_current_num_of_pointers = 0;
            for (Iterator<postingList_pointer> postingList_iter = postingList_pointers.iterator(); postingList_iter.hasNext();){
                postingList_pointer pointer = postingList_iter.next();

                if (temp_lowest_id == -1){              // if lowest id hasn't been set yet
                    temp_lowest_id = pointer.get_current().doc_id;

                    // this is a lowest pointer
                    temp_current_num_of_pointers ++;
                    if (pointer.hasNext()){
                        pointer.next();
                    } else {
                        postingList_iter.remove();
                    }
                } else {            // lowest id already set
                    if (pointer.get_current().doc_id == temp_lowest_id){
                        // this is another lowest pointer
                        temp_current_num_of_pointers ++;
                        if (pointer.hasNext()){
                            pointer.next();
                        } else {
                            postingList_iter.remove();
                        }
                    } else {
                        // this is not a lowest pointer
                        break;
                    }
                }

            }

            doc_scores.put(temp_lowest_id, temp_current_num_of_pointers);
            if (remove_finished_pointers(postingList_pointers)){
                break;
            }
        }

        String and_output = "FUNCTION: DocAtATimeQueryAnd ";
        //String or_output = "FUNCTION: DocAtATimeQueryOr ";
        for (String term : query_terms){
            and_output += term + " ";
            //or_output += term + " ";
        }

        String and_docList = "";
        //String or_docList = "";

        int num_of_and_docs = 0;
        //int num_of_or_docs = 0;
        for (int docId : doc_scores.keySet()){
            int score = doc_scores.get(docId);
            int query_length = query_terms.length;
            if (score == query_length){
                and_docList += docId + " ";
                num_of_and_docs ++;
            }
            //or_docList += docId + " ";
            //num_of_or_docs ++;
        }

        System.out.println(and_output + and_docList);
        System.out.println(num_of_and_docs + " documents are found");
        //System.out.println(or_output + or_docList);
        //System.out.println(num_of_or_docs + " documents are found");

    }


    public static void docAtATimeOR(Map<String, Term_data> index, String[] query_terms){
        Map<Integer, Integer> doc_scores = new HashMap<>();         // holds results

        List<postingList_pointer> postingList_pointers = new LinkedList<>();        // #
        //List<List<Tuple>> query_posting_lists = new LinkedList<>();

        for (String terms : query_terms){           // initialize postingList pointers
            if (index.containsKey(terms)){
                postingList_pointer pointer = new postingList_pointer(index.get(terms).getPosting_list(), terms); // #
                postingList_pointers.add(pointer);                                                          // #
                //query_posting_lists.add(index.get(terms).getPosting_list());
            } else {
                System.out.println(terms + " not found");
            }
        }

        while (continue_scanning_helper(postingList_pointers)){
            //System.out.println("DEBUG: docscores is currently " + doc_scores.size());

            Collections.sort(postingList_pointers);

            // LOG CURRENT POINTERS
            //System.out.println("POINTER STATUS");
            //for (postingList_pointer pointer : postingList_pointers){
            //    System.out.println(pointer.term + " " + pointer.get_current().doc_id);
            //}



            int temp_lowest_id = -1;
            int temp_current_num_of_pointers = 0;
            for (Iterator<postingList_pointer> postingList_iter = postingList_pointers.iterator(); postingList_iter.hasNext();){
                postingList_pointer pointer = postingList_iter.next();

                if (temp_lowest_id == -1){              // if lowest id hasn't been set yet
                    temp_lowest_id = pointer.get_current().doc_id;

                    // this is a lowest pointer
                    temp_current_num_of_pointers ++;
                    if (pointer.hasNext()){
                        pointer.next();
                    } else {
                        postingList_iter.remove();
                    }
                } else {            // lowest id already set
                    if (pointer.get_current().doc_id == temp_lowest_id){
                        // this is another lowest pointer
                        temp_current_num_of_pointers ++;
                        if (pointer.hasNext()){
                            pointer.next();
                        } else {
                            postingList_iter.remove();
                        }
                    } else {
                        // this is not a lowest pointer
                        break;
                    }
                }

            }

            remove_finished_pointers(postingList_pointers);
            doc_scores.put(temp_lowest_id, temp_current_num_of_pointers);

        }

        //String and_output = "FUNCTION: DocAtATimeQueryAnd ";
        String or_output = "FUNCTION: DocAtATimeQueryOr ";
        for (String term : query_terms){
            //and_output += term + " ";
            or_output += term + " ";
        }

        //String and_docList = "";
        String or_docList = "";

        //int num_of_and_docs = 0;
        int num_of_or_docs = 0;
        for (int docId : doc_scores.keySet()){
            int score = doc_scores.get(docId);
            int query_length = query_terms.length;
            if (score == query_length){
                //and_docList += docId + " ";
                //num_of_and_docs ++;
            }
            or_docList += docId + " ";
            num_of_or_docs ++;
        }

        //System.out.println(and_output + and_docList);
        //System.out.println(num_of_and_docs + " documents are found");
        System.out.println(or_output + or_docList);
        System.out.println(num_of_or_docs + " documents are found");
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
                docAtATimeOR(index, query_terms);
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
