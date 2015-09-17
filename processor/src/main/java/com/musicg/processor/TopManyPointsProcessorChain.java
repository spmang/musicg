package com.musicg.processor;

import java.util.LinkedList;
import java.util.List;


public class TopManyPointsProcessorChain {

    List<IntensityProcessor> processorList = new LinkedList<>();
    private double[][] intensities;

    public TopManyPointsProcessorChain(double[][] intensities, int numPoints) {
        this.intensities = intensities;
        RobustIntensityProcessor robustProcessor = new RobustIntensityProcessor(intensities, numPoints);
        processorList.add(robustProcessor);
        process();
    }

    private void process() {
        for (IntensityProcessor processor : processorList) {
            processor.execute();
            intensities = processor.getIntensities();
        }
    }

    public double[][] getIntensities() {
        return intensities;
    }
}