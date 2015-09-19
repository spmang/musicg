/*
 * Copyright (C) 2012 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.musicg.fingerprint;

import com.musicg.processor.TopManyPointsProcessorChain;
import com.musicg.spectrogram.Spectrogram;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Audio fingerprint manager, handle fingerprint operations
 *
 * @author jacquet
 * @author Scott Mangan
 */
public class FingerprintManager {

    /**
     * Constructor
     */
    private FingerprintManager() {
        super();
    }

    /**
     * Extract fingerprint from Wave object
     *
     * @param wave Wave Object to be extracted fingerprint
     * @return fingerprint in bytes
     */
    public static InputStream extractFingerprint(final AudioFormatInputStream wave, FingerprintProperties fingerprintProperties) throws IOException {

        if (fingerprintProperties == null) {
            // use default
            fingerprintProperties = FingerprintProperties.getInstance();
        }

        int sampleSizePerFrame = fingerprintProperties.getSampleSizePerFrame();
        int overlapFactor = fingerprintProperties.getOverlapFactor();
        int numRobustPointsPerFrame = fingerprintProperties.getNumRobustPointsPerFrame();
        int numFilterBanks = fingerprintProperties.getNumFilterBanks();

        int[][] coordinates;    // coordinates[x][0..3]=y0..y3

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream resampledWaveData = AudioFormatInputStreamFactory.createResampleStream(wave, targetRate);
        AudioFormat resampledWaveHeader = resampledWaveData.getAudioFormat();
        // end resample to target rate


        // get spectrogram's data
        Spectrogram spectrogram = new Spectrogram(resampledWaveData, sampleSizePerFrame, overlapFactor);
        spectrogram.buildSpectrogram(-1);
        double[][] spectorgramData = spectrogram.getNormalizedSpectrogramData();

        List<Integer>[] pointsLists = getRobustPointList(spectorgramData, numFilterBanks);
        int numFrames = pointsLists.length;

        // prepare fingerprint bytes
        coordinates = new int[numFrames][numRobustPointsPerFrame];

        for (int x = 0; x < numFrames; x++) {
            if (pointsLists[x].size() == numRobustPointsPerFrame) {
                Iterator<Integer> pointsListsIterator = pointsLists[x].iterator();
                for (int y = 0; y < numRobustPointsPerFrame; y++) {
                    coordinates[x][y] = pointsListsIterator.next();
                }
            } else {
                // use -1 to fill the empty byte
                for (int y = 0; y < numRobustPointsPerFrame; y++) {
                    coordinates[x][y] = -1;
                }
            }
        }
        // end make fingerprint
        // end for each valid coordinate, append with its intensity
        return createIntensityStream(numFrames, numRobustPointsPerFrame, coordinates, spectorgramData);
    }


    private static InputStream createIntensityStream(final int numFrames, final int numRobustPointsPerFrame,
                                                     final int[][] coordinates,
                                                     final double[][] spectorgramData) throws IOException {
        // for each valid coordinate, append with its intensity
        return null;
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

    public static FingerprintSimilarity getFingerprintSimilarity(AudioFormatInputStream wave1, AudioFormatInputStream wave2) throws IOException {
        return new FingerprintSimilarityComputer(getFingerprint(wave1),
                getFingerprint(wave2)).getFingerprintsSimilarity();
    }

    /**
     * Get bytes from fingerprint file
     *
     * @param fingerprintFile fingerprint filename
     * @return fingerprint in bytes
     */
    public static byte[] getFingerprint(String fingerprintFile) throws IOException, URISyntaxException {
        URL input = Thread.currentThread().getContextClassLoader().getResource(fingerprintFile);
        if (input == null) {
            throw new IOException("File not found.");
        }

        InputStream fis = null;
        try {
            fis = new FileInputStream(new File(input.toURI()));
            return getFingerprint(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * Get bytes from fingerprint inputstream
     *
     * @param inputStream fingerprint inputstream
     * @return fingerprint in bytes
     */
    public static byte[] getFingerprint(InputStream inputStream) throws IOException {
        byte[] fingerprint = new byte[inputStream.available()];
        int bytesRead = inputStream.read(fingerprint);
        // TODO verify that bytesRead == fingerprint.length
        return fingerprint;
    }

    /**
     * Save fingerprint to a file
     *
     * @param fingerprint fingerprint bytes
     * @param filename    fingerprint filename
     */
    public static void saveFingerprintAsFile(final InputStream fingerprint, final String filename) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            byte[] data = new byte[1024];
            for (int read = fingerprint.read(data); read > -1; read = fingerprint.read(data)) {
                fileOutputStream.write(data, 0, read);
            }
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    // robustLists[x]=y1,y2,y3,...
    private static List<Integer>[] getRobustPointList(double[][] spectrogramData, int numFilterBanks) {

        int numX = spectrogramData.length;
        int numY = spectrogramData[0].length;

        double[][] allBanksIntensities = new double[numX][numY];
        int bandwidthPerBank = numY / numFilterBanks;

        for (int b = 0; b < numFilterBanks; b++) {

            double[][] bankIntensities = new double[numX][bandwidthPerBank];

            for (int i = 0; i < numX; i++) {
                for (int j = 0; j < bandwidthPerBank; j++) {
                    bankIntensities[i][j] = spectrogramData[i][j + b * bandwidthPerBank];
                }
            }

            // get the most robust point in each filter bank
            TopManyPointsProcessorChain processorChain = new TopManyPointsProcessorChain(bankIntensities, 1);
            double[][] processedIntensities = processorChain.getIntensities();

            for (int i = 0; i < numX; i++) {
                for (int j = 0; j < bandwidthPerBank; j++) {
                    allBanksIntensities[i][j + b * bandwidthPerBank] = processedIntensities[i][j];
                }
            }
        }

        List<int[]> robustPointList = new LinkedList<>();

        // find robust points
        for (int i = 0; i < allBanksIntensities.length; i++) {
            for (int j = 0; j < allBanksIntensities[i].length; j++) {
                if (allBanksIntensities[i][j] > 0) {

                    int[] point = new int[]{i, j};
                    //System.out.println(i+","+frequency);
                    robustPointList.add(point);
                }
            }
        }
        // end find robust points

        List<Integer>[] robustLists = new LinkedList[spectrogramData.length];
        for (int i = 0; i < robustLists.length; i++) {
            robustLists[i] = new LinkedList<Integer>();
        }

        // robustLists[x]=y1,y2,y3,...
        Iterator<int[]> robustPointListIterator = robustPointList.iterator();
        while (robustPointListIterator.hasNext()) {
            int[] coor = robustPointListIterator.next();
            robustLists[coor[0]].add(coor[1]);
        }

        // return the list per frame
        return robustLists;
    }

    /**
     * Number of frames in a fingerprint
     * Each frame lengths 8 bytes
     * Usually there is more than one point in each frame, so it cannot simply divide the bytes length by 8
     * Last 8 byte of thisFingerprint is the last frame of this wave
     * First 2 byte of the last 8 byte is the x position of this wave, i.e. (number_of_frames-1) of this wave
     *
     * @param fingerprint fingerprint bytes
     * @return number of frames of the fingerprint
     */
    public static int getNumFrames(byte[] fingerprint) {

        if (fingerprint.length < 8) {
            return 0;
        }

        // get the last x-coordinate (length-8&length-7)bytes from fingerprint
        return ((fingerprint[fingerprint.length - 8] & 0xff) << 8 | fingerprint[fingerprint.length - 7] & 0xff) + 1;
    }
}