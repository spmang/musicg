package com.musicg.streams.filter;

import com.musicg.fingerprint.FingerprintProperties;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.EOFException;

/**
 * Created by Scott on 10/14/2015.
 */
public class FftFilterTest {

    @Test
    public void TestReadCount() throws Exception {

        String filename = "cock_a_1.wav";
        FingerprintProperties fingerprintProperties = FingerprintProperties.getInstance();

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);
        PipedAudioFilter resampledWaveData = new ResampleFilter(new WaveInputFilter(wave), true, targetRate);
        OverlapAmplitudeFilter overlapAmp = new OverlapAmplitudeFilter(resampledWaveData, 4, 2048);
        FftFilter filter = new FftFilter(new HanningFilter(overlapAmp, true, 2048), 2048);

        int counter = 0;
        try {
            for (; ; filter.readFrame(), counter++) {

            }
        } catch (EOFException eofe) {
            Assert.assertEquals("Incorrect Read count.", 36, counter);
        }
    }
}