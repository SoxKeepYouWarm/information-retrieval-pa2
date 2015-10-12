
public class Tuple implements Comparable<Tuple>{

    int doc_id;
    int frequency;

    public Tuple(int doc_id, int frequency){
        this.doc_id = doc_id;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Tuple other_tup) {
        return other_tup.frequency - this.frequency;
    }
}

