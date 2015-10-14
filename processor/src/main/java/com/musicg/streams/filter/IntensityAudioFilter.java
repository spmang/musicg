package com.musicg.streams.filter;

import java.io.IOException;
import java.util.List;

/**
 * Created by scottmangan on 9/18/15.
 */
public class IntensityAudioFilter extends PipedAudioFilter {
    private int[][] coordinates;
    private List<double[]> spectrogramData;
    private int index = 0;

    public IntensityAudioFilter(int[][] coordinates, List<double[]> spectrogramData) {
        super();
        this.coordinates = coordinates;
        this.spectrogramData = spectrogramData;
    }

    public void pipeValue() throws IOException {

        // TODO this should read the spectrogram data from the underlying stream
        if (index < spectrogramData.size()) {
            double[] frame = spectrogramData.get(index);
            for (int j = 0; j < frame.length; j++) {
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
                }
            }
        }
    }
}
