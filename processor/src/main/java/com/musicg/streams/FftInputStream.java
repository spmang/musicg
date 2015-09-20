package com.musicg.streams;

import com.musicg.dsp.FastFourierTransform;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stream that processes an entire FFT frame and writes it to an output stream.
 * Created by Scott on 9/19/2015.
 */
public class FftInputStream extends PipedAudioFormatInputStream {

    private FastFourierTransform fft;

    private int fftSampleSize;

    private int numFrequencyUnit;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public FftInputStream(AudioInputStream input, int fftSampleSize) {
        super(input);
        this.fftSampleSize = fftSampleSize;
        numFrequencyUnit = fftSampleSize / 4;
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public FftInputStream(AudioInputStream input, boolean useLittleEndian, int fftSampleSize) {
        super(input, useLittleEndian);
        this.fftSampleSize = fftSampleSize;
        numFrequencyUnit = fftSampleSize / 4;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in     The stream to wrap.
     * @param format
     */
    public FftInputStream(InputStream in, AudioFormat format, int fftSampleSize) {
        super(in, format);
        this.fftSampleSize = fftSampleSize;
        numFrequencyUnit = fftSampleSize / 4;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in              The stream to wrap.
     * @param format
     * @param useLittleEndian
     */
    public FftInputStream(InputStream in, AudioFormat format, boolean useLittleEndian, int fftSampleSize) {
        super(in, format, useLittleEndian);
        this.fftSampleSize = fftSampleSize;
        numFrequencyUnit = fftSampleSize / 4;
    }

    public double[] processFrame() throws IOException {

        double[] signals = new double[fftSampleSize];
        for (int n = 0; n < fftSampleSize; n++) {
            signals[n] = readDouble();
        }
        // for each frame in signals, do fft on it
        double[] magnitudes =  fft.getMagnitudes(signals);
        return magnitudes;
    }

    /**
     * Read the required data from the underlying input stream, process it and write it to the output stream.
     *
     * @throws IOException
     */
    @Override
    public void readValue() throws IOException {
        for (double value : processFrame()) {
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
