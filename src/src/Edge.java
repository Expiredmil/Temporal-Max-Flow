package src;

public class Edge {
    protected int startN;
    protected int endN;
    protected int L;
    protected int C;

    public Edge (int startN, int endN, int L, int C) {
        this.startN = startN;
        this.endN = endN;
        this.L = L;
        this.C = C;
    }
    public Edge (int startN, int endN, int C) {
        this.startN = startN;
        this.endN = endN;
        this.C = C;
    }

}