package view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import controller.Controller;
import model.*;
import org.json.*;

public class View implements ActionListener, ComponentListener{
    private Controller controller;
    private FullCoverage model;
    private JFrame frame;
    private final JFileChooser mapOpenFileChooser = new JFileChooser();
    private Window window;
    private int windowWidth = 450;
    private int windowHeight = 450;
    private int marginWidth = 40;
    private int marginHeight = 20;
    private int deltaWidth;
    private int deltaHeight;

    public View(){
        try {
            readDefaults();
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(null, "Cannot load the defaults.ini file! Exiting...");
            e.printStackTrace();
            return;
        } catch(JSONException e) {
            JOptionPane.showMessageDialog(null, "The format of your defaults.ini is not supported! Exiting...");
            e.printStackTrace();
            return;
        }
        
        this.model = new FullCoverage();
        
        //create and set up view
        if(this.frame == null){
            this.frame = new JFrame("Full Coverage Project");
            this.frame.setSize(this.windowWidth, this.windowHeight);
            this.frame.setMinimumSize(new Dimension(300, 200));
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.frame.addComponentListener(this);
        }
        this.deltaWidth = this.frame.getWidth() - this.windowWidth;
        this.deltaHeight = this.frame.getHeight() - this.windowHeight;
        this.window = new Window(new Vec(this.windowWidth, this.windowHeight), new Vec(this.marginWidth, this.marginHeight));
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
    
    private void readDefaults() throws IOException, JSONException, URISyntaxException {
        JSONObject obj = new JSONObject(readFile("/defaults.ini", StandardCharsets.UTF_8));
        this.windowWidth = obj.getInt("width");
        this.windowHeight = obj.getInt("height");
        this.marginWidth = obj.getInt("margin_width");
        this.marginHeight = obj.getInt("margin_height");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.window.openMapMenuItem){
            int returnVal = this.mapOpenFileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.mapOpenFileChooser.getSelectedFile();
                try {
                    this.model.loadMap(file.getPath());
                    this.window.onLoadMap(this.model.getROI().getSides(), this.model.getROI().getMin(), this.model.getROI().getMax());
                } catch (InputMismatchException | FileNotFoundException e1) {
                    JOptionPane.showMessageDialog(null, "Cannot load the map file!");
                }
            }
        }
    }
    
    String readFile(String path, Charset encoding) throws IOException, URISyntaxException {
        byte[] encoded = Files.readAllBytes(Paths.get(getClass().getResource(path).toURI()));
        return new String(encoded, encoding);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.window.onResize(this.frame.getWidth() - this.deltaWidth, this.frame.getHeight() - this.deltaHeight);
    }

    @Override
    public void componentShown(ComponentEvent e) {    
    }
}
