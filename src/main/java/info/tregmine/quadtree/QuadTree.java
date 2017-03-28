package info.tregmine.quadtree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuadTree<V> {
    Node<V> root;

    public QuadTree() {
        this(0);
    }

    public QuadTree(int size) {
        if (size == 0) {
            this.root = new Node<V>(new Rectangle(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE));
        } else {
            this.root = new Node<V>(new Rectangle(-size, size, size, -size));
        }
    }

    private static Integer linearSearch(Map<Rectangle, Integer> data, Point p) {
        for (Map.Entry<Rectangle, Integer> entry : data.entrySet()) {
            Rectangle rect = entry.getKey();
            if (rect.contains(p)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void main(String[] args)
            throws Exception {
        int size = 0;
        int points = 10000;
        QuadTree<Integer> tree = new QuadTree<Integer>(size);
        if (size == 0) {
            size = 1073741823;
        }
        Map<Rectangle, Integer> values = new HashMap<Rectangle, Integer>();
        Random rand = new Random();
        float avgInsertTime = 0.0F;
        for (int i = 0; i < points; i++) {
            int x1 = rand.nextInt(2 * size) - size;
            int y1 = rand.nextInt(2 * size) - size;
            int w = Math.abs(rand.nextInt(size / 1000));
            int h = Math.abs(rand.nextInt(size / 1000));

            Rectangle rect = new Rectangle(x1, y1, x1 + w, y1 - h);
            if ((rect.getLeft() < -size) || (rect.getRight() > size) || (rect.getBottom() < -size) || (rect.getTop() > size)) {
                i--;
            } else {
                Integer value = Integer.valueOf(rand.nextInt());
                try {
                    long t = System.nanoTime();
                    tree.insert(rect, value);
                    t = System.nanoTime() - t;
                    avgInsertTime += (float) t;
                    values.put(rect, value);
                } catch (IntersectionException e) {
                    i--;
                }
            }
        }
        avgInsertTime /= points;
        System.out.println(String.format("Average insert time: %.2f ns", new Object[]{Float.valueOf(avgInsertTime)}));
        if (points != values.size()) {
            System.out.println("Some inserts failed.");
        }
        float avgFindTime = 0.0F;
        float avgLinearSearchTime = 0.0F;
        for (Map.Entry<Rectangle, Integer> entry : values.entrySet()) {
            Rectangle rect = entry.getKey();
            int value = entry.getValue().intValue();

            int w = Math.abs(rect.getRight() - rect.getLeft());
            int h = Math.abs(rect.getTop() - rect.getBottom());

            int x = rect.getLeft() + rand.nextInt(w);
            int y = rect.getBottom() + rand.nextInt(h);

            Point p = new Point(x, y);

            long t = System.nanoTime();
            Integer cmp = tree.find(p);
            t = System.nanoTime() - t;
            if ((cmp == null) || (cmp.intValue() != value)) {
                System.out.println("Missmatch for " + p + " that should match " + rect);
            } else {
                avgFindTime += (float) t;

                t = System.nanoTime();
                value = linearSearch(values, p).intValue();
                if (cmp.intValue() != value) {
                    System.out.println("Linear search result does not match find.");
                }
                t = System.nanoTime() - t;

                avgLinearSearchTime += (float) t;
            }
        }
        avgFindTime /= values.size();
        System.out.println(String.format("Average find time: %.2f ns", new Object[]{Float.valueOf(avgFindTime)}));

        avgLinearSearchTime /= values.size();
        System.out.println(String.format("Average linear search time: %.2f ns", new Object[]{Float.valueOf(avgLinearSearchTime)}));
        if (size < 1000) {
            System.out.println("Generating visualization.");
            QuadTreeVisualizer.drawQuadTree(tree, "tree.png");
        }
        System.out.println("total memory: " + Runtime.getRuntime().totalMemory() / 1024L / 1024L + " mb");
        System.out.println("free memory: " + Runtime.getRuntime().freeMemory() / 1024L / 1024L + " mb");
    }

    public void insert(Rectangle rect, V value)
            throws IntersectionException {
        if (this.root.findIntersections(rect)) {
            throw new IntersectionException();
        }
        for (Point p : rect.getPoints()) {
            this.root.insert(p, rect, value, 0);
        }
        this.root.assign(rect, value);
    }

    public V find(Point p) {
        Node<V> current = this.root;
        while (current.split) {
            if (!current.contains(p)) {
                return null;
            }
            Rectangle rect = current.nodeRect;
            if (p.x > rect.centerX) {
                if (p.y > rect.centerY) {
                    current = current.tr;
                } else {
                    current = current.br;
                }
            } else if (p.y > rect.centerY) {
                current = current.tl;
            } else {
                current = current.bl;
            }
        }
        for (Map.Entry<Rectangle, V> entry : current.values.entrySet()) {
            Rectangle rect = entry.getKey();
            if (rect.contains(p)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static class Node<V> {
        public static int TOTAL_COUNT = 0;
        Rectangle nodeRect;
        Rectangle valueRect;
        Point valuePoint;
        Map<Rectangle, V> values;
        Node<V> tl;
        Node<V> tr;
        Node<V> bl;
        Node<V> br;
        boolean split;
        boolean color = false;

        public Node(Rectangle rect) {
            TOTAL_COUNT += 1;

            this.nodeRect = rect;

            this.valuePoint = null;
            this.valueRect = null;
            this.values = new HashMap<Rectangle, V>();

            this.tl = null;
            this.tr = null;
            this.bl = null;
            this.br = null;

            this.split = false;
        }

        public boolean contains(Point p) {
            return this.nodeRect.contains(p);
        }

        public boolean intersects(Rectangle rect) {
            return this.nodeRect.intersects(rect);
        }

        public V find(Point p) {
            if (!this.split) {
                for (Map.Entry<Rectangle, V> entry : this.values.entrySet()) {
                    Rectangle rect = entry.getKey();
                    if (rect.contains(p)) {
                        return entry.getValue();
                    }
                }
                return null;
            }
            if (this.tl.contains(p)) {
                return this.tl.find(p);
            }
            if (this.tr.contains(p)) {
                return this.tr.find(p);
            }
            if (this.bl.contains(p)) {
                return this.bl.find(p);
            }
            if (this.br.contains(p)) {
                return this.br.find(p);
            }
            return null;
        }

        public void insert(Point p, Rectangle rect, V v, int depth)
                throws IntersectionException {
            if (!this.nodeRect.contains(p)) {
                return;
            }
            if ((this.valuePoint == null) && (!this.split)) {
                this.values.put(rect, v);
                this.valueRect = rect;
                this.valuePoint = p;
                return;
            }
            if (p.equals(this.valuePoint)) {
                throw new IntersectionException("Specified point " + p + " is already in use.");
            }
            if (!this.split) {
                long totalWidth = this.nodeRect.getWidth();
                long totalHeight = this.nodeRect.getHeight();
                int w1 = (int) (totalWidth / 2L);
                int h1 = (int) (totalHeight / 2L);
                int w2 = (int) (totalWidth - w1);
                int h2 = (int) (totalHeight - h1);

                this.tl = new Node<V>(new Rectangle(this.nodeRect.x1, this.nodeRect.y1, this.nodeRect.x1 + w1, this.nodeRect.y1 - h1));

                this.tr = new Node<V>(new Rectangle(this.nodeRect.x1 + w1, this.nodeRect.y1, this.nodeRect.x1 + w1 + w2, this.nodeRect.y1 - h1));

                this.bl = new Node<V>(new Rectangle(this.nodeRect.x1, this.nodeRect.y1 - h1, this.nodeRect.x1 + w1, this.nodeRect.y1 - h1 - h2));

                this.br = new Node<V>(new Rectangle(this.nodeRect.x1 + w1, this.nodeRect.y1 - h1, this.nodeRect.x1 + w1 + w2, this.nodeRect.y1 - h1 - h2));

                this.split = true;

                V value = this.values.get(this.valueRect);
                if (this.tl.contains(this.valuePoint)) {
                    this.tl.insert(this.valuePoint, this.valueRect, value, depth + 1);
                } else if (this.tr.contains(this.valuePoint)) {
                    this.tr.insert(this.valuePoint, this.valueRect, value, depth + 1);
                } else if (this.bl.contains(this.valuePoint)) {
                    this.bl.insert(this.valuePoint, this.valueRect, value, depth + 1);
                } else if (this.br.contains(this.valuePoint)) {
                    this.br.insert(this.valuePoint, this.valueRect, value, depth + 1);
                } else {
                    throw new IllegalStateException(this.valuePoint + " is not " + "in " + this.tl.nodeRect + ";\n" + "or " + this.tr.nodeRect + ";\n" + "or " + this.bl.nodeRect + ";\n" + "or " + this.br.nodeRect);
                }
                for (Map.Entry<Rectangle, V> entry : this.values.entrySet()) {
                    Rectangle candidate = entry.getKey();
                    if (this.tl.intersects(candidate)) {
                        this.tl.values.put(candidate, entry.getValue());
                        this.tl.color = true;
                    }
                    if (this.tr.intersects(candidate)) {
                        this.tr.values.put(candidate, entry.getValue());
                        this.tr.color = true;
                    }
                    if (this.bl.intersects(candidate)) {
                        this.bl.values.put(candidate, entry.getValue());
                        this.bl.color = true;
                    }
                    if (this.br.intersects(candidate)) {
                        this.br.values.put(candidate, entry.getValue());
                        this.br.color = true;
                    }
                }
            }
            if (this.tl.contains(p)) {
                this.tl.insert(p, rect, v, depth + 1);
            } else if (this.tr.contains(p)) {
                this.tr.insert(p, rect, v, depth + 1);
            } else if (this.bl.contains(p)) {
                this.bl.insert(p, rect, v, depth + 1);
            } else if (this.br.contains(p)) {
                this.br.insert(p, rect, v, depth + 1);
            } else {
                throw new IllegalStateException(p + " is not in " + this.tl.nodeRect + ";\n" + "or " + this.tr.nodeRect + ";\n" + "or " + this.bl.nodeRect + ";\n" + "or " + this.br.nodeRect);
            }
            this.values.clear();
            this.valueRect = null;
            this.valuePoint = null;
        }

        public boolean findIntersections(Rectangle rect) {
            if (!this.split) {
                for (Map.Entry<Rectangle, V> entry : this.values.entrySet()) {
                    Rectangle candidate = entry.getKey();
                    if (candidate.intersects(rect)) {
                        return true;
                    }
                }
                return false;
            }
            if ((this.tl.intersects(rect)) && (this.tl.findIntersections(rect))) {
                return true;
            }
            if ((this.tr.intersects(rect)) && (this.tr.findIntersections(rect))) {
                return true;
            }
            if ((this.bl.intersects(rect)) && (this.bl.findIntersections(rect))) {
                return true;
            }
            return (this.br.intersects(rect)) && (this.br.findIntersections(rect));
        }

        public void assign(Rectangle rect, V value) {
            if (!this.split) {
                this.values.put(rect, value);
                this.color = true;
                return;
            }
            if (this.tl.intersects(rect)) {
                this.tl.assign(rect, value);
            }
            if (this.tr.intersects(rect)) {
                this.tr.assign(rect, value);
            }
            if (this.bl.intersects(rect)) {
                this.bl.assign(rect, value);
            }
            if (this.br.intersects(rect)) {
                this.br.assign(rect, value);
            }
        }
    }
}
