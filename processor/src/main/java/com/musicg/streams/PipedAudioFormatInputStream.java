package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
     * @param input The stream to wrap.
     */
    public PipedAudioFormatInputStream(AudioInputStream input, AudioFormatOutputStream outputStream) {
        super(input);
        this.outputStream = outputStream;
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public PipedAudioFormatInputStream(AudioInputStream input, boolean useLittleEndian, AudioFormatOutputStream outputStream) {
        super(input, useLittleEndian);
        this.outputStream = outputStream;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in     The stream to wrap.
     * @param format
     */
    public PipedAudioFormatInputStream(InputStream in, AudioFormat format, AudioFormatOutputStream outputStream) {
        super(in, format);
        this.outputStream = outputStream;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in              The stream to wrap.
     * @param format
     * @param useLittleEndian
     */
    public PipedAudioFormatInputStream(InputStream in, AudioFormat format, boolean useLittleEndian, AudioFormatOutputStream outputStream) {
        super(in, format, useLittleEndian);
        this.outputStream = outputStream;
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in     The stream to wrap.
     * @param format
     */
    public PipedAudioFormatInputStream(InputStream in, AudioFormat format) {
        super(in, format);
    }

    /**
     * Create a new instance of the input stream.
     *
     * @param in              The stream to wrap.
     * @param format
     * @param useLittleEndian
     */
    public PipedAudioFormatInputStream(InputStream in, AudioFormat format, boolean useLittleEndian) {
        super(in, format, useLittleEndian);
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

