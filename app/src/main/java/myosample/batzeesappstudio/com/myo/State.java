package myosample.batzeesappstudio.com.myo;

/**
 * Created by agrawalamod on 7/23/16.
 */
public class State {

    private static State currState = new State();

    String pose;
    float roll;
    float pitch;
    float yaw;
    float x;
    float y;
    float z;
    float w;



    private State()
    {
        pose = "No action";
        roll =0;
        pitch = 0;
        yaw = 0;
        x =0;
        y =0;
        z =0;
        w =0;


    }

    public static State getInstance()
    {
        return currState;
    }
}
