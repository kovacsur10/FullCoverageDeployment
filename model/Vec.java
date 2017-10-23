package model;
import java.awt.geom.Point2D;

public class Vec extends Point2D.Double {

    static final long serialVersionUID = 200L;
    static final double eps = 1e-8;

    public Vec(Point2D.Double p) {
        super(p.x, p.y);
    }

    public Vec(double x, double y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object p) {
        if (!(p instanceof Vec)) return false;
        return sub((Vec) p).norm() < eps;
    }

    public Vec add(Vec p) {
        return new Vec(this.x + p.x, this.y + p.y);
    }

    public Vec sub(Vec p) {
        return new Vec(this.x - p.x, this.y - p.y);
    }

    public Vec mul(double a) {
        return new Vec(a * this.x, a * this.y);
    }

    public double dot(Vec v) {
        return x * v.x + y * v.y;
    }

    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    public double perp(Vec v) {
        return x * v.y - y * v.x;
    }

    public double dist(Vec v) {
        return sub(v).norm();
    }
}
