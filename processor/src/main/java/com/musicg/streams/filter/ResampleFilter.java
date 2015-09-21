package com.musicg.streams.filter;

import java.io.IOException;

/**
 * Resample the audio stream. Currenly only works for short type on wave.
 * <p/>
 * <p/>
 * Created by Scott on 9/18/2015.
 */
public class ResampleFilter extends PipedAudioFilter {

    private float sampleRate;
    private long position;
    private int lastPosition;
    private float lengthMultiplier;

    public ResampleFilter(PipedAudioFilter input, float newSampleRate) {
        super(input);
        sampleRate = newSampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    public ResampleFilter(PipedAudioFilter input, boolean useLittleEndian, float newSampleRate) {
        super(input, useLittleEndian);
        sampleRate = newSampleRate;
        lengthMultiplier = (float) sampleRate / audioFormat.getSampleRate();
    }

    public short readShort() throws IOException {
        // do interpolation
        // TODO currently only works up to Long.MAX_VALUE. Fix to stream indefinitely.

        // get the nearest positions for the interpolated point
        float currentPosition = position++ / lengthMultiplier;
        int nearestLeftPosition = (int) currentPosition;
        int nearestRightPosition = nearestLeftPosition + 1;

        skipBytes(((nearestLeftPosition - lastPosition) - 2) * 2);
        short nearestLeft = super.readShort();

        short value = (short) ((super.readShort() - nearestLeft) * (currentPosition - nearestLeftPosition) + nearestLeft);    // y=mx+c
        lastPosition = nearestLeftPosition;
        // end do interpolation

        // TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used
        return value;
    }

    public void pipeValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
