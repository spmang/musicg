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

package com.musicg.wave.extension;

import com.musicg.streams.AudioFormatInputStream;

import java.io.*;

/**
 * Handles the wave data in amplitude-time domain.
 *
 * @author Jacquet Wong
 */
public final class SampleAmplitudes {

    private SampleAmplitudes() {
        super();
    }

    /**
     * Get the amplitudes of the wave samples (depends on the header).
     * Put the results in an input stream. The resulting stream must support mark and reset.
     * A background thread is started that streams the data to the returned DataInputStream.
     *
     * @param maxSamples Maximum number of samples to consider. A value of -1 will consider all samples up to Integer.MAX_VALUE.
     * @return amplitudes array (signed 16-bit)
     */
    public static DataInputStream getSampleAmplitudes(final AudioFormatInputStream wave, final int maxSamples) throws IOException {
        return null;
    }

    /**
     * Stream the amplitudes to the given output stream.
     *
     * @param maxSamples Maximum number of samples to consider. A value of -1 will consider all samples up to Integer.MAX_VALUE.
     * @param output     The stream to write the data into.
     */
    public static void getSampleAmplitudes(final AudioFormatInputStream wave, final int maxSamples, final DataOutputStream output) throws IOException {
        int bytePerSample = wave.getAudioFormat().getSampleSizeInBits() / 8;
        InputStream data = wave;
        for (int i = 0; i < maxSamples; i++) {
            short amplitude = 0;
            for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
                // little endian
                int value = data.read();
                if (value == -1) {

                    // no more data.
                    output.writeShort(amplitude);
                    return;
                } else {
                    amplitude |= (short) ((value & 0xFF) << (byteNumber * 8));
                }
            }
            output.writeShort(amplitude);
        }
    }

    public static DataInputStream getNormalizedAmplitudes(final AudioFormatInputStream wave, final int maxSamples) throws IOException {
        return null;
    }

    /**
     * Get normalized amplitude of each frame
     */
    public static void getNormalizedAmplitudes(final AudioFormatInputStream wave, final int maxSamples, final DataOutputStream normalizedAmplitudes) throws IOException {

        boolean signed = true;

        // usually 8bit is unsigned
        if (wave.getAudioFormat().getSampleSizeInBits() == 8) {
            signed = false;
        }

        DataInputStream amplitudes = getSampleAmplitudes(wave, maxSamples);

        int maxAmplitude = 1 << (wave.getAudioFormat().getSampleSizeInBits() - 1);

        if (!signed) {    // one more bit for unsigned value
            maxAmplitude <<= 1;
        }

        try {
            while (true) normalizedAmplitudes.writeDouble(amplitudes.readShort() / (double) maxAmplitude);
        } catch (EOFException eofe) {
            // ignore, done reading.
        }
    }
}