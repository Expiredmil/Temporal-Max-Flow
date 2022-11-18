package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Graph {

    // To avoid overflow
    protected final int INF = Integer.MAX_VALUE/2;
    protected int N; // Locations
    protected int M; // Highways
    protected int T; // Time
    protected static int source = -2;
    protected static int sink = -1;
    protected List<Edge>[] adj;

    public Graph (int N, int M, int T) {
        this.N = N;
        this.M = M;
        this.T = T;
        adj = new LinkedList[N];
        for (int i = 0; i < N; i++) {
            adj[i] = new LinkedList<>();
        }
    }
    public void addEdge (Edge edge) {
        adj[edge.startN].add(edge);
    }
    public TGraph makeTGraph () {
        TGraph tGraph = new TGraph(N, M, T);
        boolean[][] visited = new boolean[T+1][N];
        for (boolean[] row : visited) {
            Arrays.fill(row, false);
            row[0] = true;
        }

        for (int n = 0; n < N; n++) {
            for (int t = 0; t <= T; t++) {
                for (Edge nadj : adj[n]) {
                    if (n != 0) {
                        if (!visited[t][n]) {
                            continue;
                        }
                    }
                    if (t + nadj.L <= T) {
                        tGraph.addEdge(new TEdge(nadj.startN, t, nadj.endN, t + nadj.L, nadj.C));
                        visited[t+nadj.L][nadj.endN] = true;
                    }
                }
            }
        }
        return tGraph;
    }

}