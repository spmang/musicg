package com.musicg.streams;

import com.musicg.fingerprint.FingerprintProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.DataOutputStream;

import static org.junit.Assert.*;

/**
 * Created by Scott on 9/18/2015.
 */
public class ResampleInputStreamTest {

    @Test
    public void testReadValue() throws Exception {

        String filename = "cock_a_1.wav";
        FingerprintProperties fingerprintProperties = FingerprintProperties.getInstance();

        // resample to target rate
        int targetRate = fingerprintProperties.getSampleRate();

        AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);
        AudioFormatInputStream resampledWaveData = new ResampleInputStream(wave, wave.getAudioFormat(), true, targetRate);

        Assert.assertEquals("Resampled value is incorrect.", 259, resampledWaveData.readShort());
        Assert.assertEquals("Resampled value is incorrect.", 505, resampledWaveData.readShort());
    }
}