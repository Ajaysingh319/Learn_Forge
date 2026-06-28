package com.learnforge.server.controller;

import com.learnforge.server.dto.YoutubeVideoResponse;
import com.learnforge.server.service.YoutubeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-base-path:/api}/youtube")
public class YoutubeController {

    private final YoutubeService youtubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping
    public List<YoutubeVideoResponse> searchVideos(@RequestParam("query") String query) {
        return youtubeService.searchVideos(query);
    }
}
