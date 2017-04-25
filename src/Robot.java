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
    }

    public void FCD() {
        Direction[] mainDir = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
        Direction[] cornerDir = {Direction.NORTH_WEST, Direction.NORTH_EAST, Direction.SOUTH_WEST, Direction.SOUTH_EAST};
        putSensor(new Sensor(pos, sensors.size(), Sensor.State.REGULAR, null));
        while (true) {
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    Vec nextGrid(Vec pos, Direction dir) {
        return new Vec(pos.x + Math.cos(dir.rad) * grid, pos.y + Math.sin(dir.rad) * grid);
    }

    Sensor sensorAt(Vec pos) {
        for (Sensor s : sensors)
            if (s.coord.equals(pos)) return s;
        return null;
    }

    void putSensor(Sensor s) {
        sensors.add(s);
        System.out.println("sensor " + pos.x + " " + pos.y);
        window.placeSensor(pos);
    }

    void move(Vec pos) {
        this.pos = pos;
        System.out.println("move " + pos.x + " " + pos.y);
        window.setRobotPosition(pos);
    }

    ROI roi;
    Vec pos;
    ArrayList<Sensor> sensors;
    Window window;
}
