package src;

import java.util.ArrayList;
import java.util.List;

public class TGraph extends Graph{
    protected List<TEdge>[] tAdj;

    public TGraph(int N, int M, int T) {
        super(N, M, T);
        tAdj = new ArrayList[M];
        for (int i = 0; i < M; i++) {
            tAdj[i] = new ArrayList<>();
        }
    }

    public void addEdge(TEdge teEdge) {
        tAdj[teEdge.X].add(teEdge);
    }

}