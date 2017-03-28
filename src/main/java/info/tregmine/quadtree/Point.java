package info.tregmine.quadtree;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("%d,%d", Integer.valueOf(this.x), Integer.valueOf(this.y));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point b = (Point) obj;
        return (b != null) && (b.x == this.x) && (b.y == this.y);
    }

    public int hashCode() {
        int code = 17;
        code = 31 * code + this.x;
        code = 31 * code + this.y;

        return code;
    }
}
