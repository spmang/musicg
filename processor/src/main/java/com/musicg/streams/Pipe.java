package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import java.io.*;

/**
 * Allows stream of data between processes.
 * <p/>
 * Created by scottmangan on 9/18/15.
 */
public class Pipe {

    private AudioFormatInputStream input;
    private AudioFormatOutputStream output;
    private AudioFormat format;

    public Pipe(final AudioFormat header) throws IOException {
        PipedInputStream pipedInput = new PipedInputStream();
        PipedOutputStream pipedOutput = new PipedOutputStream();

        pipedInput.connect(pipedOutput);

        format = header;
        input = new AudioFormatInputStream(new BufferedInputStream(pipedInput), header, !header.isBigEndian());
        output = new AudioFormatOutputStream(pipedOutput, header, !header.isBigEndian());
    }

    public AudioFormatInputStream getInput() {
        return input;
    }

    public AudioFormatOutputStream getOutput() {
        return output;
    }

    public AudioFormat getAudioFormat() {
        return format;
    }
}
