package com.learnforge.server.service;

import com.learnforge.server.dto.TranslateResponse;
import com.learnforge.server.dto.TtsResponse;

public interface MultilingualService {
    TranslateResponse translateToHinglish(String text);

    TtsResponse generateSpeech(String text, String voiceName);
}
