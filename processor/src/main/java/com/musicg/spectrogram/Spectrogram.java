/*
 * Copyright (C) 2011 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.musicg.spectrogram;

import com.musicg.dsp.FastFourierTransform;
import com.musicg.dsp.WindowFunction;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.HammingInputStream;
import com.musicg.streams.OverlapAmplitudeInputStream;
import com.musicg.streams.Pipe;

import java.io.IOException;

/**
 * Handles the wave data in frequency-time domain.
 *
 * @author Jacquet Wong
 * @author Scott Mangan
 */
public class Spectrogram {

    public static final int SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE = 1024;
    public static final int SPECTROGRAM_DEFAULT_OVERLAP_FACTOR = 0;    // 0 for no overlapping

    private AudioFormatInputStream wave;
    private double[][] spectrogram;    // relative spectrogram
    private double[][] absoluteSpectrogram;    // absolute spectrogram
    private int fftSampleSize;    // number of sample in fft, the value needed to be a number to power of 2
    private int overlapFactor;    // 1/overlapFactor overlapping, e.g. 1/4=25% overlapping
    private int numFrames;    // number of frames of the spectrogram
    private int framesPerSecond;    // frame per second of the spectrogram
    private int numFrequencyUnit;    // number of y-axis unit
    private double unitFrequency;    // frequency per y-axis unit

    /**
     * Constructor
     *
     * @param wave The source Wave to process.
     */
    public Spectrogram(AudioFormatInputStream wave) {
        this.wave = wave;
        // default
        this.fftSampleSize = SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE;
        this.overlapFactor = SPECTROGRAM_DEFAULT_OVERLAP_FACTOR;
    }

    /**
     * Constructor
     *
     * @param wave          The source Wave to process.
     * @param fftSampleSize number of sample in fft, the value needed to be a number to power of 2
     * @param overlapFactor 1/overlapFactor overlapping, e.g. 1/4=25% overlapping, 0 for no overlapping
     */
    public Spectrogram(AudioFormatInputStream wave, int fftSampleSize, int overlapFactor) {
        this.wave = wave;

        if (Integer.bitCount(fftSampleSize) == 1) {
            this.fftSampleSize = fftSampleSize;
        } else {
            System.err.print("The input number must be a power of 2");
            this.fftSampleSize = SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE;
        }

        this.overlapFactor = overlapFactor;
    }

    /**
     * Build spectrogram
     *
     * @param maxSamples The maximum number of samples to process.
     */
    public void buildSpectrogram(int maxSamples) throws IOException {
        OverlapAmplitudeInputStream overlapAmp = new OverlapAmplitudeInputStream(wave.getAudioInputStream(), overlapFactor, fftSampleSize);
        Pipe pipe = new Pipe(overlapAmp.getAudioFormat());
        overlapAmp.connect(pipe.getOutput());
        HammingInputStream hamming = new HammingInputStream(pipe.getInput(), pipe.getAudioFormat(), true, fftSampleSize);

        // use FFT to generate spectrogram from modified data.
        // This can be multi-threaded.
        absoluteSpectrogram = new double[numFrames][];

        // for each frame in signals, do fft on it
        FastFourierTransform fft = new FastFourierTransform();

        double[][] signals=new double[numFrames][];
        for (int i = 0; i < numFrames; i++) {
            absoluteSpectrogram[i] = fft.getMagnitudes(signals[i]);
        }

        if (absoluteSpectrogram.length > 0) {

            numFrequencyUnit = absoluteSpectrogram[0].length;
            unitFrequency = (double) wave.getAudioFormat().getSampleRate() / 2 / numFrequencyUnit;    // frequency could be caught within the half of nSamples according to Nyquist theory

            // normalization of absoultSpectrogram
            spectrogram = new double[numFrames][numFrequencyUnit];

            // 4) Find max and min amplitudes
            double maxAmp = Double.MIN_VALUE;
            double minAmp = Double.MAX_VALUE;
            for (int i = 0; i < numFrames; i++) {
                for (int j = 0; j < numFrequencyUnit; j++) {
                    if (absoluteSpectrogram[i][j] > maxAmp) {
                        maxAmp = absoluteSpectrogram[i][j];
                    } else if (absoluteSpectrogram[i][j] < minAmp) {
                        minAmp = absoluteSpectrogram[i][j];
                    }
                }
            }
            // end set max and min amplitudes

            // normalization
            // avoiding divided by zero
            double minValidAmp = 0.00000000001F;
            if (minAmp == 0) {
                minAmp = minValidAmp;
            }

            double diff = Math.log10(maxAmp / minAmp);    // perceptual difference
            for (int i = 0; i < numFrames; i++) {
                for (int j = 0; j < numFrequencyUnit; j++) {
                    if (absoluteSpectrogram[i][j] < minValidAmp) {
                        spectrogram[i][j] = 0;
                    } else {
                        spectrogram[i][j] = (Math.log10(absoluteSpectrogram[i][j] / minAmp)) / diff;
                    }
                }
            }
            // end normalization
        }
    }

    /**
     * Get spectrogram: spectrogram[time][frequency]=intensity
     *
     * @return logarithm normalized spectrogram
     */
    public double[][] getNormalizedSpectrogramData() {
        return spectrogram;
    }

    /**
     * Get spectrogram: spectrogram[time][frequency]=intensity
     *
     * @return absolute spectrogram
     */
    public double[][] getAbsoluteSpectrogramData() {
        return absoluteSpectrogram;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    public int getNumFrequencyUnit() {
        return numFrequencyUnit;
    }

    public double getUnitFrequency() {
        return unitFrequency;
    }

    public int getFftSampleSize() {
        return fftSampleSize;
    }

    public int getOverlapFactor() {
        return overlapFactor;
    }
}