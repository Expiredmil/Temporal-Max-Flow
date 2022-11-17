package src;

import java.util.*;
import static java.lang.Math.min;

public class TGraph extends Graph{

    protected List<TEdge>[][] tAdj; // Adjacency list
    protected int[][] visited;
    protected int visitedToken = 1;
    protected int maxFlow;

    public TGraph(int N, int M, int T) {
        super(N, M, T);

        // Initialize adjacency list for the TGraph
        tAdj = new LinkedList[T+1][N];
        for (int t = 0; t <= T; t++) {
            for (int n = 0; n < N; n++) {
                tAdj[t][n] = new LinkedList<>();
            }
        }
        visited = new int[T+1][N];
        for (int[] row : visited) {
            Arrays.fill(row, 0);
        }
    }

    public void addEdge(TEdge teEdge) {
        tAdj[teEdge.startT][teEdge.startN].add(teEdge);
    }
    public int getMaxFlow() {
        int flow;
        do {
            resetVisitedToken();
            flow = bfs();
            maxFlow += flow;
        } while (flow != 0);
        return maxFlow;
    }

    private int bfs() {
        TEdge[][] prev = new TEdge[T+1][N];

        // The queue can be optimized to use a faster queue
        Queue<TEdge> q = new ArrayDeque<>();
        q.offer(new TEdge(source, source, source, source, INF));
        // Perform BFS from source to sink
        while (!q.isEmpty()) {
            TEdge edge = q.poll();
            if (edge.endN == sink && edge.endT == sink) {
                break;
            }
            for (TEdge next_edge : tAdj[edge.endT][edge.endN]) {
                int cap = next_edge.remainingCapacity();
                if (cap > 0 && !visited(next_edge.endT, next_edge.endN)) {
                    prev[next_edge.endT][next_edge.endN] = next_edge;
                    visit(next_edge.endT,next_edge.endN);
                    q.offer(next_edge);
                }
            }
        }

        // Sink not reachable!
        if (prev[sink][sink] == null) return 0;

        int bottleNeck = INF;

        // Find augmented path and bottle neck
        for (TEdge edge = prev[sink][sink]; edge != null; edge = prev[edge.startT][edge.startN])
            bottleNeck = min(bottleNeck, edge.remainingCapacity());

        // Retrace augmented path and update flow values.
        for (TEdge edge = prev[sink][sink]; edge != null; edge = prev[edge.startT][edge.startN]) edge.add(bottleNeck);

        // Return bottleneck flow
        return bottleNeck;
    }

    public void visit(int time, int node) {
        if (node == source)
            return;
        if (node == sink)
            return;
        visited[time][node] = visitedToken;
    }

    public boolean visited (int time, int node) {
        return visited[time][node] == visitedToken;
    }

    public void resetVisitedToken () {
        visitedToken++;
    }
}