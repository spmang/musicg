package com.musicg.streams.filter;

import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatOutputStream;
import com.musicg.streams.filter.PipedAudioFilter;

import java.io.IOException;

/**
 * Filter input that allows an AudioFormatInputStream to be attached to any of the other filters.
 *
 * @author Scott Mangan
 */
public class WaveInputFilter extends PipedAudioFilter {

    /**
     * Create a new Stream from the given stream.
     *
     * @param input The stream to wrap.
     */
    public WaveInputFilter(AudioFormatInputStream input) {
        super(input);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     */
    public WaveInputFilter(AudioFormatInputStream input, boolean useLittleEndian) {
        super(input, useLittleEndian);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input        The stream to wrap.
     * @param outputStream
     */
    public WaveInputFilter(AudioFormatInputStream input, AudioFormatOutputStream outputStream) {
        super(input, outputStream);
    }

    /**
     * Create a new Stream from the given stream.
     *
     * @param input           The stream to wrap.
     * @param useLittleEndian
     * @param outputStream
     */
    public WaveInputFilter(AudioFormatInputStream input, boolean useLittleEndian, AudioFormatOutputStream outputStream) {
        super(input, useLittleEndian, outputStream);
    }

    /**
     * Read a short value from the audio stream and pipe it to the attached output stream.
     *
     * @throws IOException
     */
    public void pipeValue() throws IOException {
        outputStream.writeShort(readShort());
    }
}
