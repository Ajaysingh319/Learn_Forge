package com.learnforge.server.service;

import com.learnforge.server.config.AiProperties;
import com.learnforge.server.config.GeminiProperties;
import com.learnforge.server.dto.TranslateResponse;
import com.learnforge.server.dto.TtsResponse;
import com.learnforge.server.util.WavAudioUtil;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class MultilingualServiceImpl implements MultilingualService {

    private final AiProperties aiProperties;
    private final GeminiProperties geminiProperties;
    private final GeminiClientService geminiClientService;

    public MultilingualServiceImpl(AiProperties aiProperties,
                                   GeminiProperties geminiProperties,
                                   GeminiClientService geminiClientService) {
        this.aiProperties = aiProperties;
        this.geminiProperties = geminiProperties;
        this.geminiClientService = geminiClientService;
    }

    @Override
    public TranslateResponse translateToHinglish(String text) {
        String provider = aiProperties.getProvider() == null ? "template" : aiProperties.getProvider().trim().toLowerCase();
        String translated;
        if ("gemini".equals(provider)) {
            translated = geminiClientService.generateText(buildHinglishPrompt(text));
        } else {
            translated = templateHinglish(text);
        }

        TranslateResponse response = new TranslateResponse();
        response.setLanguage("hinglish");
        response.setText(translated);
        return response;
    }

    @Override
    public TtsResponse generateSpeech(String text, String voiceName) {
        String provider = aiProperties.getProvider() == null ? "template" : aiProperties.getProvider().trim().toLowerCase();
        String selectedVoice = voiceName == null || voiceName.trim().isEmpty()
                ? geminiProperties.getTtsVoiceName()
                : voiceName.trim();

        byte[] wav;
        if ("gemini".equals(provider)) {
            GeminiClientService.AudioData audio = geminiClientService.generateSpeech(text, selectedVoice);
            wav = WavAudioUtil.pcmToWav(audio.getPcm(), audio.getSampleRate());
        } else {
            // Rule-based/offline fallback: a short synthetic narration tone.
            wav = WavAudioUtil.generateNarrationTone(text);
        }

        TtsResponse response = new TtsResponse();
        response.setMimeType("audio/wav");
        response.setVoiceName(selectedVoice);
        response.setBase64Audio(Base64.getEncoder().encodeToString(wav));
        return response;
    }

    private String buildHinglishPrompt(String text) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Translate the following lesson explanation into clear Hinglish.\n");
        prompt.append("Use simple Hindi words written in Roman script, keep technical terms in English, ");
        prompt.append("and make it student-friendly. Return only the translated explanation.\n\n");
        prompt.append(text);
        return prompt.toString();
    }

    private String templateHinglish(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            return "Is lesson ka content abhi available nahi hai.";
        }
        return "Hinglish explanation: Is lesson mein hum simple tareeke se samjhenge - "
                + trimmed
                + "\n\nKey idea: Concept ko step-by-step padho, examples dekho, aur quiz se apni understanding check karo.";
    }
}
