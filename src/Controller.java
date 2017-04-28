import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class Controller  implements ActionListener{
    private final Robot model;
    private final Window window;
    
    private boolean autoRunning = false;
    private boolean forceStopped = false;
    
    public Controller(Robot robot, Window window){
        super();
        this.model = robot;
        this.window = window;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case Values.stepActionName:
                this.disableStartButtons();
                this.forceStopped = false;
                this.model.stepFCD();
                this.window.placeSensors(this.model.getNewSensors());
                this.window.moveRobotToPosition(this.model.pos);
                this.enableStartButtons();
                break;
            case Values.autoRunActionKey:
                if(e.getSource() == this.window.autoMoveRobotButtonInvisible && this.forceStopped)
                    return;
                
                this.disableStartButtons();
                if(!this.autoRunning){
                    this.forceStopped = false;
                }
                this.autoRunning = true;
                this.model.stepFCD();
                this.window.placeSensors(this.model.getNewSensors());
                this.window.moveRobotToPosition(this.model.pos);
                if(!this.model.fcdEnded() && !forceStopped){
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            try {
                                Thread.sleep(Values.robotDrawingDelayMillisec);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            window.autoMoveRobotButtonInvisible.doClick();
                        }
                    });
                }else if(this.model.fcdEnded() && !forceStopped){
                    this.enableStartButtons();
                    this.forceStopped = false;
                    this.autoRunning = false;
                }
                break;
            case Values.stopRunActionKey:
                this.forceStopped = true;
                this.autoRunning = false;
                this.enableStartButtons();
                break;
            default:
        }
    }
    
    private void disableStartButtons(){
        this.window.autoMoveRobotButton.setEnabled(false);
        this.window.moveRobotButton.setEnabled(false);
        this.window.stopMovingRobotButton.setEnabled(true);
    }
    
    private void enableStartButtons(){
        this.window.autoMoveRobotButton.setEnabled(true);
        this.window.moveRobotButton.setEnabled(true);
        this.window.stopMovingRobotButton.setEnabled(false);
    }
}
