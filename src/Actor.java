import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;

public class Actor extends NonStatic {
    private int rate;
    private List<Action> pendingActions;

    public Actor(String name, Point position, int Rate, List<PImage> pImages) {
        super(name, position, pImages);
        rate = Rate;
        pendingActions = new LinkedList<>();
    }

    public int getRate() {
        return rate;
    }

    public void removePendingAction(Action action) {
        pendingActions.remove(action);
    }

    public void addPendingAction(Action action) {
        pendingActions.add(action);
    }

    public void clearPendingActions() {
        pendingActions.clear();
    }

    public List<Action> getPendingActions() {
        return pendingActions;
    }
}
