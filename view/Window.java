package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.ArrayList;
import model.*;
import src.Values;
import controller.action.AutoStepAction;
import controller.action.StepAction;
import controller.action.StopStepAction;

public class Window extends JPanel implements ActionListener{
    private static final long serialVersionUID = 6112332582215032445L;
    private final int animationDelayMillisec = 100;
    private final int sensorRadius = 3;
    private final Vec sensorOffset = new Vec(-this.sensorRadius, -this.sensorRadius);
    private final int robotRadius = 5;
    private final Vec robotOffset = new Vec(-this.robotRadius, -this.robotRadius);
    
    private int sensorSensingRadius;
    private int robotSensingRadius;
    
    // Model --> View transformation values
    private final int topMargin = 50; //because of the menu
    private Vec margin;
    private float scale;
    private Vec offset;
    private Vec minimumPoint;
    private Vec maximumPoint;
    
    private final ArrayList<Line> sides;
    private final ArrayList<Sensor> sensors;
    private Vec robotPosition = new Vec(0, 0);
    private int normalSides;
    private boolean loadedMap;

    JButton moveRobotButton;
    JButton autoMoveRobotButton;
    JButton stopMovingRobotButton;
    JButton autoMoveRobotButtonInvisible;
    JButton animationEndRobotButtonInvisible;
    JMenuItem openMapMenuItem;

    private final JCheckBoxMenuItem filterSensorRadius;
    private final JCheckBoxMenuItem filterRobotRadius;

    //animation variable
    private final Timer timer;
    private final Timer callbackTimer;
    private int animationIndex = 0;
    private int animationIndexBoundary = 0;
    private Vec animationDelta;
    private Vec animationPosition;
    private Vec animationEndPoint;
    private Sensor sensorToPut;

    public Window(Vec dimensions, Vec margin){
        this.loadedMap = false;
        this.sensors = new ArrayList<>();
        this.sides = new ArrayList<>();
        this.robotPosition = new Vec(0,0);
        
        this.scale = 1.0f;
        this.offset = new Vec(0,0);

        this.setPreferredSize(new Dimension(Math.round((float) dimensions.x), Math.round((float) dimensions.y)));
        this.margin = margin;

        JMenu menu = new JMenu("File");
        this.openMapMenuItem = new JMenuItem("Open map");
        menu.add(this.openMapMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        menu = new JMenu("Filters");
        this.filterSensorRadius = new JCheckBoxMenuItem(Values.filterShowSensorRadiusText);
        this.filterSensorRadius.setSelected(true);
        this.filterSensorRadius.addActionListener(this);
        menu.add(this.filterSensorRadius);

        this.filterRobotRadius = new JCheckBoxMenuItem(Values.filterShowRobotRadiusText);
        this.filterRobotRadius.setSelected(true);
        this.filterRobotRadius.addActionListener(this);
        menu.add(this.filterRobotRadius);

        menuBar.add(menu);
        JButton button = new JButton(new StepAction(Values.stepActionName));
        button.setFocusable(true);
        button.setEnabled(true);
        this.moveRobotButton = button;
        menuBar.add(this.moveRobotButton);

        button = new JButton(new AutoStepAction(Values.autoRunActionKey));
        button.setFocusable(true);
        button.setEnabled(true);
        this.autoMoveRobotButton = button;
        menuBar.add(this.autoMoveRobotButton);

        button = new JButton(new AutoStepAction(Values.autoRunActionKey));
        button.setFocusable(false);
        button.setEnabled(true);
        button.setVisible(false);
        this.autoMoveRobotButtonInvisible = button;
        menuBar.add(this.autoMoveRobotButtonInvisible);

        button = new JButton(new AutoStepAction(Values.animationStoppedKey));
        button.setFocusable(false);
        button.setEnabled(true);
        button.setVisible(false);
        this.animationEndRobotButtonInvisible = button;
        menuBar.add(this.animationEndRobotButtonInvisible);

        button = new JButton(new StopStepAction(Values.stopRunActionKey));
        button.setFocusable(true);
        button.setEnabled(false);
        this.stopMovingRobotButton = button;
        menuBar.add(this.stopMovingRobotButton);

        this.add(menuBar);
        this.timer = new Timer(Values.robotMovementAnimationTime, this);
        this.callbackTimer = new Timer(10, this);
        
        this.disableStartButtons();
        this.disableButtons();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(this.loadedMap) {
            Graphics2D g2 = (Graphics2D) g;
    
            for(int i = 0; i < this.sides.size(); i++) {
                Line l = this.sides.get(i);
                l = new Line(transformVec(l.getP1()), transformVec(l.getP2()));
                Line2D line = new Line2D.Float(Math.round(l.getP1().x), Math.round(l.getP1().y), Math.round(l.getP2().x), Math.round(l.getP2().y));
                if(i >= normalSides){
                    g2.setColor(Color.red);
                }
                g2.draw(line);
                g2.setColor(Color.black);
            }
    
            Vec robotPosition = transformVec(this.robotPosition).add(this.robotOffset);
            if(this.filterRobotRadius.isSelected()){
                Vec vecRad = robotPosition.sub(this.robotOffset).sub(new Vec(this.robotSensingRadius/2.0, this.robotSensingRadius/2.0));
                g2.drawRoundRect(Math.round((float)vecRad.x), Math.round((float)vecRad.y), this.robotSensingRadius, this.robotSensingRadius, this.robotSensingRadius, this.robotSensingRadius);
            }
            g2.drawRoundRect(Math.round((float)robotPosition.x), Math.round((float)robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);
            g2.fillRoundRect(Math.round((float)robotPosition.x), Math.round((float)robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);
    
            this.sensors.forEach((Sensor sen) -> {
                Vec vec = transformVec(sen.getCoordinates());
                Vec vecRad = vec.sub(new Vec(this.sensorSensingRadius/2.0, sensorSensingRadius/2.0));
                vec = vec.add(this.sensorOffset);
                if(this.filterSensorRadius.isSelected()){
                    g2.setColor(Color.lightGray);
                    g2.drawRoundRect(Math.round((float)vecRad.x), Math.round((float)vecRad.y), this.sensorSensingRadius, this.sensorSensingRadius, this.sensorSensingRadius, this.sensorSensingRadius);
                    g2.setColor(Color.black);
                }
                if(sen.getState() == Sensor.State.BOUNDARY){
                    g2.setColor(Color.green);
                }else if(sen.getState() == Sensor.State.ENTRANCE){
                    g2.setColor(Color.red);
                }else{
                    g2.setColor(Color.darkGray);
                }
                g2.drawRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
                g2.fillRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
                g2.setColor(Color.black);
            });
        }
    }
    
    public void onLoadMap(ArrayList<Line> sides, Vec min, Vec max) {
        this.loadedMap = true;
        this.minimumPoint = min;
        this.maximumPoint = max;
        this.onResize(this.getWidth(), this.getHeight());
        this.enableStartButtons();
        this.setSides(sides);
        this.sensors.clear();
        this.repaint();
    }

    private void setSides(ArrayList<Line> sides){
        this.normalSides = sides.size();
        this.sides.clear();
        addNewSides(sides);
    }

    public void addNewSides(ArrayList<Line> sides){
        this.sides.addAll(sides);
    }

    public void placeSensor(Sensor sensor){
        this.sensorToPut = sensor;
        this.moveRobotToPosition(sensor.getCoordinates());
    }

    public void setRobotPosition(Vec position){
        if(this.loadedMap) {
            this.robotPosition = position;
            this.repaint();
        }
    }

    public void moveRobotToPosition(Vec position){
        this.animationIndex = 0;
        this.animationIndexBoundary = Values.robotMovementAnimationTime / this.animationDelayMillisec;
        this.animationEndPoint = position;
        this.animationPosition = this.robotPosition;
        this.animationDelta = position.sub(this.animationPosition).mul((double)this.animationDelayMillisec / (double) Values.robotMovementAnimationTime);
        this.timer.setDelay(Values.robotMovementAnimationTime / this.animationIndexBoundary);
        this.timer.restart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.filterSensorRadius){
            this.repaint();
            return;
        }

        if(e.getSource() == this.callbackTimer){
            this.callbackTimer.stop();
            this.animationEndRobotButtonInvisible.doClick();
            return;
        }

        if(this.animationIndex >= this.animationIndexBoundary){
            this.timer.stop();
            this.animationPosition = this.animationEndPoint;
            if(this.sensorToPut != null){
                this.sensors.add(this.sensorToPut);
                this.sensorToPut = null;
            }
            this.callbackTimer.restart();
        }else{
            this.animationPosition = this.animationPosition.add(this.animationDelta);
        }
        this.animationIndex++;
        this.setRobotPosition(this.animationPosition);
    }
    
    public void disableStartButtons(){
        this.autoMoveRobotButton.setEnabled(false);
        this.moveRobotButton.setEnabled(false);
        this.stopMovingRobotButton.setEnabled(true);
    }

    public void enableStartButtons(){
        this.autoMoveRobotButton.setEnabled(true);
        this.moveRobotButton.setEnabled(true);
        this.stopMovingRobotButton.setEnabled(false);
    }

    public void enableButtons(){
        this.autoMoveRobotButton.setEnabled(true);
        this.moveRobotButton.setEnabled(true);
        this.stopMovingRobotButton.setEnabled(true);
    }

    public void disableButtons(){
        this.autoMoveRobotButton.setEnabled(false);
        this.moveRobotButton.setEnabled(false);
        this.stopMovingRobotButton.setEnabled(false);
    }
    
    Vec transformVec(Vec vec){        
        return vec.mul(this.scale).add(this.offset);
    }

    void onResize(int newWidth, int newHeight) {
        this.setPreferredSize(new Dimension(newWidth, newHeight));
        if(this.minimumPoint == null || this.maximumPoint == null) {
            return;
        }
        float mapWidth = (float) (this.maximumPoint.x - this.minimumPoint.x);
        float mapHeight = (float) (this.maximumPoint.y - this.minimumPoint.y);
        float scale = (float) ((this.getHeight() - 2 * this.margin.y - this.topMargin) / mapHeight);
        this.scale = (float) ((this.getWidth() - 2 * this.margin.x) / mapWidth);
        this.scale = this.scale < scale ? this.scale : scale;
        this.offset = new Vec(this.margin.x - (this.minimumPoint.x * this.scale), (this.margin.y + this.topMargin) - (this.minimumPoint.y * this.scale));
        this.sensorSensingRadius = Math.round(((float)Values.sensorSensing) * this.scale * 2);
        this.robotSensingRadius = Math.round(((float)Values.robotSensing) * this.scale * 2);
    }
}