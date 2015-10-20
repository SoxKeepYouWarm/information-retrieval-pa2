
public class Posting_data implements Comparable<Posting_data>{

    int doc_id;
    int frequency;

    public Posting_data(int doc_id, int frequency){
        this.doc_id = doc_id;
        this.frequency = frequency;
    }

    public void increment_frequency(){
        frequency ++;
    }

    @Override
    public int compareTo(Posting_data other_tup) {
        return other_tup.frequency - this.frequency;
    }
}

