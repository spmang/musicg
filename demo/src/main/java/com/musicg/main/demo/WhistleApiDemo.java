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

import com.musicg.streams.AudioFormatInputStream;
import com.musicg.streams.AudioFormatInputStreamFactory;
import com.musicg.wave.WaveTypeDetector;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class WhistleApiDemo {
    public static void main(String[] args) {

        // create a wave object
        try {
            AudioFormatInputStream wave = AudioFormatInputStreamFactory.createAudioFormatInputStream("audio_work/whistle.wav");
            WaveTypeDetector waveTypeDetector = new WaveTypeDetector(wave);
            System.out.println("Is whistle probability: " + waveTypeDetector.getWhistleProbability(1024, -1));
        } catch (IOException ioe) {
            System.out.println("Could not load input file");
            ioe.printStackTrace();
        } catch (UnsupportedAudioFileException uafe) {
            uafe.printStackTrace();
        }
    }
}