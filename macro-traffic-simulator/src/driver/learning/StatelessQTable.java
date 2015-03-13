package driver.learning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class StatelessQTable<K> {

    private Map<K, Float> table;

    public StatelessQTable(ArrayList<K> A) {

        table = new Hashtable<K, Float>();

        for (K a : A) {
            table.put(a, 0.0f);
        }

    }

    public float getQValue(K a) {
        return table.get(a);
    }

    public void setQValue(K a, float q) {
        table.put(a, q);
    }

    public void print() {
        for (K a : table.keySet()) {
            System.out.println("Q(" + a + ")=" + table.get(a));
        }
    }

}
