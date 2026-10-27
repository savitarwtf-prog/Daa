import java.util.*;

public class Zones {

    /** Mission 4: Kosaraju with explicit stacks. Zones are numbered in the order of the second pass. */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);

        // pass 1: finishing order on the original slides
        boolean[] seen = new boolean[n];
        int[] order = new int[n];
        int ordered = 0;
        int[] stack = new int[n];
        int[] next = new int[n];
        for (int root = 0; root < n; root++) {
            if (seen[root]) continue;
            int top = 0;
            stack[0] = root;
            seen[root] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[u] < out[u].length) {
                    int v = s.to()[out[u][next[u]++]];
                    if (!seen[v]) { seen[v] = true; stack[++top] = v; }
                } else {
                    order[ordered++] = u;      // u is finished
                    top--;
                }
            }
        }

        // pass 2: reversed slides, from the last finished cell to the first
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = order[i];
            if (zoneOf[root] != -1) continue;
            int top = 0;
            stack[0] = root;
            zoneOf[root] = zones;
            while (top >= 0) {
                int u = stack[top--];
                for (int e : in[u]) {
                    int v = s.from()[e];
                    if (zoneOf[v] == -1) { zoneOf[v] = zones; stack[++top] = v; }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /** Mission 5: pass over the zones in order of numbers. [0] flow stages, [1] cheapest route cost. */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n(), m = s.count();
        // group slides by the zone they start in (counting sort)
        int[] start = new int[zoneCount + 1];
        for (int i = 0; i < m; i++) start[zoneOf[s.from()[i]] + 1]++;
        for (int z = 0; z < zoneCount; z++) start[z + 1] += start[z];
        int[] fill = Arrays.copyOf(start, zoneCount);
        int[] byZone = new int[m];
        for (int i = 0; i < m; i++) byZone[fill[zoneOf[s.from()[i]]]++] = i;

        long INF = Long.MAX_VALUE / 4;
        long[] chain = new long[zoneCount];   // zones on the longest chain ending in this zone
        long[] dist = new long[zoneCount];    // cheapest cost from the zone of cell 0
        Arrays.fill(chain, 1);
        Arrays.fill(dist, INF);
        dist[zoneOf[0]] = 0;

        long stages = 0;
        for (int z = 0; z < zoneCount; z++) {
            stages = Math.max(stages, chain[z]);
            for (int p = start[z]; p < start[z + 1]; p++) {
                int i = byZone[p];
                int y = zoneOf[s.to()[i]];
                if (y == z) continue;         // inside a zone: free
                chain[y] = Math.max(chain[y], chain[z] + 1);
                if (dist[z] < INF) dist[y] = Math.min(dist[y], dist[z] + s.time()[i]);
            }
        }
        long target = dist[zoneOf[n - 1]];
        return new long[]{stages, target >= INF ? 0 : target};
    }
}