import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class View implements ActionListener{
    private ROI roi;
    private Controller controller;
    private Robot model;
    private JFrame frame;
    final JFileChooser mapOpenFileChooser = new JFileChooser();
    private Window window;
    private String filePath;
    private final String defaultFilePath = "map.txt";
    
    public View(){
        this.filePath = this.defaultFilePath;
        this.reset();
    }
    
    private void reset(){
        if(this.frame != null){
            this.frame.remove(this.window);
        }
        
        //create model
        try {
            this.roi = new ROI(this.filePath);
        } catch (FileNotFoundException ex) {
            if(this.filePath.contentEquals(this.defaultFilePath)){
                JOptionPane.showMessageDialog(null, "Default map cannot be loaded!");
            }else{
                this.filePath = this.defaultFilePath;
                this.reset();
            }
            return;
        }
        this.model = new Robot(this.roi, new Vec(0,0));
        
        //create and set up view
        if(this.frame == null){
            this.frame = new JFrame("Coverage Project");
            this.frame.setSize(450, 450);
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
                
        this.window = new Window(this.roi.windowDimensions, this.roi.windowScale, this.roi.windowOffset);
        this.window.setSides(this.roi.sides);
        this.frame.add(this.window);
        this.frame.pack();
        
        this.frame.setVisible(true);
        
        //create controller and event listeners
        this.controller = new Controller(this.model, this.window);
        this.window.moveRobotButton.addActionListener(this.controller);
        this.window.autoMoveRobotButton.addActionListener(this.controller);
        this.window.autoMoveRobotButtonInvisible.addActionListener(this.controller);
        this.window.stopMovingRobotButton.addActionListener(this.controller);
        this.window.animationEndRobotButtonInvisible.addActionListener(this.controller);
        this.window.openMapMenuItem.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.window.openMapMenuItem){
            int returnVal = this.mapOpenFileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.mapOpenFileChooser.getSelectedFile();
                this.filePath = file.getPath();
                this.reset();
            }
        }
    }
}
