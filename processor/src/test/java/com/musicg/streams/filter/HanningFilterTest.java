package com.musicg.streams.filter;

import com.musicg.fingerprint.FingerprintProperties;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.EOFException;


/**
 * Created by Scott on 10/3/2015.
 */
public class HanningFilterTest {

    @Test
    public void testReadDouble() throws Exception {

        String filename = "cock_a_1.wav";
        FingerprintProperties fingerprintProperties = FingerprintProperties.getInstance();

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);
        PipedAudioFilter resampledWaveData = new ResampleFilter(new WaveInputFilter(wave), true, targetRate);
        OverlapAmplitudeFilter overlapAmp = new OverlapAmplitudeFilter(resampledWaveData, 4, 2048);
        HanningFilter hamming = new HanningFilter(overlapAmp, true, 2048);

        int counter = 0;
        try {
            for (; ; hamming.readDouble(), counter++) {

            }
        } catch (EOFException eofe) {
            Assert.assertEquals("Incorrect Read count.", 77312, counter);
        }
    }
}