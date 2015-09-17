package com.musicg.graphic;

import com.musicg.spectrogram.Spectrogram;
import com.musicg.wave.Wave;
import com.musicg.wave.extension.SampleAmplitudes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class GraphicRender {

    public static final float WAVEFORM_DEFAULT_TIMESTEP = 0.1F;
    private int xMarker = -1;
    private int yMarker = -1;

    public GraphicRender() {
    }

    /**
     * Render a waveform of a wave file
     *
     * @param wave     Wave object
     * @param filename output file
     */
    public void renderWaveform(Wave wave, String filename) throws IOException {
        renderWaveform(wave, WAVEFORM_DEFAULT_TIMESTEP, filename);
    }

    /**
     * Render a waveform of a wave file
     *
     * @param wave     Wave object
     * @param timeStep time interval in second, as known as 1/fps
     * @param filename output file
     */
    public void renderWaveform(Wave wave, float timeStep, String filename) throws IOException {

        // for signed signals, the middle is 0 (-1 ~ 1)
        double middleLine = 0;

        // usually 8bit is unsigned
        if (wave.getWaveHeader().getSampleSize() == 8) {
            // for unsigned signals, the middle is 0.5 (0~1)
            middleLine = 0.5;
        }

        DataInputStream nAmplitudes = SampleAmplitudes.getNormalizedAmplitudes(wave, -1);

        // TODO for now render a simple image.
        int width = 500; //(int) (nAmplitudes.length / wave.getWaveHeader().getSampleRate() / timeStep);

        int height = 500;
        int middle = height / 2;
        int magnifier = 1000;

        //int numSamples = nAmplitudes.length;

        if (width > 0) {
            int numSamplePerTimeFrame = (int) wave.getWaveHeader().getSampleRate();

            ByteArrayOutputStream posAmplitudes = new ByteArrayOutputStream();
            ByteArrayOutputStream negAmplitudes = new ByteArrayOutputStream();
            DataOutputStream scaledPosAmplitudes = new DataOutputStream(posAmplitudes);
            DataOutputStream scaledNegAmplitudes = new DataOutputStream(negAmplitudes);

            // width scaling
            try {
                while (true) {
                    double sumPosAmplitude = 0;
                    double sumNegAmplitude = 0;
                    //int startSample = i * numSamplePerTimeFrame

                    for (int j = 0; j < numSamplePerTimeFrame; j++) {
                        double a = nAmplitudes.readDouble();
                        if (a > middleLine) {
                            sumPosAmplitude += (a - middleLine);
                        } else {
                            sumNegAmplitude += (a - middleLine);
                        }
                    }

                    int scaledPosAmplitude = (int) (sumPosAmplitude
                            / numSamplePerTimeFrame * magnifier + middle);
                    int scaledNegAmplitude = (int) (sumNegAmplitude
                            / numSamplePerTimeFrame * magnifier + middle);

                    scaledPosAmplitudes.writeInt(scaledPosAmplitude);
                    scaledNegAmplitudes.writeInt(scaledNegAmplitude);
                }
            } catch (EOFException eofe) {
                // ignore - end of stream.
            }

            // render wave form image
            BufferedImage bufferedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);

            // set default white background
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setPaint(new Color(255, 255, 255));
            graphics.fillRect(0, 0, bufferedImage.getWidth(),
                    bufferedImage.getHeight());
            // end set default white background

            DataInputStream scaledNegInAmplitudes = new DataInputStream(new ByteArrayInputStream(negAmplitudes.toByteArray()));
            DataInputStream scaledPosInAmplitudes = new DataInputStream(new ByteArrayInputStream(posAmplitudes.toByteArray()));

            while (true) {
                int counter = 0;
                for (int j = scaledNegInAmplitudes.readInt(); j < scaledPosInAmplitudes.readInt(); j++) {
                    int y = height - j;    // j from -ve to +ve, i.e. draw from top to bottom
                    if (y < 0) {
                        y = 0;
                    } else if (y >= height) {
                        y = height - 1;
                    }
                    bufferedImage.setRGB(counter++, y, 0);
                }
            }
            // end render wave form image
        } else {
            System.err.println("renderWaveform error: Empty Wave");
        }
    }

    /**
     * Render a spectrogram of a wave file
     *
     * @param spectrogram spectrogram object
     * @param filename    output file
     */
    public void renderSpectrogram(Spectrogram spectrogram, String filename) {
        renderSpectrogramData(spectrogram.getNormalizedSpectrogramData(), filename);
    }

    /**
     * Render a spectrogram data array
     *
     * @param spectrogramData spectrogramData[time][frequency]=intensity, which time is the x-axis, frequency is the y-axis, intensity is the color darkness
     * @param filename        output file
     */
    public void renderSpectrogramData(double[][] spectrogramData, String filename) {

        if (spectrogramData != null) {
            int width = spectrogramData.length;
            int height = spectrogramData[0].length;

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < width; i++) {
                if (i == xMarker) {
                    for (int j = 0; j < height; j++) {
                        bufferedImage.setRGB(i, j, 0xFF00);    // green
                    }
                } else {
                    for (int j = 0; j < height; j++) {
                        int value;
                        if (j == yMarker) {
                            value = 0xFF0000;    // red
                        } else {
                            value = 255 - (int) (spectrogramData[i][j] * 255);
                        }
                        bufferedImage.setRGB(i, height - 1 - j, value << 16 | value << 8 | value);
                    }
                }
            }

            try {
                int dotPos = filename.lastIndexOf(".");
                String extension = filename.substring(dotPos + 1);
                ImageIO.write(bufferedImage, extension, new File(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("renderSpectrogramData error: Empty Wave");
        }
    }

    /**
     * Set the vertical marker
     *
     * @param x x-offset pixel of the marker
     */
    public void setVerticalMarker(int x) {
        this.xMarker = x;
    }

    /**
     * Set the horizontal marker
     *
     * @param y y-offset pixel of the marker
     */
    public void setHorizontalMarker(int y) {
        this.yMarker = y;
    }

    /**
     * Reset the markers
     */
    public void resetMarkers() {
        xMarker = -1;
        yMarker = -1;
    }
}