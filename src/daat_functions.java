import java.util.*;

public class daat_functions {


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
            }
        }

        for (postingList_pointer remove_pointer : remove_pointers){
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

        int comparisons = 0;
        Map<Integer, Integer> doc_scores = new HashMap<>();         // holds results

        List<postingList_pointer> postingList_pointers = new LinkedList<>();

        for (String terms : query_terms){           // initialize postingList pointers

            comparisons ++;
            if (index.containsKey(terms)){
                postingList_pointer pointer = new postingList_pointer(index.get(terms).getPosting_list(), terms);
                postingList_pointers.add(pointer);
            } else {
                System.out.println(terms + " not found");
            }
        }

        while (continue_scanning_helper(postingList_pointers)){
            comparisons += postingList_pointers.size();

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

                comparisons ++;
                if (temp_lowest_id == -1){              // if lowest id hasn't been set yet
                    temp_lowest_id = pointer.get_current().doc_id;

                    // this is a lowest pointer
                    temp_current_num_of_pointers ++;
                    comparisons ++;
                    if (pointer.hasNext()){
                        pointer.next();
                    } else {
                        postingList_iter.remove();
                    }
                } else {            // lowest id already set
                    comparisons ++;
                    if (pointer.get_current().doc_id == temp_lowest_id){
                        // this is another lowest pointer
                        temp_current_num_of_pointers ++;
                        comparisons ++;
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
        for (String term : query_terms){
            and_output += term + " ";
        }

        String and_docList = "";

        int num_of_and_docs = 0;
        for (int docId : doc_scores.keySet()){
            int score = doc_scores.get(docId);
            int query_length = query_terms.length;
            if (score == query_length){
                and_docList += docId + " ";
                num_of_and_docs ++;
            }
        }

        System.out.println(and_output);
        System.out.println("docID's: " + and_docList);
        System.out.println(num_of_and_docs + " documents are found");
        System.out.println(comparisons + " comparisons are made");
    }


    public static void docAtATimeOR(Map<String, Term_data> index, String[] query_terms){

        int comparisons = 0;
        Map<Integer, Integer> doc_scores = new HashMap<>();         // holds results

        List<postingList_pointer> postingList_pointers = new LinkedList<>();

        for (String terms : query_terms){           // initialize postingList pointers

            comparisons ++;
            if (index.containsKey(terms)){
                postingList_pointer pointer = new postingList_pointer(index.get(terms).getPosting_list(), terms);
                postingList_pointers.add(pointer);
            } else {
                System.out.println(terms + " not found");
            }
        }

        while (continue_scanning_helper(postingList_pointers)){

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

                comparisons ++;
                if (temp_lowest_id == -1){              // if lowest id hasn't been set yet
                    temp_lowest_id = pointer.get_current().doc_id;

                    // this is a lowest pointer
                    temp_current_num_of_pointers ++;
                    comparisons ++;
                    if (pointer.hasNext()){
                        pointer.next();
                    } else {
                        postingList_iter.remove();
                    }
                } else {            // lowest id already set
                    comparisons ++;
                    if (pointer.get_current().doc_id == temp_lowest_id){
                        // this is another lowest pointer
                        temp_current_num_of_pointers ++;
                        comparisons ++;
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

        String or_output = "FUNCTION: DocAtATimeQueryOr ";
        for (String term : query_terms){
            or_output += term + " ";
        }

        String or_docList = "";

        int num_of_or_docs = 0;
        for (int docId : doc_scores.keySet()){
            or_docList += docId + " ";
            num_of_or_docs ++;
        }

        System.out.println(or_output);
        System.out.println("docID's: " + or_docList);
        System.out.println(num_of_or_docs + " documents are found");
        System.out.println(comparisons + " comparisons are made");
    }


}
