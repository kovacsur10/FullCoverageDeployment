package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;
import model.*;
import src.Values;
import view.*;

public class Controller implements ActionListener{
    private final FullCoverage model;
    private final Window window;

    private boolean autoRunning = false;
    private boolean forceStopped = false;
    private boolean alreadyMoved = false;

    private final Timer callbackTimer;

    private ArrayList<Sensor> sensorsToReach;

    public Controller(FullCoverage model, Window window){
        super();
        this.model = model;
        this.window = window;
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
        if(this.model.isLoadedMap()) {
            switch(e.getActionCommand()){
            case Values.animationStoppedKey:
                if(!this.sensorsToReach.isEmpty()){
                    Sensor s = this.sensorsToReach.get(0);
                    this.window.placeSensor(new Sensor(s.getCoordinates(), s.getSequenceNumber(), s.getState(), s.getPreviousSensor()));
                    this.sensorsToReach.remove(0);
                }else if(!this.alreadyMoved){
                    this.alreadyMoved = true;
                    this.window.moveRobotToPosition(new Vec(this.model.getRobot().getPosition()));
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
                this.window.disableButtons();
                break;
            default:
            }
        }
    }

    private boolean step(boolean repeatable){
        if(this.model.getRobot().stepFCD()){
            if(repeatable) {
                this.window.disableStartButtons();
            }else{
                this.window.disableButtons();
            }
            this.window.addNewSides(this.model.getRobot().getNewSides());
            this.sensorsToReach = this.model.getRobot().getNewSensors();
            if(!this.sensorsToReach.isEmpty()){
                if(this.sensorsToReach.get(this.sensorsToReach.size()-1).getCoordinates().equals(this.model.getRobot().getPosition())){
                    this.alreadyMoved = true;
                }else{
                    this.alreadyMoved = false;
                }
                Sensor s = this.sensorsToReach.get(0);
                this.window.placeSensor(new Sensor(s.getCoordinates(), s.getSequenceNumber(), s.getState(), s.getPreviousSensor()));
                this.sensorsToReach.remove(0);
            }else{
                this.window.moveRobotToPosition(new Vec(this.model.getRobot().getPosition()));
                this.alreadyMoved = true;
            }
        }else if(this.autoRunning){
            this.forceStopped = true;
            return false;
        }
        return true;
    }

    private void cleanup(){
        this.window.enableStartButtons();
        this.autoRunning = false;
        this.forceStopped = false;
    }
}
