import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * A simply connected orthogonal Region of Interest
 */
public class ROI {
    public ROI(String fileName) throws FileNotFoundException, java.util.InputMismatchException {
        Scanner sc = new Scanner(new File(fileName)).useLocale(Locale.ENGLISH);
        int n = sc.nextInt();
        points = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
            points.add(new Vec(sc.nextDouble(), sc.nextDouble()) {
            });
        int m = sc.nextInt();
        sides = new ArrayList<>(m);
        for (int i = 0; i < m; ++i)
            sides.add(new Line(points.get(sc.nextInt()), points.get(sc.nextInt())));
        windowDimensions = new Vec(sc.nextInt(), sc.nextInt());
        windowScale = sc.nextDouble();
        windowOffset = new Vec(sc.nextInt(), sc.nextInt());
    }

    double dist(Vec pos, double dir, double range) {
        Line ray = new Line(pos, new Vec(pos.x + Math.cos(dir) * range, pos.y + Math.sin(dir) * range));
        Vec wall = null;
        for (Line side : sides) {
            Vec act = side.intersect(ray);
            if (act != null && (wall == null || pos.dist(act) < pos.dist(wall)))
                wall = act;
        }
        return wall == null ? Double.MAX_VALUE : pos.dist(wall);
    }

    ArrayList<Vec> points;
    ArrayList<Line> sides;
    Vec windowDimensions;
    double windowScale;
    Vec windowOffset;
}
