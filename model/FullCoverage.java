package model;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;

public class FullCoverage {
    private String filePath;
    private ROI roi;
    private Robot robot;
    private boolean isLoadedMap;
    
    public FullCoverage() { 
        isLoadedMap = false;
    }
    
    public Robot getRobot() {
        return this.robot;
    }
    
    public ROI getROI() {
        return this.roi;
    }
    
    public boolean isLoadedMap() {
        return isLoadedMap;
    }
    
    public void loadMap(String pathToMap) throws InputMismatchException, FileNotFoundException {
        this.isLoadedMap = false;
        this.filePath = pathToMap;
        this.roi = new ROI(this.filePath);
        this.robot = new Robot(this.roi, new Vec(0,0));
        this.isLoadedMap = true;
    }
    
}