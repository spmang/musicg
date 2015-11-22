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
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class FingerprintRecognitionDemo {

    public static void main(String[] args) {

        String songA = "audio_work/songs/canon_d_major.wav";
        String songB = "audio_work/songs/fing_fing_ha.wav";
        String songC = "audio_work/songs/forrest_gump_theme.wav";
        String songD = "audio_work/songs/imagine.wav";
        String songE = "audio_work/songs/top_of_the_world.wav";

        // create a wave object
        try {
            AudioFormatInputStream waveA = AudioFormatInputStreamFactory.createAudioFormatInputStream(songA);
            AudioFormatInputStream waveB = AudioFormatInputStreamFactory.createAudioFormatInputStream(songB);
            AudioFormatInputStream waveC = AudioFormatInputStreamFactory.createAudioFormatInputStream(songC);
            AudioFormatInputStream waveD = AudioFormatInputStreamFactory.createAudioFormatInputStream(songD);
            AudioFormatInputStream waveE = AudioFormatInputStreamFactory.createAudioFormatInputStream(songE);

            String recordedClip = "audio_work/songs/top_of_the_world_rec.wav";

            FingerprintSimilarity similarity;

            // song A:
            AudioFormatInputStream waveRec = AudioFormatInputStreamFactory.createAudioFormatInputStream(recordedClip);
            similarity = FingerprintManager.getFingerprintSimilarity(waveA, waveRec);
            System.out.println("clip is found at "
                    + similarity.getsetMostSimilarTimePosition() + "s in "
                    + songA + " with similarity " + similarity.getSimilarity());
            waveA.close();
            waveRec.close();

            // song B:
            waveRec = AudioFormatInputStreamFactory.createAudioFormatInputStream(recordedClip);
            similarity = FingerprintManager.getFingerprintSimilarity(waveB, waveRec);
            System.out.println("clip is found at "
                    + similarity.getsetMostSimilarTimePosition() + "s in "
                    + songB + " with similarity " + similarity.getSimilarity());
            waveB.close();
            waveRec.close();

            // song C:
            waveRec = AudioFormatInputStreamFactory.createAudioFormatInputStream(recordedClip);
            similarity = FingerprintManager.getFingerprintSimilarity(waveC, waveRec);
            System.out.println("clip is found at "
                    + similarity.getsetMostSimilarTimePosition() + "s in "
                    + songC + " with similarity " + similarity.getSimilarity());
            waveC.close();
            waveRec.close();

            // song D:
            waveRec = AudioFormatInputStreamFactory.createAudioFormatInputStream(recordedClip);
            similarity = FingerprintManager.getFingerprintSimilarity(waveD, waveRec);
            System.out.println("clip is found at "
                    + similarity.getsetMostSimilarTimePosition() + "s in "
                    + songD + " with similarity " + similarity.getSimilarity());
            waveD.close();
            waveRec.close();

            // song E:
            waveRec = AudioFormatInputStreamFactory.createAudioFormatInputStream(recordedClip);
            similarity = FingerprintManager.getFingerprintSimilarity(waveE, waveRec);
            System.out.println("clip is found at "
                    + similarity.getsetMostSimilarTimePosition() + "s in "
                    + songE + " with similarity " + similarity.getSimilarity());
            waveE.close();
            waveRec.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (UnsupportedAudioFileException uafe) {
            uafe.printStackTrace();
        }
    }
}