package com.musicg.spectrogram;

import com.musicg.wave.Wave;

import java.io.IOException;

/**
 * Created by scottmangan on 9/16/15.
 */
public class SpectrogramFactory {

    /**
     * Get the wave spectrogram
     *
     * @param fftSampleSize number of sample in fft, the value needed to be a number to power of 2
     * @param overlapFactor 1/overlapFactor overlapping, e.g. 1/4=25% overlapping, 0 for no overlapping
     * @return spectrogram
     */
    public Spectrogram getSpectrogram(final Wave wave, int fftSampleSize, int overlapFactor) throws IOException {
        return new Spectrogram(wave, fftSampleSize, overlapFactor);
    }

}
