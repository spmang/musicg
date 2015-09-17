package com.musicg.processor;

import java.util.LinkedList;
import java.util.List;

public class ProcessorChain {

    List<IntensityProcessor> processorList = new LinkedList<>();
    private double[][] intensities;

    public ProcessorChain(double[][] intensities) {
        this.intensities = intensities;
        RobustIntensityProcessor robustProcessor = new RobustIntensityProcessor(intensities, 1);
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