import java.util.Comparator;
import java.util.Map;

public class Term_Data_Comparator implements Comparator<Term_data> {
    Map base;

    @Override
    public int compare(Term_data o1, Term_data o2) {
        return o1.get_Posting_List_size() - o2.get_Posting_List_size();
    }

    public Term_Data_Comparator(Map base) {
        this.base = base;
    }

}