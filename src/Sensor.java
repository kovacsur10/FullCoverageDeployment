
public class Sensor {

    public static enum State {
        REGULAR, BOUNDARY, ENTRANCE;
    }

    public Sensor(Vec coord, int seqNum, State state, Sensor backPtr) {
        this.coord = coord;
        this.seqNum = seqNum;
        this.state = state;
        this.backPtr = backPtr;
    }
    
    public Vec coord;
    public int seqNum;
    public State state;
    public Sensor backPtr;
}
