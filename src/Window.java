import Actions.AutoStepAction;
import Actions.StepAction;
import Actions.StopStepAction;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

class Window extends JPanel{
    private final int animationDelayMillisec = 100;
    private final int sensorRadius = 3;
    private final Vec sensorOffset = new Vec(-this.sensorRadius, -this.sensorRadius);
    private final int robotRadius = 5;
    private final Vec robotOffset = new Vec(-this.robotRadius, -this.robotRadius);
    private final int sensingRadius;
    
    private ArrayList<Line> sides;
    private final ArrayList<Vec> sensors;
    private Vec robotPosition;
    private final Vec offset;
    private final int width;
    private final int height;
    private final float scale;
    
    public JButton moveRobotButton;
    public JButton autoMoveRobotButton;
    public JButton stopMovingRobotButton;
    public JButton autoMoveRobotButtonInvisible;
    
    //animation variable
    private boolean isAnimating = false;
    private int animationIndex = 0;
    private int animationIndexBoundary = 0;
    private Vec animationDelta;
            
    public Window(int width, int height, float scale, Vec offset){       
        this.offset = offset;
        
        this.scale = scale;
        this.sensors = new ArrayList<>();
        this.sides = new ArrayList<>();
        this.robotPosition = new Vec(0,0);
        
        this.width = width;
        this.height = height;
        
        this.sensingRadius = Math.round(((float)Math.sqrt(2)) * this.scale * 2);
        
        JMenu menu = new JMenu("Main");
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
        
        button = new JButton(new StopStepAction(Values.stopRunActionKey));
        button.setFocusable(true);
        button.setEnabled(false);
        this.stopMovingRobotButton = button;
        menuBar.add(this.stopMovingRobotButton);
        
        this.add(menuBar);
    }
    
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(this.width, this.height);
    }

    @Override
    public void paintComponent(Graphics g) {
        System.out.println("PRINT");
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        this.sides.forEach((l) -> {
            Line2D line = new Line2D.Float(Math.round(l.getP1().x), Math.round(l.getP1().y), Math.round(l.getP2().x), Math.round(l.getP2().y));
            g2.draw(line); 
        });
        
        this.sensors.forEach((vec) -> {
            Vec vecRad = vec.sub(new Vec(this.sensingRadius/2.0, sensingRadius/2.0));
            vec = vec.add(this.sensorOffset);
            g2.drawRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
            g2.drawRoundRect(Math.round((float)vecRad.x), Math.round((float)vecRad.y), this.sensingRadius, this.sensingRadius, this.sensingRadius, this.sensingRadius);
        });
        
        g2.drawRoundRect(Math.round((float)this.robotPosition.x), Math.round((float)this.robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);
    }
    
    public void setSides(ArrayList<Line> sides){
        this.sides = new ArrayList<>();
        sides.forEach((l) -> {
            this.sides.add(new Line(transformVec(l.getP1()), transformVec(l.getP2())));
        });
    }
    
    public void placeSensors(ArrayList<Vec> position){
        position.forEach((pos) -> {
            this.sensors.add(transformVec(pos));
        });
        this.repaint();
    }
    
    public void setRobotPosition(Vec position){
        this.robotPosition = transformVec(position).add(this.robotOffset);
        this.repaint();
    }
    
    public void moveRobotToPosition(Vec position){
        if(this.isAnimating && this.animationIndex == this.animationIndexBoundary){
            this.setRobotPosition(position);
            this.isAnimating = false;
            return;
        }
        if(!this.isAnimating){
            this.isAnimating = true;
            this.animationIndex = 0;
            this.animationIndexBoundary = Values.robotMovementAnimationTime / this.animationDelayMillisec;
            this.animationDelta = position.sub(this.robotPosition).mul((double)this.animationDelayMillisec / (double) Values.robotMovementAnimationTime);
        }
        this.robotPosition = this.robotPosition.add(this.animationDelta);
        this.animationIndex++;
        this.repaint();
    }
    
    private Vec transformVec(Vec vec){
        return vec.mul(this.scale).add(this.offset);
    }
}