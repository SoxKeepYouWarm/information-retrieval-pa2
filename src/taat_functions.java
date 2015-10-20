import java.util.*;

public class taat_functions {


    public static String[] termAtATimeAND(Map<String, Term_data> index, String[] query_terms){

        class comparison_wrapper{
            int comparisons;
            public comparison_wrapper(){
                this.comparisons = 0;
            }
            public void increment_comparisons(){
                comparisons ++;
            }
        }

        comparison_wrapper comparison_wrapper = new comparison_wrapper();

        class Intermediate_Results{
            List<Posting_data> intermediate_results;

            public Intermediate_Results(){
                this.intermediate_results = new LinkedList<>();
            }

            public void add_result(Posting_data new_data, comparison_wrapper _comparisons){
                int new_id = new_data.doc_id;

                if (intermediate_results.size() == 0){
                    intermediate_results.add(new_data);
                    _comparisons.increment_comparisons();
                } else{
                    for (int i = 0; i < intermediate_results.size(); i++){
                        Posting_data current_data = intermediate_results.get(i);

                        _comparisons.increment_comparisons();
                        if (new_id < current_data.doc_id){
                            return;
                        } else if (new_id == current_data.doc_id){
                            current_data.increment_frequency();
                            return;
                        } else {
                            // new id is greater than current id
                            if (i < intermediate_results.size() - 1){
                                // if there's another entry
                                // continue loop
                            } else {
                                // if this is the last entry
                                intermediate_results.add(i + 1, new_data);
                                return;
                            }
                        }
                    }
                }

            }
        }

        List<Integer> test_resulting_docs = new ArrayList<>();

        Intermediate_Results intermediate_results = new Intermediate_Results();

        for (String term : query_terms){
            List<Posting_data> posting_list = index.get(term).getPosting_list();

            for (Posting_data posting : posting_list){

                Posting_data new_posting = new Posting_data(posting.doc_id, 1);
                intermediate_results.add_result(new_posting, comparison_wrapper);

            }
        }

        for (Posting_data data : intermediate_results.intermediate_results){
            if (data.frequency == query_terms.length){
                test_resulting_docs.add(data.doc_id);
            }
        }

        int num_of_docs = test_resulting_docs.size();

        String function_output = "FUNCTION: TermAtATimeAnd ";
        for (String term : query_terms){
            function_output += term + " ";
        }
        System.out.println(function_output);

        String function_output_two = "docID's: ";
        for (int id : test_resulting_docs){
            function_output_two += id + " ";
        }
        System.out.println(function_output_two);

        System.out.println(num_of_docs + " documents are found");
        System.out.println(comparison_wrapper.comparisons + " comparisons are made");

        String[] return_messages = new String[4];
        return_messages[0] = function_output;
        return_messages[1] = function_output_two;
        return_messages[2] = num_of_docs + " documents are found";
        return_messages[3] = comparison_wrapper.comparisons + " comparisons are made";

        return return_messages;
    }


    public static String[] termAtATimeOR(Map<String, Term_data> index, String[] query_terms) {

        class comparison_wrapper{
            int comparisons;
            public comparison_wrapper(){
                this.comparisons = 0;
            }
            public void increment_comparisons(){
                comparisons ++;
            }
        }

        comparison_wrapper comparison_wrapper = new comparison_wrapper();

        class Intermediate_Results{
            List<Posting_data> intermediate_results;

            public Intermediate_Results(){
                this.intermediate_results = new LinkedList<>();
            }

            public void add_result(Posting_data new_data, comparison_wrapper _comparisons){
                int new_id = new_data.doc_id;

                if (intermediate_results.size() == 0){
                    intermediate_results.add(new_data);
                    _comparisons.increment_comparisons();
                } else{
                    for (int i = 0; i < intermediate_results.size(); i++){
                        Posting_data current_data = intermediate_results.get(i);

                        _comparisons.increment_comparisons();
                        if (new_id < current_data.doc_id){
                            intermediate_results.add(i, new_data);
                            return;
                        } else if (new_id == current_data.doc_id){
                            current_data.increment_frequency();
                            return;
                        } else {
                            // new id is greater than current id
                            if (i < intermediate_results.size() - 1){
                                // if there's another entry
                                // continue loop
                            } else {
                                // if this is the last entry
                                intermediate_results.add(i + 1, new_data);
                                return;
                            }
                        }
                    }
                }

            }
        }

        List<Integer> test_resulting_docs = new ArrayList<>();

        Intermediate_Results intermediate_results = new Intermediate_Results();

        for (String term : query_terms){
            List<Posting_data> posting_list = index.get(term).getPosting_list();

            for (Posting_data posting : posting_list){

                Posting_data new_posting = new Posting_data(posting.doc_id, 1);
                intermediate_results.add_result(new_posting, comparison_wrapper);

            }
        }

        for (Posting_data data : intermediate_results.intermediate_results){
            test_resulting_docs.add(data.doc_id);
        }

        int num_of_docs = test_resulting_docs.size();

        String function_output = "FUNCTION: TermAtATimeOr ";
        for (String term : query_terms){
            function_output += term + " ";
        }
        System.out.println(function_output);

        String function_output_two = "docID's: ";
        for (int id : test_resulting_docs){
            function_output_two += id + " ";
        }
        System.out.println(function_output_two);

        System.out.println(num_of_docs + " documents are found");
        System.out.println(comparison_wrapper.comparisons + " comparisons are made");

        String[] return_messages = new String[4];
        return_messages[0] = function_output;
        return_messages[1] = function_output_two;
        return_messages[2] = num_of_docs + " documents are found";
        return_messages[3] = comparison_wrapper.comparisons + " comparisons are made";

        return return_messages;
    }


}
