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
import com.musicg.streams.AudioFormatOutputStream;
import com.musicg.streams.filter.IntensityAudioFilter;
import com.musicg.streams.filter.PipedAudioFilter;
import com.musicg.streams.filter.ResampleFilter;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
    public static PipedAudioFilter extractFingerprint(final PipedAudioFilter wave, FingerprintProperties fingerprintProperties) throws IOException {

        if (fingerprintProperties == null) {
            // use default
            fingerprintProperties = FingerprintProperties.getInstance();
        }

        int sampleSizePerFrame = fingerprintProperties.getSampleSizePerFrame();
        int numRobustPointsPerFrame = fingerprintProperties.getNumRobustPointsPerFrame();
        int numFilterBanks = fingerprintProperties.getNumFilterBanks();

        int[][] coordinates;    // coordinates[x][0..3]=y0..y3

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();
        PipedAudioFilter resampledWaveData = new ResampleFilter(wave, targetRate);
        // end resample to target rate

        // get spectrogram's data
        Spectrogram spectrogram = new Spectrogram(resampledWaveData, sampleSizePerFrame, fingerprintProperties.getOverlapFactor());
        List<double[]> spectorgramData = spectrogram.getNormalizedSpectrogram();

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
        return createIntensityStream(coordinates, spectorgramData);
    }

    public static IntensityAudioFilter createIntensityStream(final int[][] coordinates, final List<double[]> spectorgramData) throws IOException {
        return new IntensityAudioFilter(coordinates, spectorgramData);
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
    public static void saveFingerprintAsFile(final PipedAudioFilter fingerprint, final String filename) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            File outFile = new File(filename);
            outFile.getParentFile().mkdirs();
            fileOutputStream = new FileOutputStream(filename);
            fingerprint.connect(new AudioFormatOutputStream(fileOutputStream, fingerprint.getAudioFormat()));
            for (fingerprint.pipeValue(); ; ) ;
        } catch (EOFException eofe) {
            // complete
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    // robustLists[x]=y1,y2,y3,...
    private static List<Integer>[] getRobustPointList(List<double[]> spectrogramData, int numFilterBanks) {

        int numX = spectrogramData.size();
        int numY = spectrogramData.get(0).length;

        double[][] allBanksIntensities = new double[numX][numY];
        int bandwidthPerBank = numY / numFilterBanks;

        for (int b = 0; b < numFilterBanks; b++) {

            List<double[]> bankIntensities = new ArrayList<double[]>();

            for (int i = 0; i < numX; i++) {
                double[] frame = spectrogramData.get(i);
                double[] intensities = new double[bandwidthPerBank];
                bankIntensities.add(intensities);
                for (int j = 0; j < bandwidthPerBank; j++) {
                    intensities[j] = frame[j + b * bandwidthPerBank];
                }
            }

            // get the most robust point in each filter bank
            TopManyPointsProcessorChain processorChain = new TopManyPointsProcessorChain(bankIntensities, 1);
            List<double[]> processedIntensities = processorChain.getIntensities();

            for (int i = 0; i < numX; i++) {
                double[] frame = processedIntensities.get(i);
                for (int j = 0; j < bandwidthPerBank; j++) {
                    allBanksIntensities[i][j + b * bandwidthPerBank] = frame[j];
                }
            }
        }

        List<int[]> robustPointList = new ArrayList<>();

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

        List<Integer>[] robustLists = new ArrayList[spectrogramData.size()];
        for (int i = 0; i < robustLists.length; i++) {
            robustLists[i] = new ArrayList<>();
        }

        // robustLists[x]=y1,y2,y3,...
        for (int[] coor : robustPointList) {
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