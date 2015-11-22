package com.musicg.streams.filter;

import javax.sound.sampled.AudioFormat;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;

/**
 * Created by scottmangan on 9/18/15.
 */
public class IntensityAudioFilter extends PipedAudioFilter {
    private int[][] coordinates;
    private List<double[]> spectrogramData;
    private int index = 0;

    /**
     * Build an basic intensity filter.
     *
     * @param format The original audio format.
     * @param coordinates
     * @param spectrogramData
     */
    public IntensityAudioFilter(final AudioFormat format, int[][] coordinates, List<double[]> spectrogramData) {
        super();
        this.coordinates = coordinates;
        this.spectrogramData = spectrogramData;
        audioFormat = format;
    }

    /**
     * Pipe the value to the connected outputstream.
     *
     * @return The number of bytes sent, or -1 for end f stream.
     * @throws IOException
     */
    public int pipeValue() throws IOException {
        int length = 0;
        if (index >= spectrogramData.size()) {
            throw new EOFException("End of Stream.");
        }
        double[] frame = spectrogramData.get(index);
        for (int j = 0; j < coordinates[index].length; j++) {
            if (coordinates[index][j] != -1) {
                // first 2 bytes is x
                outputStream.write((byte) (index >> 8));
                outputStream.write((byte) index);

                // next 2 bytes is y
                int y = coordinates[index][j];
                outputStream.write((byte) (y >> 8));
                outputStream.write((byte) y);

                // next 4 bytes is intensity
                // spectorgramData is ranged from 0~1
                int intensity = (int) (frame[y] * Integer.MAX_VALUE);
                outputStream.write((byte) (intensity >> 24));
                outputStream.write((byte) (intensity >> 16));
                outputStream.write((byte) (intensity >> 8));
                outputStream.write((byte) intensity);
                length += 8;
            }
        }
        index++;
        return length;
    }
}
