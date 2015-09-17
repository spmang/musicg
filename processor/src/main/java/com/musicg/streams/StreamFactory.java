package com.musicg.streams;

import java.io.*;

/**
 * Factory to create DataInputStream for Runnable tasks.
 *
 * @author smangan
 */
public class StreamFactory {

    private StreamFactory() {
        super();
    }

    /**
     * Create a piped stream for the given target.
     *
     * @param target The target item to execute in a thread.
     * @return The DataInputStream to read the results of the streamed data from.
     * @throws IOException
     */
    public static DataInputStream getStreamedTarget(StreamedRunnable target) throws IOException {
        PipedInputStream pipedInput = new PipedInputStream();
        PipedOutputStream pipedOutput = new PipedOutputStream();

        pipedInput.connect(pipedOutput);

        DataInputStream input = new DataInputStream(new BufferedInputStream(pipedInput));
        final DataOutputStream output = new DataOutputStream(pipedOutput);
        target.setOutputStream(output);

        new Thread(target).start();

        return input;
    }
}
