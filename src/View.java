
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class View {
    private ROI roi;
    private final Controller controller;
    private final Robot model;
    private final JFrame frame;
    public JButton moveRobotButton;
    
    public View(){
        //create model
        try {
            this.roi = new ROI("map.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.model = new Robot(this.roi, new Vec(0,0));
        
        //create and set up view
        this.frame = new JFrame("Beadandó");
        this.frame.setSize(450, 450);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenu menu = new JMenu("Main");
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        this.moveRobotButton = new JButton("Next step");
        menuBar.add(this.moveRobotButton);
        
        Window window = new Window(450, 450, 50.0f, new Vec(220, 140));
        window.setSides(this.roi.sides);
        window.add(menuBar);
        this.frame.add(window);
        this.frame.pack();
        
        this.frame.setVisible(true);
        
        //create controller and event listeners
        this.controller = new Controller(this.model, window);
        this.moveRobotButton.addActionListener(this.controller);
    }
}
