import java.util.*;

public class Maze {

    /** Mission 1: walls in file order, break a wall if union returns true. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] result = new int[Math.max(0, w.cells() - 1)];
        int m = 0;
        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) result[m++] = k;
        }
        return Arrays.copyOf(result, m);
    }

    /** Mission 2: Kruskal. Sort walls by cost, then the same DSU loop. */
    public static int[] cheapestMaze(Input.Walls w) {
        int count = w.count();
        long[] keys = new long[count];                 // cost in high bits, wall index in low bits
        for (int k = 0; k < count; k++) keys[k] = ((long) w.cost()[k] << 32) | k;
        Arrays.sort(keys);
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] result = new int[Math.max(0, w.cells() - 1)];
        int m = 0;
        for (long key : keys) {
            int k = (int) (key & 0xFFFFFFFFL);
            if (dsu.union(w.a()[k], w.b()[k])) {
                result[m++] = k;
                if (m == result.length) break;
            }
        }
        return Arrays.copyOf(result, m);
    }

    /** Mission 2: Prim with a priority queue (lazy). Returns the MST cost. */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), count = w.count();
        // adjacency in arrays: edges of u are start[u] .. start[u+1]-1
        int[] start = new int[n + 1];
        for (int k = 0; k < count; k++) { start[w.a()[k] + 1]++; start[w.b()[k] + 1]++; }
        for (int i = 0; i < n; i++) start[i + 1] += start[i];
        int[] fill = Arrays.copyOf(start, n);
        int[] to = new int[2 * count], cost = new int[2 * count];
        for (int k = 0; k < count; k++) {
            int a = w.a()[k], b = w.b()[k];
            to[fill[a]] = b; cost[fill[a]++] = w.cost()[k];
            to[fill[b]] = a; cost[fill[b]++] = w.cost()[k];
        }

        boolean[] inTree = new boolean[n];
        PriorityQueue<Long> pq = new PriorityQueue<>();  // cost in high bits, target cell in low bits
        long total = 0;
        int taken = 1;
        inTree[0] = true;
        for (int e = start[0]; e < start[1]; e++) pq.add(((long) cost[e] << 32) | to[e]);
        while (!pq.isEmpty() && taken < n) {
            long top = pq.poll();
            int v = (int) (top & 0xFFFFFFFFL);
            if (inTree[v]) continue;                      // lazy: skip outdated entries
            inTree[v] = true;
            taken++;
            total += top >>> 32;
            for (int e = start[v]; e < start[v + 1]; e++) {
                if (!inTree[to[e]]) pq.add(((long) cost[e] << 32) | to[e]);
            }
        }
        return total;
    }
}