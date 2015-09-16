package com.musicg.main.demo;

import com.musicg.processor.TopManyPointsProcessorChain;
import com.musicg.wave.Wave;
import com.musicg.spectrogram.Spectrogram;

import java.net.URISyntaxException;

public class PitchDemo {

    public static void main(String[] args) {

        String filename = "audio_work/cock_a_1.wav";

        // create a wave object
        try {
            Wave wave = new Wave(filename);
            Spectrogram spectrogram = new Spectrogram(wave);

            TopManyPointsProcessorChain processorChain = new TopManyPointsProcessorChain(spectrogram.getNormalizedSpectrogramData(), 1);
            double[][] processedIntensities = processorChain.getIntensities();

            for (int i = 0; i < processedIntensities.length; i++) {
                for (int j = 0; j < processedIntensities[i].length; j++) {
                    if (processedIntensities[i][j] > 0) {
                        System.out.println(i + ": " + processedIntensities[i][j]);
                    }
                }
            }
        } catch (URISyntaxException urie) {
            System.out.println("The input file could not be found.");
        }
    }
}