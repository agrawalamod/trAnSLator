package myosample.batzeesappstudio.com.myo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by agrawalamod on 7/26/16.
 */
public class CSVNormalizer {


    ArrayList<Double> x;
    ArrayList<Double> y;
    ArrayList<Double> z;
    ArrayList<Double> w;
    ArrayList<Double> yaw;
    ArrayList<Double> pitch;
    ArrayList<Double> roll;
    ArrayList<String> pose;

    public CSVNormalizer()
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

    public void readCSV(File file) throws IOException {

        Log.v("CSVNormalizer", "Normalizing " + file.getAbsolutePath());
        FileInputStream is = new FileInputStream(file.getAbsoluteFile());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                x.add(Double.parseDouble(RowData[0]));
                y.add(Double.parseDouble(RowData[1]));
                z.add(Double.parseDouble(RowData[2]));
                w.add(Double.parseDouble(RowData[3]));
                yaw.add(Double.parseDouble(RowData[4]));
                pitch.add(Double.parseDouble(RowData[5]));
                roll.add(Double.parseDouble(RowData[6]));
                //pose.add(RowData[7]);

            }
        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            is.close();
        }

    }

    public void normalize()
    {
        double xAvg = calculateAverage(x);
        double yAvg = calculateAverage(y);
        double zAvg = calculateAverage(z);
        double wAvg = calculateAverage(w);
        double yawAvg = calculateAverage(yaw);
        double pitchAvg = calculateAverage(pitch);
        double rollAvg = calculateAverage(roll);

        for (int i = 0; i < x.size(); i++) {
            x.set(i, x.get(i) - xAvg);
            y.set(i, y.get(i) - yAvg);
            z.set(i, z.get(i) - zAvg);
            w.set(i, w.get(i) - wAvg);
            yaw.set(i, (yaw.get(i) - yawAvg)/500);
            pitch.set(i, (pitch.get(i) - pitchAvg)/500);
            roll.set(i, (roll.get(i) - rollAvg)/500);
        }


    }

    public void writeData(String path, String filename)
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        String dir = path;
        Log.v("CSVNormalizer", "Writing to " + path + "/"+ filename );

        for(int i=0;i<x.size();i++)
        {
            String data = x.get(i) + "," + y.get(i) + "," + z.get(i) + "," + w.get(i) + "," + yaw.get(i) + ","  + pitch.get(i) + ","+roll.get(i);
            writeToSDFile(data,dir,filename);
        }



    }


    private void writeToSDFile(String d, String dir, String filename){


        File file = new File(dir + File.separator + filename);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(d + "\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detectGesture()
    {




    }

    public double calculateAverage(List<Double> data) {
        double sum = 0;
        if(!data.isEmpty()) {
            for (Double d : data) {
                sum += d;
            }
            return sum / data.size();
        }
        return sum;
    }

    double DTWDistance(Double[] first, Double[] second){
        int n=first.length;
        int m=second.length;
        double[][] costMatrix=new double[n][m];
        costMatrix[0][0]=0;
        for (int i=1;i<n;i++){
            costMatrix[i][0]=Double.POSITIVE_INFINITY;
        }
        for (int j=1;j<m;j++){
            costMatrix[0][j]=Double.POSITIVE_INFINITY;
        }
        for (int i=1;i<n;i++){
            for (int j=1;j<m;j++){
                double cost=evaluateCost(first[i],second[j]);
                costMatrix[i][j]=cost+Math.min(costMatrix[i-1][j],Math.min(costMatrix[i][j],costMatrix[i][j-1]));
            }
        }
        return costMatrix[n][m];
    }
    double evaluateCost(double first, double second){
        return Math.abs(first-second)*Math.abs(first-second);
    }

    public ArrayList<StatParameters> getFeatures(){
        ArrayList<StatParameters> features=new ArrayList<StatParameters>();
        features.add(calculateFeatures(x));
        features.add(calculateFeatures(y));
        features.add(calculateFeatures(z));
        features.add(calculateFeatures(w));
        features.add(calculateFeatures(roll));
        features.add(calculateFeatures(pitch));
        features.add(calculateFeatures(yaw));
        return features;
    }
    public StatParameters calculateFeatures(ArrayList<Double> data){
        StatParameters param=new StatParameters();
        double sum = 0;
        if(!data.isEmpty()) {
            int counter=0;
            double mean=0;
            for (Double d : data) {
                mean=mean*counter+d;
                counter++;
                mean/=counter;
            }
            param.setMean(mean);
            sum=0;
            for(Double d : data){
                double dataVal=d.doubleValue();
                sum+=(dataVal -mean)*(dataVal-mean);
            }
            double stdDev=Math.sqrt(sum/data.size());
            param.setStdDev(stdDev);
            double minVal = Collections.min(data);
            double maxVal = Collections.max(data);
            double diffPeak=maxVal-minVal;
            param.setPeakDiff(diffPeak);

        }
        return param;
    }
}
