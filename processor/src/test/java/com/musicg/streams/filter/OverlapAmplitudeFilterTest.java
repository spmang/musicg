package com.musicg.streams.filter;

import com.musicg.fingerprint.FingerprintProperties;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.EOFException;

/**
 * Created by Scott on 9/30/2015.
 */
public class OverlapAmplitudeFilterTest {

    @Test
    public void testReadShort()  throws Exception {

        String filename = "cock_a_1.wav";
        FingerprintProperties fingerprintProperties = FingerprintProperties.getInstance();

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);
        PipedAudioFilter resampledWaveData = new ResampleFilter(new WaveInputFilter(wave), true, targetRate);
        OverlapAmplitudeFilter overlapAmp = new OverlapAmplitudeFilter(resampledWaveData, 4, 2048); //overlapFactor, fftSampleSize);

        int counter = 0;
        try {
            for (; ; overlapAmp.readShort(), counter++) ;
        } catch (EOFException eofe) {
            // read complete -- expected 73617
            Assert.assertEquals("Read count incorrect", 73617, counter);
        }
    }
}