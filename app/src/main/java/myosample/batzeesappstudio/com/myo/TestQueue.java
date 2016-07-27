package myosample.batzeesappstudio.com.myo;

import android.util.Log;

import com.util.IMU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agrawalamod on 7/27/16.
 */
public class TestQueue {

    int final_size = 500;
    ArrayList<Double>x;
    ArrayList<Double>y;
    ArrayList<Double>z;
    ArrayList<Double>w;
    ArrayList<Double>yaw;
    ArrayList<Double>pitch;
    ArrayList<Double>roll;
    ArrayList<String>pose;

    int firstIndex,lastIndex;
    boolean isInertial = false;

    public TestQueue()
    {
        x = new ArrayList<Double>();
        y = new ArrayList<Double>();
        z = new ArrayList<Double>();
        w = new ArrayList<Double>();
        yaw = new ArrayList<Double>();
        pitch = new ArrayList<Double>();
        roll = new ArrayList<Double>();
        pose = new ArrayList<String>();

    }


    public void clearQueue()
    {
        if(x.size()>final_size)
        {
            int popIndices = x.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                x.remove(i);
            }


        }
        if(y.size()>final_size)
        {
            int popIndices = y.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                y.remove(i);
            }

        }
        if(z.size()>final_size)
        {
            int popIndices = z.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                z.remove(i);
            }

        }
        if(w.size()>final_size)
        {
            int popIndices = w.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                w.remove(i);
            }

        }
        if(yaw.size()>final_size)
        {
            int popIndices = yaw.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                yaw.remove(i);
            }

        }
        if(pitch.size()>final_size)
        {
            int popIndices = pitch.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                pitch.remove(i);
            }

        }
        if(roll.size()>final_size)
        {
            int popIndices = roll.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                roll.remove(i);
            }


        }
        if(pose.size()>final_size)
        {
            int popIndices = pose.size()-final_size;
            for(int i=0; i<popIndices; i++)
            {
                pose.remove(i);
            }

        }

    }
    public void addDataQueue(double x, double y, double z, double w, double roll, double pitch, double yaw, String pose)
    {
        //Log.v("AddQueueData", ""+ this.x.size());
        this.x.add(x);
        this.y.add(y);
        this.z.add(z);
        this.w.add(w);
        this.yaw.add(yaw);
        this.pitch.add(pitch);
        this.roll.add(roll);
        this.pose.add(pose);

        if(this.x.size()%30 == 0)
        {
            //Log.v("AddQueueData", "***********************************");
            lastIndex = this.x.size() -1;
            firstIndex = this.x.size() - 30;
            isInertial = isInertialMotion();
           // Log.v("isInertial", Boolean.toString(isInertial));
            clearQueue();

        }

    }
    public boolean isInertialMotion()
    {
        boolean a = IMU.isIntertialMotion(x.subList(firstIndex,lastIndex));
        boolean b = IMU.isIntertialMotion(y.subList(firstIndex,lastIndex));
        boolean c = IMU.isIntertialMotion(z.subList(firstIndex,lastIndex));
        boolean d = IMU.isIntertialMotion(w.subList(firstIndex,lastIndex));
        //Log.v("isInertial", Boolean.toString(a) + " " + Boolean.toString(b) +" " + Boolean.toString(c) +" " + Boolean.toString(d)  );
        return (a && b && c && d);


    }




}
