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

import com.musicg.graphic.GraphicRender;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class RenderWaveformDemo {
    public static void main(String[] args) {

        String filename = "audio_work/cock_a_1.wav";
        String outFolder = "out";

        // create a wave object
        try {
            File location = new File(Thread.currentThread().getContextClassLoader().getResource(filename).toURI());
            Wave wave = WaveFactory.createWave(location);

            // Graphic render
            GraphicRender render = new GraphicRender();
            //render.setHorizontalMarker(1);
            //render.setVerticalMarker(1);
            render.renderWaveform(wave, outFolder + "/waveform.jpg");

            // change the amplitude representation
            float timeStep = 0.1F;
            render.renderWaveform(wave, timeStep, outFolder + "/waveform2.jpg");
        } catch (URISyntaxException urie) {
            urie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}