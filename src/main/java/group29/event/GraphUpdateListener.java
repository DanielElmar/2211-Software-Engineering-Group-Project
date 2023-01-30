package group29.event;

/**
 * Used to handle the event in which the graph is told to update, and passes the graph that was told such
 */
public interface GraphUpdateListener {
    /**
     * handles a graph update event
     */
    void graphUpdate();
    boolean isAlive();

}
