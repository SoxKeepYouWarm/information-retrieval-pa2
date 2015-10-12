
public class Top_K_Tuple implements Comparable<Top_K_Tuple>{

    int posting_list_size;
    String term;

    public Top_K_Tuple(int posting_list_size, String term){
        this.posting_list_size = posting_list_size;
        this.term = term;
    }

    public int getPosting_list_size(){
        return posting_list_size;
    }

    public String getTerm(){
        return term;
    }

    @Override
    public int compareTo(Top_K_Tuple other_tup) {
        return other_tup.getPosting_list_size() - this.getPosting_list_size();
    }
}
