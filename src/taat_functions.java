import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class taat_functions {


    public static void termAtATimeAND(Map<String, Term_data> index, String[] query_terms){

        int comparisons = 0;
        List<Integer> resulting_docs = new ArrayList<>();

        int target_score = query_terms.length;
        Map<Integer, Integer> doc_scores = new HashMap<>();

        for (String term : query_terms){
            List<Tuple> posting_list = index.get(term).getPosting_list();

            for (Tuple posting : posting_list){

                comparisons ++;
                if (doc_scores.containsKey(posting.doc_id)){
                    int current_score = doc_scores.get(posting.doc_id);
                    doc_scores.put(posting.doc_id, (current_score + 1));
                } else {
                    doc_scores.put(posting.doc_id, 1);
                }
            }
        }

        for (int key : doc_scores.keySet()){
            comparisons ++;
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
        System.out.println(comparisons + " comparisons are made");
    }


    public static void termAtATimeOR(Map<String, Term_data> index, String[] query_terms) {

        int comparisons = 0;
        List<Integer> resulting_docs = new ArrayList<>();

        Map<Integer, Integer> doc_scores = new HashMap<>();

        for (String term : query_terms) {
            List<Tuple> posting_list = index.get(term).getPosting_list();

            for (Tuple posting : posting_list) {

                comparisons ++;
                if (doc_scores.containsKey(posting.doc_id)) {
                    // do nothing
                } else {
                    doc_scores.put(posting.doc_id, 1);
                }
            }
        }

        for (int key : doc_scores.keySet()) {
            comparisons ++;
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
        System.out.println(comparisons + " comparisons are made");
    }


}
