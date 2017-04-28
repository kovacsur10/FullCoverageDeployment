import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller  implements ActionListener{
    private final Robot model;
    private final Window window;
    
    private final int robotDrawingDelayMillisec = 1000;
    private final int sensorDrawingDelayMillisec = 500;
    
    public Controller(Robot robot, Window window){
        super();
        this.model = robot;
        this.window = window;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        this.model.stepFCD();
        this.window.placeSensors(this.model.getNewSensors());
        this.window.setRobotPosition(this.model.pos);
    }
}
