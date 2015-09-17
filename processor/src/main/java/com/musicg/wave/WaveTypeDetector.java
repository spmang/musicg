package com.musicg.wave;

import com.musicg.api.WhistleApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WaveTypeDetector {

    private Wave wave;

    public WaveTypeDetector(Wave wave) {
        this.wave = wave;
    }

    /**
     * Get the whistle probability for the given sample size and max number of frames.
     *
     * @param maxFrames     Maximum number of frames to consider.
     *                      A value of -1 will consider all data available in the stream up to Long.MAX_LONG.
     * @param fftSampleSize The number of samples to check in each frame.
     * @return The probability.
     * @throws IOException
     */
    public double getWhistleProbability(int fftSampleSize, long maxFrames) throws IOException {

        double probability = 0;

        // fft size 1024, no overlap
        int fftSignalByteLength = fftSampleSize * wave.getWaveHeader().getSampleSize();
        InputStream inputStream = wave.getAudioStream();

        WhistleApi whistleApi = new WhistleApi(wave);

        // read the byte signals
        byte[] bytes = new byte[fftSignalByteLength];
        int checkLength = 3;
        int passScore = 3;

        ArrayList<Boolean> bufferList = new ArrayList<>();
        int numWhistles = 0;
        int numPasses = 0;

        // first 10(checkLength) frames
        for (int frameNumber = 0; frameNumber < checkLength; frameNumber++) {
            int bytesRead = 0;
            int read = 0;
            while (bytesRead < fftSignalByteLength && read > -1) {
                read = inputStream.read(bytes, bytesRead, fftSignalByteLength - bytesRead);
                bytesRead += read;
            }
            if (bytesRead == fftSignalByteLength) {
                boolean isWhistle = whistleApi.isWhistle(bytes);
                bufferList.add(isWhistle);
                if (isWhistle) {
                    numWhistles++;
                }
                if (numWhistles >= passScore) {
                    numPasses++;
                }
            } else {
                throw new WaveException("Error loading checkLength data.");
            }
        }

        // other frames
        long frameNumber;
        if (maxFrames < 0) {
            maxFrames = Long.MAX_VALUE;
        }
        for (frameNumber = checkLength; frameNumber < maxFrames; frameNumber++) {
            int bytesRead = 0;
            int read = 0;
            while (bytesRead < fftSignalByteLength && read > -1) {
                read = inputStream.read(bytes, bytesRead, fftSignalByteLength - bytesRead);
                bytesRead += read;
            }
            if (bytesRead == fftSignalByteLength) {
                boolean isWhistle = whistleApi.isWhistle(bytes);
                if (bufferList.get(0)) {
                    numWhistles--;
                }
                bufferList.remove(0);
                bufferList.add(isWhistle);

                if (isWhistle) {
                    numWhistles++;
                }
                if (numWhistles >= passScore) {
                    numPasses++;
                }
            } else {

                // we can no longer read data from the stream, exit the loop if necessary.
                frameNumber = maxFrames;
            }
        }
        return (double) numPasses / frameNumber;
    }
}