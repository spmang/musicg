package com.musicg.streams.filter;

import com.musicg.dsp.FastFourierTransform;
import com.musicg.streams.filter.PipedAudioFilter;

import java.io.EOFException;
import java.io.IOException;

/**
 * Stream that processes an entire FFT frame and writes it to an output stream.
 * Created by Scott on 9/19/2015.
 */
public class FftFilter extends PipedAudioFilter {

    private FastFourierTransform fft;

    private int fftSampleSize;

    private int numFrequencyUnit;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public FftFilter(PipedAudioFilter input, int fftSampleSize) {
        super(input);
        this.fftSampleSize = fftSampleSize;
        numFrequencyUnit = fftSampleSize / 4;
        fft = new FastFourierTransform();
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public FftFilter(PipedAudioFilter input, boolean useLittleEndian, int fftSampleSize) {
        super(input, useLittleEndian);
        this.fftSampleSize = fftSampleSize;
        numFrequencyUnit = fftSampleSize / 4;
    }

    /**
     * Read an entire frame of data.
     *
     * @return
     * @throws IOException
     */
    public double[] readFrame() throws IOException {
        double[] signals = new double[fftSampleSize];
        int n = 0;
        try {
            for (n = 0; n < fftSampleSize; n++) {
                signals[n] = inputStream.readDouble();
            }
        } catch (EOFException eofe) {
            if (n == 0) throw eofe;
        }
        // for each frame in signals, do fft on it
        double[] magnitudes = fft.getMagnitudes(signals);
        return magnitudes;
    }

    /**
     * Read the required data from the underlying input stream, process it and write it to the output stream.
     *
     * @throws IOException
     */
    @Override
    public void pipeValue() throws IOException {
        for (double value : readFrame()) {
            outputStream.writeDouble(value);
        }
    }

    public int getNumFrequencyUnit() {
        return numFrequencyUnit;
    }

    public double getMaxAmp() {
        return fft.getMaxAmp();
    }

    public double getMinAmp() {
        return fft.getMinAmp();
    }
}
