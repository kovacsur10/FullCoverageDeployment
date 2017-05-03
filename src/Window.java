import Actions.AutoStepAction;
import Actions.StepAction;
import Actions.StopStepAction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.ArrayList;

class Window extends JPanel implements ActionListener{
    private final int animationDelayMillisec = 100;
    private final int sensorRadius = 3;
    private final Vec sensorOffset = new Vec(-this.sensorRadius, -this.sensorRadius);
    private final int robotRadius = 5;
    private final Vec robotOffset = new Vec(-this.robotRadius, -this.robotRadius);
    private final int sensingRadius;
    
    private ArrayList<Line> sides;
    private final ArrayList<Sensor> sensors;
    private Vec robotPosition;
    private Vec robotRealWorldPosition = new Vec(0,0);
    private final Vec offset;
    private final int width;
    private final int height;
    private final float scale;
    
    public JButton moveRobotButton;
    public JButton autoMoveRobotButton;
    public JButton stopMovingRobotButton;
    public JButton autoMoveRobotButtonInvisible;
    public JButton animationEndRobotButtonInvisible;
    
    private JCheckBoxMenuItem filterSensorRadius;
    
    //animation variable
    private Timer timer;
    private Timer callbackTimer;
    private boolean isAnimating = false;
    private int animationIndex = 0;
    private int animationIndexBoundary = 0;
    private Vec animationDelta;
    private Vec animationPosition;
    private Vec animationEndPoint;
    private Sensor sensorToPut;
            
    public Window(int width, int height, float scale, Vec offset){       
        this.offset = offset;
        
        this.scale = scale;
        this.sensors = new ArrayList<>();
        this.sides = new ArrayList<>();
        this.robotPosition = new Vec(0,0);
        
        this.width = width;
        this.height = height;
        
        this.sensingRadius = Math.round(((float)Math.sqrt(2)) * this.scale * 2);
        
        JMenu menu = new JMenu("Filters");
        this.filterSensorRadius = new JCheckBoxMenuItem(Values.filterShowSensorRadiusText);
        this.filterSensorRadius.setSelected(true);
        this.filterSensorRadius.addActionListener(this);
        menu.add(this.filterSensorRadius);
        
        JMenuBar menuBar = new JMenuBar();
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
        
        this.sides.forEach((l) -> {
            Line2D line = new Line2D.Float(Math.round(l.getP1().x), Math.round(l.getP1().y), Math.round(l.getP2().x), Math.round(l.getP2().y));
            g2.draw(line); 
        });
        
        this.sensors.forEach((Sensor sen) -> {
            Vec vec = sen.coord;
            Vec vecRad = vec.sub(new Vec(this.sensingRadius/2.0, sensingRadius/2.0));
            vec = vec.add(this.sensorOffset);
            if(sen.state == Sensor.State.BOUNDARY){
                g2.setColor(Color.lightGray);
            }else if(sen.state == Sensor.State.ENTRANCE){
                g2.setColor(Color.red);
            }else{
                g2.setColor(Color.black);
            }
            g2.drawRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
            g2.setColor(Color.black);
            if(this.filterSensorRadius.isSelected()){
                g2.drawRoundRect(Math.round((float)vecRad.x), Math.round((float)vecRad.y), this.sensingRadius, this.sensingRadius, this.sensingRadius, this.sensingRadius);
            }
        });
        
        g2.drawRoundRect(Math.round((float)this.robotPosition.x), Math.round((float)this.robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);
    }
    
    public void setSides(ArrayList<Line> sides){
        this.sides = new ArrayList<>();
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
        this.isAnimating = true;
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
            this.isAnimating = false;
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