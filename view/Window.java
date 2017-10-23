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
    private final int sensorSensingRadius;
    private final int robotSensingRadius;

    private ArrayList<Line> sides;
    private final ArrayList<Sensor> sensors;
    private Vec robotPosition;
    private Vec robotRealWorldPosition = new Vec(0,0);
    private final Vec offset;
    private final int width;
    private final int height;
    private final float scale;
    private int normalSides;

    public JButton moveRobotButton;
    public JButton autoMoveRobotButton;
    public JButton stopMovingRobotButton;
    public JButton autoMoveRobotButtonInvisible;
    public JButton animationEndRobotButtonInvisible;
    public JMenuItem openMapMenuItem;

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

    public Window(Vec dimensions, double scale, Vec offset){       
        this.offset = offset;

        this.scale = (float) scale;
        this.sensors = new ArrayList<>();
        this.sides = new ArrayList<>();
        this.robotPosition = new Vec(0,0);

        this.width = Math.round((float) dimensions.x);
        this.height = Math.round((float) dimensions.y);

        this.sensorSensingRadius = Math.round(((float)Values.sensorSensing) * this.scale * 2);
        this.robotSensingRadius = Math.round(((float)Values.robotSensing) * this.scale * 2);

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
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(this.width, this.height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for(int i = 0; i < this.sides.size(); i++) {
            Line l = this.sides.get(i);
            Line2D line = new Line2D.Float(Math.round(l.getP1().x), Math.round(l.getP1().y), Math.round(l.getP2().x), Math.round(l.getP2().y));
            if(i >= normalSides){
                g2.setColor(Color.red);
            }
            g2.draw(line);
            g2.setColor(Color.black);
        }

        if(this.filterRobotRadius.isSelected()){
            Vec vecRad = this.robotPosition.sub(this.robotOffset).sub(new Vec(this.robotSensingRadius/2.0, this.robotSensingRadius/2.0));
            g2.drawRoundRect(Math.round((float)vecRad.x), Math.round((float)vecRad.y), this.robotSensingRadius, this.robotSensingRadius, this.robotSensingRadius, this.robotSensingRadius);
        }
        g2.drawRoundRect(Math.round((float)this.robotPosition.x), Math.round((float)this.robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);
        g2.fillRoundRect(Math.round((float)this.robotPosition.x), Math.round((float)this.robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);

        this.sensors.forEach((Sensor sen) -> {
            Vec vec = sen.coord;
            Vec vecRad = vec.sub(new Vec(this.sensorSensingRadius/2.0, sensorSensingRadius/2.0));
            vec = vec.add(this.sensorOffset);
            if(this.filterSensorRadius.isSelected()){
                g2.setColor(Color.lightGray);
                g2.drawRoundRect(Math.round((float)vecRad.x), Math.round((float)vecRad.y), this.sensorSensingRadius, this.sensorSensingRadius, this.sensorSensingRadius, this.sensorSensingRadius);
                g2.setColor(Color.black);
            }
            if(sen.state == Sensor.State.BOUNDARY){
                g2.setColor(Color.green);
            }else if(sen.state == Sensor.State.ENTRANCE){
                g2.setColor(Color.red);
            }else{
                g2.setColor(Color.darkGray);
            }
            g2.drawRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
            g2.fillRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
            g2.setColor(Color.black);
        });
    }

    public void setSides(ArrayList<Line> sides){
        this.normalSides = sides.size();
        this.sides = new ArrayList<>();
        sides.forEach((l) -> {
            this.sides.add(new Line(transformVec(l.getP1()), transformVec(l.getP2())));
        });
    }

    public void addNewSides(ArrayList<Line> sides){
        sides.forEach((l) -> {
            this.sides.add(new Line(transformVec(l.getP1()), transformVec(l.getP2())));
        });
    }

    public void placeSensor(Sensor sensor){
        this.sensorToPut = sensor;
        this.moveRobotToPosition(sensor.coord);
    }

    public void setRobotPosition(Vec position){
        this.robotRealWorldPosition = position;
        this.robotPosition = transformVec(position).add(this.robotOffset);
        this.repaint();
    }

    public void moveRobotToPosition(Vec position){
        this.animationIndex = 0;
        this.animationIndexBoundary = Values.robotMovementAnimationTime / this.animationDelayMillisec;
        this.animationEndPoint = position;
        this.animationPosition = this.robotRealWorldPosition;
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
                this.sensorToPut.coord = transformVec(this.sensorToPut.coord);
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

    private Vec transformVec(Vec vec){
        return vec.mul(this.scale).add(this.offset);
    }
}