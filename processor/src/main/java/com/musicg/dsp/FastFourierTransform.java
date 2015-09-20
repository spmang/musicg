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

package com.musicg.dsp;

import com.musicg.spectrogram.FFT;

/**
 * FFT object, transform amplitudes to frequency intensities
 *
 * @author Jacquet Wong
 * @author Scott Mangan
 */
public class FastFourierTransform {

    // Find max and min amplitudes
    private double maxAmp = Double.MIN_VALUE;
    private double minAmp = Double.MAX_VALUE;

    /**
     * Get the frequency intensities
     *
     * @param amplitudes amplitudes of the signal
     * @return intensities of each frequency unit: mag[frequency_unit]=intensity
     */
    public double[] getMagnitudes(double[] amplitudes) {

        int indexSize = amplitudes.length / 2;

        // call the fft and transform the complex numbers
        FFT fft = new FFT(indexSize, -1);
        fft.transform(amplitudes);
        // end call the fft and transform the complex numbers

        // even indexes (0,2,4,6,...) are real parts
        // odd indexes (1,3,5,7,...) are img parts

        // FFT produces a transformed pair of arrays where the first half of the
        // values represent positive frequency components and the second half
        // represents negative frequency components.
        // we omit the negative ones
        double[] mag = new double[indexSize / 2];
        for (int i = 0; i < indexSize; i += 2) {
            double value = Math.sqrt(amplitudes[i] * amplitudes[i] + amplitudes[i + 1] * amplitudes[i + 1]);
            mag[i / 2] = value;
            if (value > maxAmp) {
                maxAmp = value;
            } else if (value < minAmp) {
                minAmp = value;
            }
        }
        return mag;
    }

    public double getMaxAmp() {
        return maxAmp;
    }

    public double getMinAmp() {
        return minAmp;
    }
}
