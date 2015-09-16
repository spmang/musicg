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

import java.io.*;

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
     * Get the wave header
     *
     * @return waveHeader
     */
    public WaveHeader getWaveHeader() {
        return waveHeader;
    }

    /**
     * Get the wave data in bytes
     *
     * @return wave data
     */
    public InputStream getAudioStream() {
        return data;
    }

    /**
     * Length of the wave in second
     * <p/>
     * TODO fix this to be based on samples.
     *
     * @return length in second
     */
    public float length() {
        return (float) waveHeader.getSubChunk2Size() / waveHeader.getByteRate();
    }

    /**
     * Timestamp of the wave length
     * <p/>
     * TODO do we need this?
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

    public String toString() {
        StringBuilder sb = new StringBuilder(waveHeader.toString());
        sb.append("\n");
        sb.append("length: ");
        sb.append(timestamp());
        return sb.toString();
    }
}