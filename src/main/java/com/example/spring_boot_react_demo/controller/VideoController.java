package com.example.spring_boot_react_demo.controller;

import com.example.spring_boot_react_demo.service.FFmpegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/video")
public class VideoController {
    private final FFmpegService videoService;

    @Autowired
    public VideoController(FFmpegService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/convert")
    public ResponseEntity<String> convertVideo(@RequestParam String inputVideoPath, @RequestParam String outputVideoPath) {
        try {
            String result = videoService.extractAudio(inputVideoPath, outputVideoPath); // Gọi service để chuyển đổi video
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi chuyển đổi video: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/convertA")
    public String convertVideoToAudioVideo(@RequestParam String inputVideoPath,
                                           @RequestParam String outputVideoPath
                                           ) {

        return videoService.extractAudioToVideo(inputVideoPath, outputVideoPath);
    }
    @GetMapping("/merge")
    public String merge(@RequestParam("video") String video,
                        @RequestParam("video") String audio,
                        @RequestParam("output") String output) {
        return videoService.mergeAudioVideo(video, audio, output);
    }
    @GetMapping("/merge_lyr")
    public String mergelry(@RequestParam("video") MultipartFile video,
                        @RequestParam("lyric") MultipartFile lyric)
                        {
        return videoService.mergeSubtitlesToVideo(video, lyric);
    }
    @PostMapping("/getSrt")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "❌ Không nhận được file SRT!";
        }

        try {
            // Lưu file vào thư mục tạm thời
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File savedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(savedFile);

            System.out.println("✅ Đã nhận được file: " + savedFile.getAbsolutePath());

            return "✅ Spring Boot đã nhận file: " + file.getOriginalFilename();
        } catch (IOException e) {
            return "❌ Lỗi khi lưu file: " + e.getMessage();
        }
    }
}
