
import Actions.AutoStepAction;
import Actions.StepAction;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;

public class View {
    private ROI roi;
    private final Controller controller;
    private final Robot model;
    private final JFrame frame;
    
    public View(){
        //create model
        try {
            this.roi = new ROI("map_hun.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.model = new Robot(this.roi, new Vec(0,0));
        
        //create and set up view
        this.frame = new JFrame("Beadandó");
        this.frame.setSize(450, 450);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
        Window window = new Window(600, 450, 50.0f, new Vec(100, 350));
        window.setSides(this.roi.sides);
        this.frame.add(window);
        this.frame.pack();
        
        this.frame.setVisible(true);
        
        //create controller and event listeners
        this.controller = new Controller(this.model, window);
        window.moveRobotButton.addActionListener(this.controller);
        window.autoMoveRobotButton.addActionListener(this.controller);
        window.autoMoveRobotButtonInvisible.addActionListener(this.controller);
        window.stopMovingRobotButton.addActionListener(this.controller);
        window.animationEndRobotButtonInvisible.addActionListener(this.controller);
    }
}
