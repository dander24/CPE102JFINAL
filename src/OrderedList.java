import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class OrderedList {
    //functionally a linked list that uses the pair implementation for ordering without being
    //a horrible [redacted] of an implementation

    private List<Pair<Long, Action>> internalList;

    public OrderedList() {
        internalList = new LinkedList<>();
    }

    public void insert(Action action, long time) {
        int size = internalList.size();
        int index = 0;
        while (index < size && internalList.get(index).getKey() < time) {
            index += 1;
        }

        internalList.add(index, new Pair<>(time, action));
    }

    public void remove(Action oldAction) {
        int size = internalList.size();
        int index = 0;
        while (index < size && internalList.get(index).getValue() != oldAction) {
            index += 1;
        }

        if (index < size) {
            internalList.remove(index);
        }

    }

    public Pair<Long, Action> head() {
        if (internalList.size() != 0) {
            return internalList.get(0);
        } else return null;
    }

    public Pair<Long, Action> pop() {
        Pair<Long, Action> p = null;
        if (internalList.size() != 0) {
            p = internalList.get(0);
            internalList.remove(0);
        }
        return p;
    }

}
