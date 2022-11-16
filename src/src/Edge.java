package src;

public class Edge {
    protected int X;
    protected int Y;
    protected int L;
    protected int C;
    protected int flow = 0;

    public Edge (int X, int Y, int L, int C) {
        this.X = X;
        this.Y = Y;
        this.L = L;
        this.C = C;
    }
    public Edge (int X, int Y, int C) {
        this.X = X;
        this.Y = Y;
        this.C = C;
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
}