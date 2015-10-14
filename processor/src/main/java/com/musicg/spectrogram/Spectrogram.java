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

import com.musicg.streams.filter.FftInputStream;
import com.musicg.streams.filter.HanningInputStream;
import com.musicg.streams.filter.OverlapAmplitudeFilter;
import com.musicg.streams.filter.PipedAudioFilter;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the wave data in frequency-time domain.
 *
 * @author Jacquet Wong
 * @author Scott Mangan
 */
public class Spectrogram {

    public static final int SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE = 1024;
    public static final int SPECTROGRAM_DEFAULT_OVERLAP_FACTOR = 0;    // 0 for no overlapping

    private PipedAudioFilter wave;
    private List<double[]> normalizedSpectrogram;

    private int fftSampleSize;    // number of sample in fft, the value needed to be a number to power of 2
    private int overlapFactor;    // 1/overlapFactor overlapping, e.g. 1/4=25% overlapping

    /**
     * Constructor
     *
     * @param wave The source Wave to process.
     */
    public Spectrogram(PipedAudioFilter wave) {
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
    public Spectrogram(PipedAudioFilter wave, int fftSampleSize, int overlapFactor) {
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
     * Build spectrogram.
     */
    public FftInputStream getSpectrogramInputStream() throws IOException {
        OverlapAmplitudeFilter overlapAmp = new OverlapAmplitudeFilter(wave, overlapFactor, fftSampleSize);
        HanningInputStream hamming = new HanningInputStream(overlapAmp, true, fftSampleSize);
        return new FftInputStream(hamming, fftSampleSize);
    }

    /**
     * Create a normalized spectrogram. This requires us to load all the data into memory.
     *
     * @return
     * @throws IOException
     */
    public List<double[]> getNormalizedSpectrogram() throws IOException {
        if (normalizedSpectrogram == null) {
            FftInputStream fftInputStream = getSpectrogramInputStream();
            int numFrequencyUnit = fftInputStream.getNumFrequencyUnit();
            // frequency could be caught within the half of nSamples according to Nyquist theory

            // requires normalized data.
            normalizedSpectrogram = new ArrayList<double[]>();

            // read entire spectrogram
            List<double[]> spectrogram = new ArrayList<>();
            try {
                do {
                    spectrogram.add(fftInputStream.readFrame());
                } while (true);
            } catch (EOFException eofe) {
                // end of stream, read complete.
            }

            // The next part requires the entire spectrogram to be processed.
            double minAmp = fftInputStream.getMinAmp();
            double maxAmp = fftInputStream.getMaxAmp();

            // normalization
            // avoiding divided by zero
            double minValidAmp = 0.00000000001F;
            if (minAmp == 0) {
                minAmp = minValidAmp;
            }

            // A frame in the spectrogram.

            double diff = Math.log10(maxAmp / minAmp);    // perceptual difference

            for (double[] fftFrame : spectrogram) {
                double[] frame = new double[numFrequencyUnit];
                for (int j = 0; j < numFrequencyUnit; j++) {
                    if (fftFrame[j] < minValidAmp) {
                        frame[j] = 0;
                    } else {
                        frame[j] = (Math.log10(fftFrame[j] / minAmp)) / diff;
                    }
                }
                normalizedSpectrogram.add(frame);
            }
            // end normalization
        }
        return normalizedSpectrogram;
    }

    public int getFftSampleSize() {
        return fftSampleSize;
    }

    public int getOverlapFactor() {
        return overlapFactor;
    }
}