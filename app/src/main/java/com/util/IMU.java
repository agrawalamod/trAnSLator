package com.util;

/**
 * Created by agrawalamod on 7/27/16.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class IMU {
    public static double epsilon=0.005;
    public static boolean isIntertialMotion(List<Double> data){
        double sum = 0;
        if(!data.isEmpty()) {
            int counter=0;
            double mean=0;
            for (Double d : data) {
                mean=mean*counter+d;
                counter++;
                mean/=counter;
            }
            sum=0;
            for(Double d : data){
                double dataVal=d.doubleValue();
                sum+=(dataVal -mean)*(dataVal-mean);
            }
            double stdDev=Math.sqrt(sum/data.size());
           // Log.v("isInertial", "mean= "+mean);
           // Log.v("isInertial", "stddev= "+stdDev);
            if(stdDev>=epsilon){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
}
