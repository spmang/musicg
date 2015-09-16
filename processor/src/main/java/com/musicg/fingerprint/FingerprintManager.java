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

import com.musicg.dsp.Resampler;
import com.musicg.processor.TopManyPointsProcessorChain;
import com.musicg.properties.FingerprintProperties;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveFactory;
import com.musicg.wave.WaveHeader;
import com.musicg.spectrogram.Spectrogram;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
    public static List<Byte> extractFingerprint(Wave wave, FingerprintProperties fingerprintProperties) throws IOException {

        int sampleSizePerFrame = fingerprintProperties.getSampleSizePerFrame();
        int overlapFactor = fingerprintProperties.getOverlapFactor();
        int numRobustPointsPerFrame = fingerprintProperties.getNumRobustPointsPerFrame();
        int numFilterBanks = fingerprintProperties.getNumFilterBanks();

        int[][] coordinates;    // coordinates[x][0..3]=y0..y3

        // resample to target rate
        Resampler resampler = new Resampler();
        float sourceRate = wave.getWaveHeader().getSampleRate();
        int targetRate = fingerprintProperties.getSampleRate();


        byte[] resampledWaveData = resampler.reSample(wave, wave.getWaveHeader().getSampleSize(), wave.getWaveHeader().getSampleRate(), targetRate);

        // update the wave header
        Wave resampledWave = WaveFactory.createWave(wave);
        WaveHeader resampledWaveHeader = resampledWave.getWaveHeader();
        resampledWaveHeader.setSampleRate(targetRate);


// TODO fix me
        // make resampled wave
        //Wave resampledWave = new Wave(resampledWaveHeader, resampledWaveData);
        // end resample to target rate

        // get spectrogram's data
        Spectrogram spectrogram = new Spectrogram(resampledWave, sampleSizePerFrame, overlapFactor);
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

        // for each valid coordinate, append with its intensity
        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            for (int j = 0; j < numRobustPointsPerFrame; j++) {
                if (coordinates[i][j] != -1) {
                    // first 2 bytes is x
                    byteList.add((byte) (i >> 8));
                    byteList.add((byte) i);

                    // next 2 bytes is y
                    int y = coordinates[i][j];
                    byteList.add((byte) (y >> 8));
                    byteList.add((byte) y);

                    // next 4 bytes is intensity
                    int intensity = (int) (spectorgramData[i][y] * Integer.MAX_VALUE);    // spectorgramData is ranged from 0~1
                    byteList.add((byte) (intensity >> 24));
                    byteList.add((byte) (intensity >> 16));
                    byteList.add((byte) (intensity >> 8));
                    byteList.add((byte) intensity);
                }
            }
        }
        // end for each valid coordinate, append with its intensity
        return byteList;
    }

    public static FingerprintSimilarity getFingerprintSimilarity(Wave wave1, Wave wave2) throws IOException {
        return new FingerprintSimilarityComputer(getFingerprint(wave1.getAudioStream()),
                getFingerprint(wave2.getAudioStream())).getFingerprintsSimilarity();
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
    public void saveFingerprintAsFile(byte[] fingerprint, String filename) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(fingerprint);
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
        int numFrames = ((int) (fingerprint[fingerprint.length - 8] & 0xff) << 8 | (int) (fingerprint[fingerprint.length - 7] & 0xff)) + 1;
        return numFrames;
    }
}