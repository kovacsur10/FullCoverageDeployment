import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Controller  implements ActionListener{
    private final Robot model;
    private final Window window;
    
    private boolean autoRunning = false;
    private boolean forceStopped = false;
    private boolean alreadyMoved = false;
    
    private ArrayList<Sensor> sensorsToReach;
    
    public Controller(Robot robot, Window window){
        super();
        this.model = robot;
        this.window = window;
        this.window.setRobotPosition(this.model.pos);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        this.window.addNewSides(this.model.getNewSides());
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
                    this.enableStartButtons();
                }
                break;
            case Values.stepActionName:
                this.forceStopped = false;
                if(this.model.stepFCD()){
                    this.disableButtons();
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
                }
                break;
            case Values.autoRunActionKey: //TODO: recreate this
                /*if(e.getSource() == this.window.autoMoveRobotButtonInvisible && this.forceStopped)
                    return;
                
                this.disableStartButtons();
                if(!this.autoRunning){
                    this.forceStopped = false;
                }
                this.autoRunning = true;
                this.model.stepFCD();
                //this.window.placeSensors(this.model.getNewSensors());
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
                }*/
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
}
