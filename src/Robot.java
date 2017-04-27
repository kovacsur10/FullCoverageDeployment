import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public Robot(ROI roi, Vec start, Window window) {
        this.roi = roi;
        this.pos = start;
        this.sensors = new ArrayList<>();
        this.window = window;
        this.window.setRobotPosition(this.pos);
    }

    Direction[] mainDir = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    Direction[] cornerDir = {Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST};

    public void FCD() {
        putSensor(new Sensor(pos, sensors.size(), Sensor.State.REGULAR, null));
        while (true) {
            boundaryHandling();
            int i;
            for (i = 0; i < mainDir.length &&
                    (roi.dist(pos, mainDir[i].rad, visibility) < grid || sensorAt(nextGrid(pos, mainDir[i])) != null); ++i)
                ;
            if (i == mainDir.length) {
                Sensor back = sensorAt(pos).backPtr;
                if (back == null) break;
                move(back.coord);
            }
            else {
                Sensor prev = sensorAt(pos);
                move(nextGrid(pos, mainDir[i]));
                putSensor(new Sensor(pos, sensors.size(), Sensor.State.REGULAR, prev));
            }
        }
    }

    void boundaryHandling() {
        ArrayList<Vec> BSensors = new ArrayList<>();
        for(Direction dir : mainDir) {
            double dist = roi.dist(pos, dir.rad, visibility);
            if(dist > grid/2 && dist < grid)
                BSensors.add(nextGrid(pos, dir, dist));
        }
        for(Direction dir : cornerDir) {
            double dist = roi.dist(pos, dir.rad, visibility);
            if(dist > Sensor.sensing && dist < 2*Sensor.sensing && !isCovered(nextGrid(pos, dir, dist)))
                BSensors.add(nextGrid(pos, dir, dist));
        }
        if(!BSensors.isEmpty()) {
            Vec u = pos;
            for (Vec pos : BSensors) {
                move(pos);
                putSensor(new Sensor(pos, sensors.size(), Sensor.State.BOUNDARY, sensorAt(u)));
            }
            move(u);
        }
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
        for(Sensor s: sensors)
            if(pos.dist(s.coord) <= Sensor.sensing)
                return true;
        return false;
    }

    void putSensor(Sensor s) {
        this.sensors.add(s);
        System.out.println("sensor " + pos.x + " " + pos.y);
        this.window.placeSensor(pos);
    }

    void move(Vec pos) {
        this.pos = pos;
        System.out.println("move " + pos.x + " " + pos.y);
        this.window.moveRobotToPosition(pos);
    }

    ROI roi;
    Vec pos;
    ArrayList<Sensor> sensors;
    Window window;
}
