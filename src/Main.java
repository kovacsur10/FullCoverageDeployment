import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        ROI roi = new ROI("map.txt");
        Robot robot = new Robot(roi, new Vec(0,0));
        robot.FCD();
    }
}
