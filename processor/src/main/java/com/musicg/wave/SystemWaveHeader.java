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

    protected SystemWaveHeader(final AudioInputStream input) {
        super();
        format = input.getFormat();
    }

    public long getChunkSize() {

    }

    public long getSubChunk1Size() {

    }

    public int getAudioFormat() {

    }

    public int getChannels() {

    }

    public int getSampleRate() {

    }

    public int getByteRate() {

    }

    public int getBlockAlign() {

    }

    public int getSampleSize() {

    }

    public long getSubChunk2Size() {

    }

    public int getTrimSample() {

    }
}
