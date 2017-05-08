import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

public class Controller  implements ActionListener{
    private final Robot model;
    private final Window window;
    
    private boolean autoRunning = false;
    private boolean forceStopped = false;
    private boolean alreadyMoved = false;
    
    private final Timer callbackTimer;
    
    private ArrayList<Sensor> sensorsToReach;
    
    public Controller(Robot robot, Window window){
        super();
        this.model = robot;
        this.window = window;
        this.window.setRobotPosition(this.model.pos);
        this.callbackTimer = new Timer(Values.autoPlayingWaitingTime, this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == this.callbackTimer){
            this.callbackTimer.stop();
            if(this.forceStopped){
                this.cleanup();
            }else{
                if(!step(true)){
                    this.cleanup();
                }
            }
            return;
        }
        switch(e.getActionCommand()){
            case Values.animationStoppedKey:
                if(!this.sensorsToReach.isEmpty()){
                    Sensor s = this.sensorsToReach.get(0);
                    this.window.placeSensor(new Sensor(s.coord, s.seqNum, s.state, s.backPtr));
                    this.sensorsToReach.remove(0);
                }else if(!this.alreadyMoved){
                    this.alreadyMoved = true;
                    this.window.moveRobotToPosition(new Vec(this.model.pos));
                }else if(this.alreadyMoved){
                    if(this.autoRunning && !this.forceStopped){
                        this.callbackTimer.restart();
                    }else{
                        this.cleanup();
                    }
                }
                break;
            case Values.stepActionName:
                step(false);
                break;
            case Values.autoRunActionKey:
                this.autoRunning = true;
                step(true);
                break;
            case Values.stopRunActionKey:
                this.forceStopped = true;
                this.disableButtons();
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
    
    private void enableButtons(){
        this.window.autoMoveRobotButton.setEnabled(true);
        this.window.moveRobotButton.setEnabled(true);
        this.window.stopMovingRobotButton.setEnabled(true);
    }
    
    private void disableButtons(){
        this.window.autoMoveRobotButton.setEnabled(false);
        this.window.moveRobotButton.setEnabled(false);
        this.window.stopMovingRobotButton.setEnabled(false);
    }
    
    private boolean step(boolean repeatable){
        if(this.model.stepFCD()){
            if(repeatable)
                this.disableStartButtons();
            else
                this.disableButtons();
            this.window.addNewSides(this.model.getNewSides());
            this.sensorsToReach = this.model.getNewSensors();
            if(!this.sensorsToReach.isEmpty()){
                if(this.sensorsToReach.get(this.sensorsToReach.size()-1).coord.equals(this.model.pos)){
                    this.alreadyMoved = true;
                }else{
                    this.alreadyMoved = false;
                }
                Sensor s = this.sensorsToReach.get(0);
                this.window.placeSensor(new Sensor(s.coord, s.seqNum, s.state, s.backPtr));
                this.sensorsToReach.remove(0);
            }else{
                this.window.moveRobotToPosition(new Vec(this.model.pos));
                this.alreadyMoved = true;
            }
        }else if(this.autoRunning){
            this.forceStopped = true;
            return false;
        }
        return true;
    }
    
    private void cleanup(){
        this.enableStartButtons();
        this.autoRunning = false;
        this.forceStopped = false;
    }
}
