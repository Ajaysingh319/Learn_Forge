package com.learnforge.server.dto;

public class TtsResponse {
    private String mimeType;
    private String base64Audio;
    private String voiceName;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getBase64Audio() {
        return base64Audio;
    }

    public void setBase64Audio(String base64Audio) {
        this.base64Audio = base64Audio;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }
}
