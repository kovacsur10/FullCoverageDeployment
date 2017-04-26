import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class Window extends JFrame{
    private final int robotDrawingDelayMillisec = 1000;
    private final int sensorDrawingDelayMillisec = 500;
    private final int animationDelayMillisec = 100;
    private final int sensorRadius = 3;
    private final Vec sensorOffset = new Vec(-this.sensorRadius, -this.sensorRadius);
    private final int robotRadius = 5;
    private final Vec robotOffset = new Vec(-this.robotRadius, -this.robotRadius);
    
    private ArrayList<Line> sides;
    private ArrayList<Vec> sensors;
    private double scale;
    private Vec robotPosition;
    private Vec offset;
            
    public Window(int width, int height, double scale, Vec offset){
        JPanel panel=new JPanel();
        getContentPane().add(panel);
        setSize(width,height);
        this.offset = offset;
        
        this.setVisible(true);
        
        this.scale = scale;
        this.sensors = new ArrayList<Vec>();
        this.robotPosition = new Vec(0,0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        
        this.sides.forEach((l) -> {
            Line2D line = new Line2D.Float(Math.round(l.getP1().x), Math.round(l.getP1().y), Math.round(l.getP2().x), Math.round(l.getP2().y));
            g2.draw(line); 
        });
        
        this.sensors.forEach((vec) -> {
            g2.drawRoundRect(Math.round((float)vec.x), Math.round((float)vec.y), this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2, this.sensorRadius*2);
        });
        
        g2.drawRoundRect(Math.round((float)this.robotPosition.x), Math.round((float)this.robotPosition.y), this.robotRadius*2, this.robotRadius*2, this.robotRadius*2, this.robotRadius*2);
    }
    
    public void setSides(ArrayList<Line> sides){
        this.sides = new ArrayList<Line>();
        sides.forEach((l) -> {
            this.sides.add(new Line(transformVec(l.getP1()), transformVec(l.getP2())));
        });
    }
    
    public void placeSensor(Vec position){
        this.sensors.add(transformVec(position).add(this.sensorOffset));
        this.paint(this.getGraphics());
        try {
            Thread.sleep(this.sensorDrawingDelayMillisec);
        } catch (InterruptedException ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setRobotPosition(Vec position){
        this.robotPosition = transformVec(position).add(this.robotOffset);
        this.paint(this.getGraphics());
        try {
            Thread.sleep(this.robotDrawingDelayMillisec);
        } catch (InterruptedException ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void moveRobotToPosition(Vec position){
        Vec endPosition = transformVec(position).add(this.robotOffset);
        this.robotAnimation(this.robotPosition, endPosition, 1000);
        this.robotPosition = endPosition;
        this.paint(this.getGraphics());
        try {
            Thread.sleep(this.robotDrawingDelayMillisec);
        } catch (InterruptedException ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Vec transformVec(Vec vec){
        return vec.mul(this.scale).add(this.offset);
    }
    
    private void robotAnimation(Vec start, Vec end, int time){
        Vec delta = end.sub(start).mul((double)this.animationDelayMillisec / (double)time);
        for(int i = 0; i < Math.floor(time / this.animationDelayMillisec); i++){
            this.robotPosition = this.robotPosition.add(delta);
            this.paint(this.getGraphics());
            try {
                Thread.sleep(this.animationDelayMillisec);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}