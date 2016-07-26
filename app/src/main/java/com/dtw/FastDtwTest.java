package com.dtw;

/*
 * FastDtwTest.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */



import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;
import com.dtw.TimeWarpInfo;


/**
 * This class contains a main method that executes the FastDTW algorithm on two
 * time series with a specified radius.
 *
 * @author Stan Salvador, stansalvador@hotmail.com
 * @since Jul 14, 2004
 */
public class FastDtwTest
{
   /**
    * This main method executes the FastDTW algorithm on two time series with a
    * specified radius. The time series arguments are file names for files that
    * contain one measurement per line (time measurements are an optional value
    * in the first column). After calculating the warp path, the warp
    * path distance will be printed to standard output, followed by the path
    * in the format "(0,0),(1,0),(2,1)..." were each pair of numbers in
    * parenthesis are indexes of the first and second time series that are
    * linked in the warp path
    *
     /* public static void main(String[]  args)
      {
    	 String inputFile1="C:\\Users\\t-shdas\\msr-hackathon\\FastDTWTest\\trace0.csv";
    	 String inputFile2="C:\\Users\\t-shdas\\msr-hackathon\\FastDTWTest\\trace1.csv";
    			 
*//*         if (args.length!=3 && args.length!=4)
         {
            System.out.println("USAGE:  java FastDtwTest timeSeries1 timeSeries2 radius [EuclideanDistance|ManhattanDistance|BinaryDistance]");
            System.exit(1);
         }
         else
         {*//*
            final TimeSeries tsI = new TimeSeries(inputFile1, false, false, ',');
            final TimeSeries tsJ = new TimeSeries(inputFile2, false, false, ',');
            int depth=10;
            final DistanceFunction distFn;
            if (args.length < 4)
            {
               distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance"); 
            }
            else
            {
               distFn = DistanceFunctionFactory.getDistFnByName(args[3]);
            }   // end if
            
            final TimeWarpInfo info = FastDTW.getWarpInfoBetween(tsI, tsJ, depth, distFn);

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
        // }  // end if

      }  // end main()*/

   public static void DTW(String file1, String file2, boolean Euclidean)
   {
       final TimeSeries tsI = new TimeSeries(file1, false, false, ',');
       final TimeSeries tsJ = new TimeSeries(file2, false, false, ',');
       int depth=10;
       final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
       final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, depth, distFn);

       System.out.println("Warp Distance: " + info.getDistance());
       System.out.println("Warp Path:     " + info.getPath());

   }


}  // end class FastDtwTest
