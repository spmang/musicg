package com.musicg.wave;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Header created using the standard system available audio readers.
 * <p/>
 * Created by Scott on 9/15/2015.
 */
public class SystemWaveHeader implements WaveHeader {

    private AudioFormat format;

    private int sampleSize;

    protected SystemWaveHeader(final AudioInputStream input) {
        super();
        format = input.getFormat();
        sampleSize = format.getSampleSizeInBits() / 8;
    }

    public String getFormatName() {
        return format.getEncoding().toString();
    }

    public int getChannels() {
        return format.getChannels();
    }

    public float getSampleRate() {
        return format.getSampleRate();
    }

    public float getByteRate() {
        return format.getFrameRate();
    }

    public int getFrameSize() {
        return format.getFrameSize();
    }

    public float getFrameRate() {
        return format.getFrameRate();
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public int getBitsPerSample() {
        return format.getSampleSizeInBits();
    }
}
