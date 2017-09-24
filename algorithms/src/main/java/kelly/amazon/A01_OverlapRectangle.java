package kelly.amazon;

/**
 * Created by kelly.li on 17/8/26.
 */
public class A01_OverlapRectangle {

    public static boolean hasOverlap(Node a, Node b, Node c, Node d) {
        int ex = Math.max(a.x, c.x);
        int ey = Math.max(a.y, c.y);

        int fx = Math.min(b.x, d.x);
        int fy = Math.min(b.y, d.y);

        return (fx > ex && fy > ey);
    }

    public static void main(String[] args) {
        Node A = new Node(0, 0), B = new Node(2, 2), C = new Node(3, 0), D = new Node(4, 4);
        System.out.println(hasOverlap(A, B, C, D));
    }
}


class Node {

    int x;
    int y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}