package com.musicg.streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by scottmangan on 9/18/15.
 */
public class IntensityInputStream extends AudioFormatInputStream {


    public IntensityInputStream(AudioInputStream input) {
        super(input);
    }

    public IntensityInputStream(InputStream in, AudioFormat format) {
        super(in, format);
    }

    private static InputStream createIntensityStream(final int numFrames, final int numRobustPointsPerFrame,
                                                     final int[][] coordinates,
                                                     final double[][] spectorgramData) throws IOException {
        // for each valid coordinate, append with its intensity
        return StreamFactory.getStreamedTarget(new AbstractStreamRunnable() {
            @Override
            public void run() {
                try {
                    createIntensityStream(numFrames, numRobustPointsPerFrame, coordinates, spectorgramData, outputStream);
                } catch (IOException ioe) {
                    // TODO send notification
                    ioe.printStackTrace();
                }
            }
        });
    }


    public static void createIntensityStream(final int numFrames, final int numRobustPointsPerFrame,
                                             final int[][] coordinates, final double[][] spectorgramData,
                                             final OutputStream outputStream) throws IOException {
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
    }
}
