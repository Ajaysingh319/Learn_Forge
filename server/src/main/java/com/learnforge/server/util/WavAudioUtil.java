package com.learnforge.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class WavAudioUtil {

    private static final int SAMPLE_RATE = 16000;
    private static final short BITS_PER_SAMPLE = 16;
    private static final short CHANNELS = 1;

    private WavAudioUtil() {
    }

    public static byte[] generateNarrationTone(String text) {
        int seconds = Math.max(1, Math.min(4, text == null ? 1 : text.length() / 350 + 1));
        int sampleCount = SAMPLE_RATE * seconds;
        byte[] pcm = new byte[sampleCount * 2];

        for (int i = 0; i < sampleCount; i++) {
            double envelope = Math.sin(Math.PI * i / sampleCount);
            double wave = Math.sin(2.0 * Math.PI * 440.0 * i / SAMPLE_RATE);
            short sample = (short) (wave * envelope * 2500);
            pcm[i * 2] = (byte) (sample & 0xff);
            pcm[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return withWavHeader(pcm);
    }

    private static byte[] withWavHeader(byte[] pcm) {
        int byteRate = SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE / 8;
        int blockAlign = CHANNELS * BITS_PER_SAMPLE / 8;
        int dataSize = pcm.length;
        int chunkSize = 36 + dataSize;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeAscii(out, "RIFF");
            writeInt(out, chunkSize);
            writeAscii(out, "WAVE");
            writeAscii(out, "fmt ");
            writeInt(out, 16);
            writeShort(out, (short) 1);
            writeShort(out, CHANNELS);
            writeInt(out, SAMPLE_RATE);
            writeInt(out, byteRate);
            writeShort(out, (short) blockAlign);
            writeShort(out, BITS_PER_SAMPLE);
            writeAscii(out, "data");
            writeInt(out, dataSize);
            out.write(pcm);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create WAV audio", ex);
        }
    }

    private static void writeAscii(ByteArrayOutputStream out, String value) throws IOException {
        out.write(value.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }

    private static void writeInt(ByteArrayOutputStream out, int value) {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
        out.write((value >> 16) & 0xff);
        out.write((value >> 24) & 0xff);
    }

    private static void writeShort(ByteArrayOutputStream out, short value) {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
    }
}
