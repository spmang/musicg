package com.musicg.processor;

import java.util.List;

public interface IntensityProcessor {

    void execute();

    List<double[]> getIntensities();
}