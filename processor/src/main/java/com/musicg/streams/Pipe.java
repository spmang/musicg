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
    private DataOutputStream output;

    public Pipe(final AudioFormat header) throws IOException {
        PipedInputStream pipedInput = new PipedInputStream();
        PipedOutputStream pipedOutput = new PipedOutputStream();

        pipedInput.connect(pipedOutput);

        input = new AudioFormatInputStream(new BufferedInputStream(pipedInput), header);
        output = new DataOutputStream(pipedOutput);
    }

    public AudioFormatInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
        return output;
    }
}
