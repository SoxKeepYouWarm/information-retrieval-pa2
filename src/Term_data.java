import java.util.List;

public class Term_data{

    int frequency;
    List<Tuple> posting_list;

    public Term_data(int frequency, List<Tuple> posting_list){
        this.frequency = frequency;
        this.posting_list = posting_list;
    }
}