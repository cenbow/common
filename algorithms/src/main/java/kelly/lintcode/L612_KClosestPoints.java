package kelly.lintcode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by kelly.li on 17/8/28.
 */
public class L612_KClosestPoints {

    public Point[] kClosest(Point[] points, final Point origin, int k) {
        PriorityQueue<Point> priorityQueue = new PriorityQueue<Point>(points.length, new Comparator<Point>() {
            public int compare(Point a, Point b) {
                double distance1 = Math.sqrt(Math.pow(a.x - origin.x, 2) + Math.pow(a.y - origin.y, 2));
                double distance2 = Math.sqrt(Math.pow(b.x - origin.x, 2) + Math.pow(b.y - origin.y, 2));
                // 比如负数坐标: -1,-1  要排在 1,1 前面
                return distance1 < distance2 ? 1 : (distance1 > distance2 ? -1 : a.x < 0 ? 1 : 0);
            }
        });
        for (Point point : points) {
            priorityQueue.offer(point);
            if (priorityQueue.size() > k) {
                priorityQueue.poll();
            }
        }

        List<Point> list = new ArrayList<Point>();
        int index = 0;
        while (priorityQueue.peek() != null) {
            list.add(priorityQueue.poll());
        }

        return list.toArray(new Point[k]);
    }


}


class Point {
    int x;
    int y;

    Point() {
        x = 0;
        y = 0;
    }

    Point(int a, int b) {
        x = a;
        y = b;
    }
}