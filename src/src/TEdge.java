package src;

public class TEdge extends Edge {
    protected int flow = 0;
    protected int startT;
    protected int endT;
    public TEdge(int startN, int startT, int endN, int endT, int C) {
        super(startN, endN, C);
        this.startT = startT;
        this.endT = endT;
    }
    public boolean isFull() {
        return flow >= C;
    }
    public boolean add(int v) {
        if (flow + v > C) {
            return false;
        }
        flow += v;
        return true;
    }

    public int remainingCapacity() {
        return C - flow;
    }

}