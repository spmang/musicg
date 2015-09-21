package com.musicg.streams.filter;

import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatOutputStream;

import java.io.IOException;

/**
 * Created by Scott on 9/18/2015.
 */
public abstract class PipedAudioFilter extends AudioFormatInputStream {

    /**
     * The Stream to write data to.
     */
    protected AudioFormatOutputStream outputStream;

    protected PipedAudioFilter inputStream;

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFilter(final PipedAudioFilter input) {
        super(input, input.audioFormat);
        inputStream = input;
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFilter(final PipedAudioFilter input, final boolean useLittleEndian) {
        super(input, input.audioFormat, useLittleEndian);
        inputStream = input;
    }


    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFilter(PipedAudioFilter input, AudioFormatOutputStream outputStream) {
        super(input, input.audioFormat);
        inputStream = input;
        this.outputStream = outputStream;
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public PipedAudioFilter(PipedAudioFilter input, boolean useLittleEndian, AudioFormatOutputStream outputStream) {
        super(input, input.audioFormat, useLittleEndian);
        inputStream = input;
        this.outputStream = outputStream;
    }


    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFilter(final AudioFormatInputStream input) {
        super(input, input.getAudioFormat());
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFilter(final AudioFormatInputStream input, final boolean useLittleEndian) {
        super(input, input.getAudioFormat(), useLittleEndian);
    }


    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public PipedAudioFilter(AudioFormatInputStream input, AudioFormatOutputStream outputStream) {
        super(input, input.getAudioFormat());
        this.outputStream = outputStream;
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public PipedAudioFilter(AudioFormatInputStream input, boolean useLittleEndian, AudioFormatOutputStream outputStream) {
        super(input, input.getAudioFormat(), useLittleEndian);
        this.outputStream = outputStream;
    }


    /**
     * Read a value from the underlying stream and send it to the output stream.
     *
     * @throws IOException
     */
    public abstract void pipeValue() throws IOException;

    /**
     * Stream the content of this stream to the given output stream.
     *
     * @param output The stream to write to during a read.
     */
    public void connect(final AudioFormatOutputStream output) {
        outputStream = output;
    }
}

