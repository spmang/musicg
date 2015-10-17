/*
 * Copyright (C) 2012 Jacquet Wong
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
package com.musicg.main.demo;

import com.musicg.fingerprint.FingerprintManager;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;
import com.musicg.streams.filter.PipedAudioFilter;
import com.musicg.streams.filter.WaveInputFilter;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class FingerprintDemo {

    public static void main(String[] args) {

        String filename = "cock_a_1.wav";

        // create a wave object
        try {
            AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/" + filename);

            // get the fingerprint
            PipedAudioFilter fingerprint = FingerprintManager.extractFingerprint(new WaveInputFilter(wave), null);

            // dump the fingerprint
            try {
                FingerprintManager.saveFingerprintAsFile(fingerprint, new File("out/" + filename + ".fingerprint").getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // load fingerprint from file
            try {
                byte[] fingerprintFile = FingerprintManager.getFingerprint(new File("out/" + filename + ".fingerprint").getAbsolutePath());
                System.out.println("Fingerprint size == " + fingerprintFile.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

		/*
        // fingerprint bytes checking
		for (int i=0; i<fingerprint.length; i++){
			System.out.println(fingerprint[i]+" vs "+loadedFp[i]);
		}
		*/
        } catch (URISyntaxException urie) {
            System.out.println("The test file could not be loaded.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (UnsupportedAudioFileException uafe) {
            uafe.printStackTrace();
        }
    }
}