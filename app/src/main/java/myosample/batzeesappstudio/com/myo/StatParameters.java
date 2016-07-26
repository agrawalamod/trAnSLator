package myosample.batzeesappstudio.com.myo;

/**
 * Created by agrawalamod on 7/27/16.
 */
public class StatParameters {
    private double mean;
    private double stdDev;
    private double peakDiff;
    private double diffTimePeak;
    public double getMean() {
        return mean;
    }
    public void setMean(double mean) {
        this.mean = mean;
    }
    public double getStdDev() {
        return stdDev;
    }
    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }
    public double getPeakDiff() {
        return peakDiff;
    }
    public void setPeakDiff(double peakDiff) {
        this.peakDiff = peakDiff;
    }
    public double getDiffTimePeak() {
        return diffTimePeak;
    }
    public void setDiffTimePeak(double diffTimePeak) {
        this.diffTimePeak = diffTimePeak;
    }

}
