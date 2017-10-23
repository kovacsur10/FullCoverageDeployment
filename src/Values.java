package src;

public final class Values {

    public static final String filterShowSensorRadiusText = "Show sensor sensing radius";
    public static final String filterShowRobotRadiusText = "Show robot sensing radius";

    public static final String autoRunActionKey = "Run";
    public static final String stepActionName = "Next step";
    public static final String stopRunActionKey = "Stop";
    public static final String animationStoppedKey = "AnimStopped";

    public static final int robotMovementAnimationTime = 1000;
    public static final int robotDrawingDelayMillisec = 1000;
    public static final int sensorDrawingDelayMillisec = 500;

    public static final double sensorSensing = Math.sqrt(2);
    public static final double robotSensing = 3.0;

    public static final int autoPlayingWaitingTime = 800;

    private Values(){
    }
}
