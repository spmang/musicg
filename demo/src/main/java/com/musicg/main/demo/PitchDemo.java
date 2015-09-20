package com.musicg.main.demo;

import com.musicg.processor.TopManyPointsProcessorChain;
import com.musicg.spectrogram.Spectrogram;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;

public class PitchDemo {

    public static void main(String[] args) {

        String filename = "audio_work/cock_a_1.wav";

        // create a wave object
        try {
            AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream(filename);
            Spectrogram spectrogram = new Spectrogram(wave);

            TopManyPointsProcessorChain processorChain = new TopManyPointsProcessorChain(spectrogram.getNormalizedSpectrogram(), 1);
            List<double[]> processedIntensities = processorChain.getIntensities();

            for (int i = 0; i < processedIntensities.size(); i++) {
                double[] frame = processedIntensities.get(i);
                for (int j = 0; j < frame.length; j++) {
                    if (frame[j] > 0) {
                        System.out.println(i + ": " + frame[j]);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (UnsupportedAudioFileException uafe) {
            uafe.printStackTrace();
        }
    }
}