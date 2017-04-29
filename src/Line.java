import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line extends Line2D.Double {

    public Line(Vec a, Vec b) {
        super(a, b);
    }

    @Override
    public Vec getP1() {
        return new Vec((Point2D.Double) super.getP1());
    }

    @Override
    public Vec getP2() {
        return new Vec((Point2D.Double) super.getP2());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Line)) return super.equals(obj);
        Line line = (Line)obj;
        return getP1().equals(line.getP1()) && getP2().equals(line.getP2()) ||
                getP2().equals(line.getP1()) && getP1().equals(line.getP2());
    }

    Vec intersect(Line S2) {
        Vec u = this.getP2().sub(this.getP1());
        Vec v = S2.getP2().sub(S2.getP1());
        Vec w = this.getP1().sub(S2.getP1());
        double D = u.perp(v);

        // test if  they are parallel (includes either being a point)
        if (Math.abs(D) < Vec.eps) {           // S1 and S2 are parallel
            if (u.perp(w) != 0 || v.perp(w) != 0) {
                return null;                    // they are NOT collinear
            }
            // they are collinear or degenerate
            // check if they are degenerate  points
            double du = u.dot(u);
            double dv = v.dot(v);
            if (du == 0 && dv == 0) {            // both segments are points
                if (!this.getP1().equals(S2.getP1()))         // they are distinct  points
                    return null;
                else               // they are the same point
                    return this.getP1();
            }
            if (du == 0) {                     // S1 is a single point
                if (!S2.inside(getP1()))  // but is not in S2
                    return null;
                return getP1();
            }
            if (dv == 0) {                     // S2 a single point
                if (!inside(S2.getP1()))  // but is not in S1
                    return null;
                return S2.getP1();
            }
            // they are collinear segments - get  overlap (or not)
            double t0, t1;                    // endpoints of S1 in eqn for S2
            Vec w2 = getP2().sub(S2.getP1());
            if (v.x != 0) {
                t0 = w.x / v.x;
                t1 = w2.x / v.x;
            } else {
                t0 = w.y / v.y;
                t1 = w2.y / v.y;
            }
            if (t0 > t1) {                   // must have t0 smaller than t1
                double t = t0;
                t0 = t1;
                t1 = t;    // swap if not
            }
            if (t0 > 1 || t1 < 0) {
                return null;      // NO overlap
            }
            t0 = t0 < 0 ? 0 : t0;               // clip to min 0
            t1 = t1 > 1 ? 1 : t1;               // clip to max 1
            //if (t0 == t1) {                  // intersect is a point
            return S2.getP1().add(v.mul(t0));
            //}

            // they overlap in a valid subsegment
            //*I0 = S2.P0 + t0 * v;
            //*I1 = S2.P0 + t1 * v;
            //return 2;
        }

        // the segments are skew and may intersect in a point
        // get the intersect parameter for S1
        double sI = v.perp(w) / D;
        if (sI < 0 || sI > 1)                // no intersect with S1
            return null;

        // get the intersect parameter for S2
        double tI = u.perp(w) / D;
        if (tI < 0 || tI > 1)                // no intersect with S2
            return null;

        return getP1().add(u.mul(sI)); // compute S1 intersect point
    }

    boolean inside(Vec P) {
        if (getP1().x != getP2().x) {    // S is not  vertical
            if (getP1().x <= P.x && P.x <= getP2().x)
                return true;
            if (getP1().x >= P.x && P.x >= getP2().x)
                return true;
        } else {    // S is vertical, so test y  coordinate
            if (getP1().y <= P.y && P.y <= getP2().y)
                return true;
            if (getP1().y >= P.y && P.y >= getP2().y)
                return true;
        }
        return false;
    }

    boolean isEntrance = false;
}
