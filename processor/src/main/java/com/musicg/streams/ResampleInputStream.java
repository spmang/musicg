package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Resample the audio stream. Currenly only works for short type on wave.
 * <p/>
 * <p/>
 * Created by Scott on 9/18/2015.
 */
public class ResampleInputStream extends PipedAudioFormatInputStream {

    private float sampleRate;
    private long position;
    private float lastPosition;
    private float lengthMultiplier;

    public ResampleInputStream(AudioInputStream input, float newSampleRate) {
        super(input);
        sampleRate = newSampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    public ResampleInputStream(AudioInputStream input, boolean useLittleEndian, float newSampleRate) {
        super(input, useLittleEndian);
        sampleRate = newSampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in     The stream to wrap.
     * @param format
     */
    public ResampleInputStream(InputStream in, AudioFormat format, float sampleRate) {
        super(in, format);
        this.sampleRate = sampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in              The stream to wrap.
     * @param format
     * @param useLittleEndian
     */
    public ResampleInputStream(InputStream in, AudioFormat format, boolean useLittleEndian, float sampleRate) {
        super(in, format, useLittleEndian);
        this.sampleRate = sampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    public short readShort() throws IOException {
        // do interpolation
        // TODO currently only works up to Long.MAX_VALUE. Fix to stream indefinitely.

        // get the nearest positions for the interpolated point
        float currentPosition = (position++ / lengthMultiplier) - lastPosition;
        int nearestLeftPosition = (int) currentPosition;
        int nearestRightPosition = nearestLeftPosition + 1;

        skipBytes((nearestLeftPosition - 2) * 2);
        short nearestLeft = super.readShort();

        short value = (short) ((super.readShort() - nearestLeft) * (currentPosition - nearestLeftPosition) + nearestLeft);    // y=mx+c
        lastPosition = currentPosition;
        // end do interpolation

        // TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used
        return value;
    }

    public void readValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
