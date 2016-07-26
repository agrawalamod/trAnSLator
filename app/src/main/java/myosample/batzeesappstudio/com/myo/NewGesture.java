package myosample.batzeesappstudio.com.myo;

import android.util.Log;

import com.dtw.FastDtwTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by agrawalamod on 7/27/16.
 */
public class NewGesture {


    public int detectGesture(File testFile)
    {
        int[][] winMatrix={
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };
        double[] weightMatrix={0.12,0.16,0.42,0.3};
        double[] dtwScore={0,0,0,0};
        double[] mean={0,0,0,0};
        double[] stddev={0,0,0,0};
        double[] peak={0,0,0,0};

        double[][] scoreMatrix={
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };

        // TODO Auto-generated method stub
/*		String trainFile="C:\\data\\amod\\train3.csv";
		String trainPath="C:\\data\\amod\\Test";
		File trainFileObj=new File(trainFile);
		System.out.println("##### OPERATION STARTED ###");
		CSVNormalizer csvNormalizer=new CSVNormalizer();
		try {
			csvNormalizer.readCSV(trainFileObj);
			csvNormalizer.normalize();
			csvNormalizer.writeData(trainPath, "NormalizedTrain3.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("#### OPERATION COMPLETED SUCCESSFULLY ####");*/

        File root = android.os.Environment.getExternalStorageDirectory();
        File[] trainedFiles = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Trained").listFiles();
        /*for(File f: trainedFiles)
        {
            Log.v("Testing", ""+f.getAbsolutePath());
            //FastDtwTest.DTW(f.getAbsolutePath(),dir.getAbsolutePath()+ File.separator+filename,true);
        }*/


        String trainFile1="C:\\data\\Trained\\train_hungry1.csv";
        String trainFile2="C:\\data\\Trained\\train_working1.csv";
        String trainFile3="C:\\data\\Trained\\train_hi1.csv";
        String trainFile4="C:\\data\\Trained\\train_good1.csv";
        String testFile1="C:\\data\\Trained\\train_good2.csv";



        CSVNormalizer train1=new CSVNormalizer();
        CSVNormalizer train2=new CSVNormalizer();
        CSVNormalizer train3=new CSVNormalizer();
        CSVNormalizer train4=new CSVNormalizer();
        CSVNormalizer test1=new CSVNormalizer();
        try {
            train1.readCSV(trainedFiles[0]);
            train2.readCSV(trainedFiles[1]);
            train3.readCSV(trainedFiles[2]);
            train4.readCSV(trainedFiles[3]);
            test1.readCSV(testFile);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //Extract param before normalizing
        ArrayList<StatParameters> train1Param=train1.getFeatures();
        ArrayList<StatParameters> train2Param=train2.getFeatures();
        ArrayList<StatParameters> train3Param=train3.getFeatures();
        ArrayList<StatParameters> train4Param=train4.getFeatures();
        ArrayList<StatParameters> test1Param=test1.getFeatures();
        //train1.normalize();
        //train2.normalize();
        //train3.normalize();
        //test1.normalize();
        dtwScore[0]=runDTW(train1, test1);
        dtwScore[1]=runDTW(train2, test1);
        dtwScore[2]=runDTW(train3, test1);
        dtwScore[3]=runDTW(train4, test1);
        int dtwIndex = getWinIndex(dtwScore);
        winMatrix[dtwIndex][0]=1;


        //System.out.println("Gesture 1");

        StatParameters param1= displayFeatureDiff(train1Param, test1Param);
        StatParameters param2= displayFeatureDiff(train2Param, test1Param);
        StatParameters param3= displayFeatureDiff(train3Param, test1Param);
        StatParameters param4= displayFeatureDiff(train4Param, test1Param);
        mean[0]=param1.getMean();
        mean[1]=param2.getMean();
        mean[2]=param3.getMean();
        mean[3]=param4.getMean();
        int meanIndex = getWinIndex(mean);
        winMatrix[meanIndex][1]=1;

        stddev[0]=param1.getStdDev();
        stddev[1]=param2.getStdDev();
        stddev[2]=param3.getStdDev();
        stddev[3]=param4.getStdDev();
        int stddevIndex = getWinIndex(stddev);
        winMatrix[stddevIndex][2]=1;

        peak[0]=param1.getPeakDiff();
        peak[1]=param2.getPeakDiff();
        peak[2]=param3.getPeakDiff();
        peak[3]=param4.getPeakDiff();
        int peakIndex = getWinIndex(peak);
        winMatrix[peakIndex][3]=1;

        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                scoreMatrix[i][j]=winMatrix[i][j]*weightMatrix[j];
                System.out.print(" "+scoreMatrix[i][j]+" ");
            }
            System.out.println();
        }
        double sumMatrix[]={0,0,0,0};
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                sumMatrix[i]+=scoreMatrix[i][j];
            }
            System.out.println(" "+sumMatrix[i]);
        }

        int gestureId=getLoseIndex(sumMatrix);
        System.out.println("The Gesture is : "+gestureId);

        return gestureId;
		/*System.out.println("Gesture 2");
		runDTW(train2, test1);
		displayFeatureDiff(train2Param, test1Param);
		System.out.println("Gesture 3");
		runDTW(train3, test1);
		displayFeatureDiff(train3Param, test1Param);
		System.out.println("Gesture 4");
		runDTW(train4, test1);
		displayFeatureDiff(train4Param, test1Param);*/

    }
    public static StatParameters displayFeatureDiff(ArrayList<StatParameters> train, ArrayList<StatParameters> test){
        StatParameters result=new StatParameters();
        double summean=0;
        double sumstd=0;
        double sumpeak=0;
        for(int i=0;i<7;i++){
            double meanDiff=Math.abs( train.get(i).getMean()-test.get(i).getMean());
            double stdDev=Math.abs(train.get(i).getStdDev()-test.get(i).getStdDev());
            double diffPeak=Math.abs(train.get(i).getPeakDiff()-test.get(i).getPeakDiff());
            summean+=meanDiff;
            sumstd+=stdDev;
            sumpeak+=diffPeak;
        }
        result.setMean(summean);
        result.setStdDev(sumstd);
        result.setPeakDiff(sumpeak);
        return result;
        //System.out.println(":  mean= "+summean+" , stddev= "+sumstd+" , peak= "+sumpeak);
    }
    public static double runDTW(CSVNormalizer csv1,CSVNormalizer csv2){
        double[] x1= convertDoubles(csv1.x);
        double[] y1=convertDoubles(csv1.y);
        double[] z1=convertDoubles(csv1.z);
        double[] w1=convertDoubles(csv1.w);
        double[] roll1=convertDoubles(csv1.roll);
        double[] pitch1=convertDoubles(csv1.pitch);
        double[] yaw1=convertDoubles(csv1.yaw);


        double[] x2=convertDoubles(csv2.x);
        double[] y2=convertDoubles(csv2.y);
        double[] z2=convertDoubles(csv2.z);
        double[] w2=convertDoubles(csv2.w);
        double[] roll2=convertDoubles(csv2.roll);
        double[] pitch2=convertDoubles(csv2.pitch);
        double[] yaw2=convertDoubles(csv2.yaw);


        double sum=DTWDistance(x1,x2)+DTWDistance(y1, y2)+DTWDistance(z1, z2)+DTWDistance(w1, w2)+DTWDistance(roll1, roll2)+DTWDistance(pitch1, pitch2)+DTWDistance(yaw1, yaw2);
        //System.out.println(DTWDistance(x1,x2)*1000);
        //System.out.println(DTWDistance(y1, y2)*1000);
        //System.out.println(DTWDistance(z1, z2)*1000);
        //System.out.println(DTWDistance(w1, w2)*1000);
        System.out.println("DTW:  "+sum*1000);
        return sum;
    }
    public static double DTWDistance(double[] first, double[] second){
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
        return costMatrix[n-1][m-1];
    }
    public static double evaluateCost(double first, double second){
        return Math.abs(first-second)*Math.abs(first-second);
    }
    public static double[] convertDoubles(List<Double> doubles)
    {
        double[] ret = new double[doubles.size()];
        Iterator<Double> iterator = doubles.iterator();
        int i = 0;
        while(iterator.hasNext())
        {
            ret[i] = iterator.next();
            i++;
        }
        return ret;
    }
    public static int getWinIndex(double[] matrix){
        if(matrix[0]<=matrix[1] && matrix[0]<=matrix[2] && matrix[0]<=matrix[3]){
            return 0;
        }else if(matrix[1]<=matrix[0] && matrix[1]<=matrix[2] && matrix[1]<=matrix[3]){
            return 1;
        }else if(matrix[2]<=matrix[0] && matrix[2]<=matrix[1] && matrix[2]<=matrix[3]){
            return 2;
        }else{
            return 3;
        }
    }
    public static int getLoseIndex(double[] matrix){
        if(matrix[0]>=matrix[1] && matrix[0]>=matrix[2] && matrix[0]>=matrix[3]){
            return 0;
        }else if(matrix[1]>=matrix[0] && matrix[1]>=matrix[2] && matrix[1]>=matrix[3]){
            return 1;
        }else if(matrix[2]>=matrix[0] && matrix[2]>=matrix[1] && matrix[2]>=matrix[3]){
            return 2;
        }else{
            return 3;
        }
    }
}
