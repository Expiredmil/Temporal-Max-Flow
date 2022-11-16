package src;

public class TEdge extends Edge {
    protected int sT = 0; // start time
    protected int eT = 0; // end time
    public TEdge(int X, int Y, int C, int sT, int eT) {
        super(X, Y, C);
        this.sT = sT;
        this.eT = eT;
    }
}