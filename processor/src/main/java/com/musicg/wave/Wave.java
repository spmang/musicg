/*
 * Copyright (C) 2011 Jacquet Wong
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

package com.musicg.wave;

import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.extension.NormalizedSampleAmplitudes;
import com.musicg.wave.extension.Spectrogram;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Read WAVE headers and data from wave input stream
 *
 * @author Jacquet Wong
 * @author Scott Mangan
 */
public class Wave implements Serializable {

    private WaveHeader waveHeader;

    private InputStream data;

    protected Wave(final WaveHeader header, InputStream input) {
        super();
        waveHeader = header;
        data = input;
    }

    /**
     * Trim the wave data
     *
     * @param leftTrimNumberOfSample Number of sample trimmed from beginning
     */
    public static void leftTrim(final WaveHeader waveHeader, final InputStream data, final int leftTrimNumberOfSample) throws IOException {

        long chunkSize = waveHeader.getChunkSize();
        long subChunk2Size = waveHeader.getSubChunk2Size();

        // update wav info
        chunkSize -= leftTrimNumberOfSample;
        subChunk2Size -= leftTrimNumberOfSample;

        if (chunkSize < 0 || subChunk2Size < 0) {
            throw new WaveException("Invalid chunk size: " + chunkSize + ":" + subChunk2Size);
        } else {
            waveHeader.setChunkSize(chunkSize);
            waveHeader.setSubChunk2Size(subChunk2Size);

            // skip to the offset
            long skipped = 0;
            while (skipped < leftTrimNumberOfSample) {
                long read = data.skip(leftTrimNumberOfSample);
                if (read < 0) {
                    // nothing left in the stream.
                    throw new WaveException("End of stream reached before left trim completed.");
                }
                skipped += read;
            }
        }
    }

    /**
     * Trim the wave data
     *
     * @param leftTrimSecond  Seconds trimmed from beginning
     * @param rightTrimSecond Seconds trimmed from ending
     */
    public byte[] trim(double leftTrimSecond, double rightTrimSecond) throws IOException {
        return trim((int) (waveHeader.getTrimSample() * leftTrimSecond), (int) (waveHeader.getTrimSample() * rightTrimSecond));
    }

    /**
     * Trim the wave data from beginning
     *
     * @param second Seconds trimmed from beginning
     */
    public byte[] leftTrim(double second) throws IOException {
        return trim(second, 0);
    }

    /**
     * Trim the wave data from ending
     *
     * @param second Seconds trimmed from ending
     */
    public byte[] rightTrim(double second) throws IOException {
        return trim(0, second);
    }

    /**
     * Get the wave header
     *
     * @return waveHeader
     */
    public WaveHeader getWaveHeader() {
        return waveHeader;
    }

    /**
     * Get the wave spectrogram
     *
     * @return spectrogram
     */
    public Spectrogram getSpectrogram() {
        return new Spectrogram(this);
    }

    /**
     * Get the wave spectrogram
     *
     * @param fftSampleSize number of sample in fft, the value needed to be a number to power of 2
     * @param overlapFactor 1/overlapFactor overlapping, e.g. 1/4=25% overlapping, 0 for no overlapping
     * @return spectrogram
     */
    public Spectrogram getSpectrogram(int fftSampleSize, int overlapFactor) {
        return new Spectrogram(this, fftSampleSize, overlapFactor);
    }

    /**
     * Get the wave data in bytes
     *
     * @return wave data
     */
    public InputStream getBytes() {
        return data;
    }

    /**
     * Length of the wave in second
     *
     * @return length in second
     */
    public float length() {
        return (float) waveHeader.getSubChunk2Size() / waveHeader.getByteRate();
    }

    /**
     * Timestamp of the wave length
     *
     * @return timestamp
     */
    public String timestamp() {
        float totalSeconds = this.length();
        float second = totalSeconds % 60;
        int minute = (int) totalSeconds / 60 % 60;
        int hour = (int) (totalSeconds / 3600);

        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            sb.append(hour + ":");
        }
        if (minute > 0) {
            sb.append(minute + ":");
        }
        sb.append(second);

        return sb.toString();
    }

    /**
     * Get the amplitudes of the wave samples (depends on the header)
     *
     * @param maxSamples Maximum number of samples to consider. A value of -1 will consider all samples up to Integer.MAX_VALUE.
     * @return amplitudes array (signed 16-bit)
     */
    public short[] getSampleAmplitudes(int maxSamples) throws IOException {
        int bytePerSample = waveHeader.getSampleSize() / 8;

        short[] amplitudes = new short[numSamples];

        int pointer = 0;
        for (int i = 0; i < maxSamples; i++) {
            short amplitude = 0;
            for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
                // little endian
                amplitude |= (short) ((data.read() & 0xFF) << (byteNumber * 8));
            }
            amplitudes[i] = amplitude;
        }
        return amplitudes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(waveHeader.toString());
        sb.append("\n");
        sb.append("length: ");
        sb.append(timestamp());
        return sb.toString();
    }

    public double[] getNormalizedAmplitudes() {
        NormalizedSampleAmplitudes amplitudes = new NormalizedSampleAmplitudes(this);
        return amplitudes.getNormalizedAmplitudes();
    }

    public FingerprintSimilarity getFingerprintSimilarity(Wave wave) {
        List<Byte> fingerprint = new FingerprintManager().extractFingerprint(this);
        FingerprintSimilarityComputer fingerprintSimilarityComputer = new FingerprintSimilarityComputer(this.getFingerprint().toArray(), wave.getFingerprint().toArray());
        return fingerprintSimilarityComputer.getFingerprintsSimilarity();
    }
}