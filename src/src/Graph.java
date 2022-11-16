package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    protected int N = 0; // Locations
    protected int M = 0; // Highways
    protected int T = 0; // Time
    protected final int source = 0;
    protected final int sink = N - 1;
    protected List<Edge>[] adj;

    public Graph (int N, int M, int T) {
        this.N = N;
        this.M = M;
        this.T = T;
        adj = new ArrayList[N];
        for (int i = 0; i < N; i++) {
            adj[i] = new ArrayList<>();
        }
    }


    public void addEdge (Edge edge) {
        adj[edge.X].add(edge);
    }
    public TGraph makeTGraph () {
        TGraph tEGraph = new TGraph(N, M, T);
        boolean[][] visited = new boolean[T+1][N];
        for (boolean[] row : visited)
            Arrays.fill(row, false);

        for (int n = 0; n < N; n++) {
            for (int t = 0; t < T+1; t++) {
                for (Edge nadj : adj[n]) {
                    if (n != 0) {
                        if (!visited[t][n]) {
                            continue;
                        }
                    }
                    if (t + nadj.L <= T) {
                        tEGraph.addEdge(new TEdge(nadj.X, nadj.Y, nadj.C, t, t + nadj.L));
                        visited[t+nadj.L][nadj.Y] = true;
                        if((n == 0 || n == N - 1) && t + 1 <= T){
                            tEGraph.addEdge(new TEdge(n, n, 0, t, t + 1));
                            visited[t+nadj.L][nadj.Y] = true;
                        }
                    }
                }
            }
        }
        return tEGraph;
    }

}