package model;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * A simply connected orthogonal Region of Interest
 */
public class ROI {
    private ArrayList<Vec> points;
    private ArrayList<Line> sides;
    private Vec minPoint;
    private Vec maxPoint;

    public ROI(String fileName) throws FileNotFoundException, java.util.InputMismatchException {
        Scanner sc = new Scanner(new File(fileName)).useLocale(Locale.ENGLISH);
        int n = sc.nextInt();
        points = new ArrayList<>(n);
        if(n > 0) {
            points.add(new Vec(sc.nextDouble(), sc.nextDouble()));
            this.minPoint = new Vec(points.get(0).x, points.get(0).y);
            this.maxPoint = new Vec(points.get(0).x, points.get(0).y);
            for(int i = 1; i < n; ++i) {
                points.add(new Vec(sc.nextDouble(), sc.nextDouble()));
                if(points.get(i).x < this.minPoint.x) {
                    this.minPoint.x = points.get(i).x;
                }
                if(points.get(i).y < this.minPoint.y) {
                    this.minPoint.y = points.get(i).y;
                }
                if(this.maxPoint.x < points.get(i).x) {
                    this.maxPoint.x = points.get(i).x;
                }
                if(this.maxPoint.y < points.get(i).y) {
                    this.maxPoint.y = points.get(i).y;
                }
            }
        }
        int m = sc.nextInt();
        sides = new ArrayList<>(m);
        for(int i = 0; i < m; ++i) {
            sides.add(new Line(points.get(sc.nextInt()), points.get(sc.nextInt())));
        }
        sc.close();
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
    
    ArrayList<Vec> getPoints(){
        return this.points;
    }
    
    public ArrayList<Line> getSides(){
        return this.sides;
    }
    
    public Vec getMin() {
        return this.minPoint;
    }
    
    public Vec getMax() {
        return this.maxPoint;
    }
}
