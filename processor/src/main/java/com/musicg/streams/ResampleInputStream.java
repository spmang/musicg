package com.musicg.streams;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

/**
 * Resample the audio stream.
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

    public void readValue() throws IOException {
        // do interpolation
        // TODO currently only works up to Long.MAX_VALUE. Fix to stream indefinitely.

        // get the nearest positions for the interpolated point
        float currentPosition = (position++ / lengthMultiplier) - lastPosition;
        int nearestLeftPosition = (int) currentPosition;
        int nearestRightPosition = nearestLeftPosition + 1;

        skipBytes(nearestLeftPosition);
        short nearestLeft = readShort();

        outputStream.writeShort((short) ((readShort() - nearestLeft) * (currentPosition - nearestLeftPosition) + nearestLeft));    // y=mx+c
        lastPosition = currentPosition;
        // end do interpolation

        // TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used
    }
}
