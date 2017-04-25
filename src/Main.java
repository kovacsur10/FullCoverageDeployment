import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        ROI roi = new ROI("map.txt");
        
        Window window = new Window(450, 450, 50.0, new Vec(220, 140));
        window.setSides(roi.sides);
        
        Robot robot = new Robot(roi, new Vec(0,0), window);
        robot.FCD();
    }
}
