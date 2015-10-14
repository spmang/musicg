package com.musicg.streams.filter;

import com.musicg.fingerprint.FingerprintProperties;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.EOFException;

/**
 * Created by Scott on 9/18/2015.
 */
public class ResampleFilterTest {

    @Test
    public void testReadValue() throws Exception {

        String filename = "cock_a_1.wav";
        FingerprintProperties fingerprintProperties = FingerprintProperties.getInstance();

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);
        PipedAudioFilter resampledWaveData = new ResampleFilter(new WaveInputFilter(wave), true, targetRate);

        Assert.assertEquals("Resampled value is incorrect.", 259, resampledWaveData.readShort());
        Assert.assertEquals("Resampled value is incorrect.", 505, resampledWaveData.readShort());
        int counter = 2;
        try {
            for (; ; resampledWaveData.readShort(), counter++) ;
        } catch (EOFException eofe) {
            // read complete -- expected 19857
            Assert.assertEquals("Resample count incorrect.", 19857, counter);
        }
    }
}