import java.util.*;

public class daat_functions {


    private static boolean continue_scanning_helper(List<postingList_pointer> list_pointers){
        boolean continue_search = false;

        for (int i = 0; i < list_pointers.size(); i++){
            if (list_pointers.get(i).get_current() != null){
                continue_search = true;
            }
        }

        return continue_search;
    }

    public static boolean remove_finished_pointers(List<postingList_pointer> list_pointers){
        //List<Integer> pointers_to_remove = new ArrayList<>(list_pointers.size());
        List<postingList_pointer> remove_pointers = new LinkedList<>();
        for (int i = 0; i < list_pointers.size(); i++){

            try {
                if (list_pointers.get(i).get_current() == null) {
                    remove_pointers.add(list_pointers.get(i));
                }
            } catch (IndexOutOfBoundsException err){
                remove_pointers.add(list_pointers.get(i));
            }
            //if (! list_pointers.get(i).hasNext()){
            //    remove_pointers.add(list_pointers.get(i));
            //}
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
        List<Posting_data> posting_list;
        int index;
        String term;

        public postingList_pointer(List<Posting_data> posting_list, String term){
            this.posting_list = posting_list;
            this.term = term;
            index = 0;
        }

        public Posting_data get_current(){
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
        List<Posting_data> doc_results = new LinkedList<>();

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

            Posting_data data = new Posting_data(temp_lowest_id, temp_current_num_of_pointers);
            doc_results.add(data);
            if (remove_finished_pointers(postingList_pointers)){
                break;
            }
        }

        String and_output = "FUNCTION: DocAtATimeQueryAnd ";
        for (String term : query_terms){
            and_output += term + " ";
        }

        String test_docList = "";

        int doc_result_num = 0;
        for (Posting_data data : doc_results){
            if (data.frequency == query_terms.length){
                test_docList += data.doc_id + " ";
                doc_result_num ++;
            }
        }

        System.out.println(and_output);
        System.out.println("docID's: " + test_docList);
        System.out.println(doc_result_num + " documents are found");
        System.out.println(comparisons + " comparisons are made");
    }


    public static void docAtATimeOR(Map<String, Term_data> index, String[] query_terms){

        int comparisons = 0;
        List<Posting_data> doc_results = new LinkedList<>();

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

            Posting_data data = new Posting_data(temp_lowest_id, temp_current_num_of_pointers);
            doc_results.add(data);
            remove_finished_pointers(postingList_pointers);

        }

        String and_output = "FUNCTION: DocAtATimeQueryOr ";
        for (String term : query_terms){
            and_output += term + " ";
        }

        String test_docList = "";

        int doc_result_num = 0;
        for (Posting_data data : doc_results){
            test_docList += data.doc_id + " ";
            doc_result_num ++;
        }

        System.out.println(and_output);
        System.out.println("docID's: " + test_docList);
        System.out.println(doc_result_num + " documents are found");
        System.out.println(comparisons + " comparisons are made");
    }


}
