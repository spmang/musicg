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

public class WaveFileManager {

    /**
     * Save the wave file
     *
     * @param wave     The Wave to save to the file.
     * @param filename filename to be saved
     */
    public static void saveWaveAsFile(final Wave wave, String filename) {
        // TODO
    }

    /**
     * Save he given Wave to a file.
     *
     * @param wave     The Wave to save.
     * @param filename The name of the file to create.
     * @param offset   The number of offset samples into the stream to start saving at.
     */
    public static void saveWaveAsFile(final Wave wave, String filename, long offset) {
        // TODO
    }

    /**
     * @param wave     The wave to save to the output.
     * @param filename The name of the file to create.
     * @param offset   The number of offset samples into the stream to start saving at.
     * @param length   The number of samples to save.
     */
    public static void saveWaveAsFile(final Wave wave, String filename, double offset, double length) {
        // TODO
    }
}