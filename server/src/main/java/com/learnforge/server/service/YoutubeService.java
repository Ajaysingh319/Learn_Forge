package com.learnforge.server.service;

import com.learnforge.server.dto.YoutubeVideoResponse;
import java.util.List;

public interface YoutubeService {
    List<YoutubeVideoResponse> searchVideos(String query);
}
