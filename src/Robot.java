import java.util.ArrayList;

public class Robot {

    static final double visibility = 3;
    static final double grid = Math.sqrt(2) * Sensor.sensing;

    public enum Direction {
        EAST(0),
        NORTH_EAST(Math.PI / 4),
        NORTH(Math.PI / 2),
        NORTH_WEST(Math.PI * 3 / 4),
        WEST(Math.PI),
        SOUTH_WEST(Math.PI * 5 / 4),
        SOUTH(Math.PI * 3 / 2),
        SOUTH_EAST(Math.PI * 7 / 4);

        Direction(double rad) {
            this.rad = rad;
        }

        double rad;
    }

    public Robot(ROI roi, Vec start) {
        this.roi = roi;
        this.pos = start;
        this.sensors = new ArrayList<>();
        this.newSensorVecs = new ArrayList<>();
        this.fcdEnded = false;
        this.started = false;
        //TODO: set robot position on screen
    }

    Direction[] mainDir = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    Direction[] cornerDir = {Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST};

    public void stepFCD() {
        if (!this.started) {
            putSensor(new Sensor(pos, sensors.size(), Sensor.State.REGULAR, null));
            this.started = true;
        } else if (!this.fcdEnded) {
            if (criticalAreas()) return;
            if (boundaryHandling()) return;
            int i;
            for (i = 0; i < mainDir.length &&
                    (roi.dist(pos, mainDir[i].rad, visibility) < grid || sensorAt(nextGrid(pos, mainDir[i])) != null); ++i)
                ;
            if (i == mainDir.length) {
                Sensor back = sensorAt(pos).backPtr;
                if (back == null) {
                    this.fcdEnded = true;
                    return;
                }
                move(back.coord);
            } else {
                Sensor prev = sensorAt(pos);
                move(nextGrid(pos, mainDir[i]));
                putSensor(new Sensor(pos, sensors.size(), Sensor.State.REGULAR, prev));
            }
        }
    }

    boolean boundaryHandling() {
        ArrayList<Vec> BSensors = new ArrayList<>();
        for (Direction dir : mainDir) {
            double dist = roi.dist(pos, dir.rad, visibility);
            if (dist > grid / 2 && dist < grid)
                BSensors.add(nextGrid(pos, dir, dist));
        }
        for (Direction dir : cornerDir) {
            double dist = roi.dist(pos, dir.rad, visibility);
            if (dist > Sensor.sensing && dist < 2 * Sensor.sensing)
                BSensors.add(nextGrid(pos, dir, dist));
        }
        BSensors.removeIf(v->isCovered(v));
        if (!BSensors.isEmpty()) {
            Vec u = pos;
            for (Vec pos : BSensors) {
                move(pos);
                putSensor(new Sensor(pos, sensors.size(), Sensor.State.BOUNDARY, sensorAt(u)));
            }
            move(u);
            return true;
        }
        return false;
    }

    boolean criticalAreas() {
        ArrayList<Vec> polygon = new ArrayList<>();
        for (Vec v : roi.points) {
            if (pos.dist(v) > visibility) continue;
            Line ray = new Line(pos, v);
            boolean visible = true;
            for (Line side : roi.sides)
                if (!v.equals(side.getP1()) && !v.equals(side.getP2()) && ray.intersect(side) != null) {
                    visible = false;
                    break;
                }
            if (visible)
                polygon.add(v);
        }
        polygon.sort((Vec a, Vec b) -> (int)Math.signum(Math.atan2(a.y - pos.y, a.x - pos.x) - Math.atan2(b.y - pos.y, b.x - pos.x)));
        for (int i = 0; i < polygon.size() - 1; ++i) {
            if (nextQuadrant(polygon.get(i), polygon.get(i + 1))) continue;
            Line edge = new Line(polygon.get(i), polygon.get(i + 1));
            if (roi.sides.contains(edge)) continue;
            Vec u = pos,
                    entrance = edge.getP1().add(edge.getP2()).mul(0.5),
                    shift = entrance.sub(u).mul(Vec.eps);
            entrance = entrance.add(shift);
            move(entrance);
            edge.isEntrance = true;
            roi.sides.add(edge); //TODO: add side to window
            putSensor(new Sensor(pos, sensors.size(), Sensor.State.ENTRANCE, sensorAt(u)));
            return true;
        }
        return false;
    }

    boolean nextQuadrant(Vec a, Vec b) {
        double rad1 = Math.atan2(a.y - pos.y, a.x - pos.x);
        double rad2 = Math.atan2(b.y - pos.y, b.x - pos.x);
        for (int i = -1; i <= 1; ++i)
            if (rad1 < i * Math.PI / 2 && rad2 > i * Math.PI / 2)
                return true;
        return false;
    }

    Vec nextGrid(Vec pos, Direction dir) {
        return new Vec(pos.x + Math.cos(dir.rad) * grid, pos.y + Math.sin(dir.rad) * grid);
    }

    Vec nextGrid(Vec pos, Direction dir, double step) {
        return new Vec(pos.x + Math.cos(dir.rad) * step, pos.y + Math.sin(dir.rad) * step);
    }

    Sensor sensorAt(Vec pos) {
        for (Sensor s : sensors)
            if (s.coord.equals(pos)) return s;
        return null;
    }

    boolean isCovered(Vec pos) {
        for (Sensor s : sensors)
            if (pos.dist(s.coord) <= Sensor.sensing)
                return true;
        return false;
    }

    void putSensor(Sensor s) {
        this.sensors.add(s);
        this.newSensorVecs.add(s.coord);
        System.out.println("sensor " + pos.x + " " + pos.y);
    }

    void move(Vec pos) {
        this.pos = pos;
        System.out.println("move " + pos.x + " " + pos.y);
    }

    public ArrayList<Vec> getNewSensors() {
        ArrayList<Vec> tmpNewSensorVecs = new ArrayList<Vec>(this.newSensorVecs);
        this.newSensorVecs.clear();
        return tmpNewSensorVecs;
    }

    public boolean fcdEnded() {
        return fcdEnded;
    }

    ROI roi;
    Vec pos;
    ArrayList<Sensor> sensors;
    private ArrayList<Vec> newSensorVecs;
    private boolean fcdEnded;
    private boolean started;
}
