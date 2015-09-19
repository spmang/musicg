package com.musicg.streams;

import javax.sound.sampled.AudioInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Scott on 9/18/2015.
 */
public abstract class PipedAudioFormatInputStream extends AudioFormatInputStream {

    /**
     * The Stream to write data to
     */
    protected AudioFormatOutputStream outputStream;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFormatInputStream(final AudioInputStream input) {
        super(input);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFormatInputStream(final AudioInputStream input, final boolean useLittleEndian) {
        super(input);
        littleEndian = useLittleEndian;
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public PipedAudioFormatInputStream(final AudioInputStream input, final AudioFormatOutputStream outputStream, final boolean useLittleEndian) {
        super(input, useLittleEndian);
        this.outputStream = outputStream;
    }

    public abstract void readValue() throws IOException;

    /**
     * Stream the content of this stream to the given output stream.
     *
     * @param output The stream to write to during a read.
     */
    public void connect(final AudioFormatOutputStream output) {
        outputStream = output;
    }
}

