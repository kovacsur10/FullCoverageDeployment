package model;

public class Sensor {

    public static enum State {
        REGULAR, BOUNDARY, ENTRANCE;
    }

    private Vec coord;
    private int seqNum;
    private State state;
    private Sensor backPtr;

    public Sensor(Vec coord, int seqNum, State state, Sensor backPtr) {
        this.coord = coord;
        this.seqNum = seqNum;
        this.state = state;
        this.backPtr = backPtr;
    }

    public int getSequenceNumber() {
        return seqNum;
    }

    public State getState() {
        return state;
    }

    public Vec getCoordinates() {
        return coord;
    }

    public Sensor getPreviousSensor() {
        return backPtr;
    }
}
