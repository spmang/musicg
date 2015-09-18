package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.*;

/**
 * Created by scottmangan on 9/18/15.
 */
public class IntensityInputStream extends AudioFormatInputStream {

    int numFrames;
    int numRobustPointsPerFrame;
    int[][] coordinates;
    double[][] spectorgramData;

    public IntensityInputStream(AudioInputStream input) {
        super(input);
    }

    public IntensityInputStream(InputStream in, AudioFormat format) {
        super(in, format);
    }

    public short readValue(OutputStream outputStream) throws IOException {

        // TODO this should read the spectrogram data from the underlying stream
        for (int i = 0; i < numFrames; i++) {
            for (int j = 0; j < numRobustPointsPerFrame; j++) {
                if (coordinates[i][j] != -1) {
                    // first 2 bytes is x
                    outputStream.write((byte) (i >> 8));
                    outputStream.write((byte) i);

                    // next 2 bytes is y
                    int y = coordinates[i][j];
                    outputStream.write((byte) (y >> 8));
                    outputStream.write((byte) y);

                    // next 4 bytes is intensity
                    // spectorgramData is ranged from 0~1
                    int intensity = (int) (spectorgramData[i][y] * Integer.MAX_VALUE);
                    outputStream.write((byte) (intensity >> 24));
                    outputStream.write((byte) (intensity >> 16));
                    outputStream.write((byte) (intensity >> 8));
                    outputStream.write((byte) intensity);
                }
            }
        }
        return -1;
    }

    /**
     * Stream the content of this stream to the given output stream.
     *
     * @param output
     */
    public void connect(OutputStream output) throws IOException {
        short value;
        while ((value = readValue(output)) > -1);
        throw new EOFException("Stream closed");
    }
}
