package com.musicg.processor;

import com.musicg.math.rank.ArrayRankDouble;

import java.util.ArrayList;
import java.util.List;

public class RobustIntensityProcessor implements IntensityProcessor {

    private List<double[]> intensities;
    private int numPointsPerFrame;

    public RobustIntensityProcessor(List<double[]> intensities, int numPointsPerFrame) {
        this.intensities = intensities;
        this.numPointsPerFrame = numPointsPerFrame;
    }

    public void execute() {

        int numX = intensities.size();
        int numY = intensities.get(0).length;
        List<double[]> processedIntensities = new ArrayList<double[]>(numY);

        for (int i = 0; i < numX; i++) {
            double[] tmpArray = new double[numY];
            double[] frame = intensities.get(i);
            System.arraycopy(frame, 0, tmpArray, 0, numY);

            // pass value is the last some elements in sorted array
            ArrayRankDouble arrayRankDouble = new ArrayRankDouble();
            double passValue = arrayRankDouble.getNthOrderedValue(tmpArray, numPointsPerFrame, false);

            // only passed elements will be assigned a value
            for (int j = 0; j < numY; j++) {
                if (frame[j] >= passValue) {
                    frame[j] = frame[j];
                }
            }
        }
        intensities = processedIntensities;
    }

    public List<double[]> getIntensities() {
        return intensities;
    }
}