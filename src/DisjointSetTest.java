/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // long chain
        DisjointSet big = new DisjointSet(100000);
        for (int i = 0; i + 1 < 100000; i++) big.union(i, i + 1);
        check(big.count() == 1, "chain: one set at the end");
        check(big.connected(0, 99999), "chain: the ends are connected");

        // union of a set with itself
        DisjointSet s = new DisjointSet(3);
        check(!s.union(1, 1), "union(1, 1) returns false");
        check(s.count() == 3, "count is unchanged after union(1, 1)");

        // count after many unions
        DisjointSet c = new DisjointSet(10);
        for (int i = 0; i < 9; i++) c.union(i, i + 1);
        check(c.count() == 1, "count is 1 after 9 unions");
        check(!c.union(0, 9), "union inside one set returns false");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
