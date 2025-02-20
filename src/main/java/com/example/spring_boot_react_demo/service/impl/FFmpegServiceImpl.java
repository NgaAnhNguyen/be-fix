package com.example.spring_boot_react_demo.service.impl;

import com.example.spring_boot_react_demo.service.CloudinaryService;
import com.example.spring_boot_react_demo.service.FFmpegService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FFmpegServiceImpl implements FFmpegService {
    private final CloudinaryService cloudinaryService;
    @Override
    public String extractAudio(String videoPath) {
        String audioPath = videoPath.replace(".mp4", ".mp3");
        String command = "ffmpeg -i " + videoPath + " -vn -acodec mp3 " + audioPath;

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            return "Audio extraction completed, output saved to: " + audioPath;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error during audio extraction";
        }
    }

    @Override
    public String cutAudio(MultipartFile file, String startTime, String endTime) {
        try{
            File tempFile = File.createTempFile("temp_audio_",".mp3");
            file.transferTo(tempFile);

            String outputFilePath = "output_" + tempFile.getName();
            String ffmpegCommand = String.format(
                    "ffmpeg -i %s -ss %s -to %s -c copy %s",
                    tempFile.getAbsolutePath(), startTime, endTime, outputFilePath
            );


            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
            processBuilder.inheritIO();  // Hiển thị output ra console
            Process process = processBuilder.start();
            process.waitFor();

            return "Audio file cut successfully. Output file: " + outputFilePath;

        }catch (IOException | InterruptedException e)  {
            e.printStackTrace();
            return "Error while cutting audio.";
        }

    }
    @Override
    public String mergeAudio(MultipartFile file1, MultipartFile file2) {
        try {
            // Lưu các file âm thanh tạm thời
            File tempFile1 = File.createTempFile("temp_audio_1_", ".mp3");
            File tempFile2 = File.createTempFile("temp_audio_2_", ".mp3");
            file1.transferTo(tempFile1);
            file2.transferTo(tempFile2);

            // Tạo file danh sách chứa các file cần ghép
            File listFile = File.createTempFile("fileList", ".txt");
            String listContent = "file '" + tempFile1.getAbsolutePath() + "'\n" +
                    "file '" + tempFile2.getAbsolutePath() + "'\n";
            java.nio.file.Files.write(listFile.toPath(), listContent.getBytes());

            // Lệnh FFmpeg để ghép 2 file âm thanh
            String outputFilePath = "output_merged.mp3";
            String ffmpegCommand = String.format(
                    "ffmpeg -f concat -safe 0 -i %s -c copy %s",
                    listFile.getAbsolutePath(), outputFilePath
            );

            // Thực thi lệnh FFmpeg
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
            processBuilder.inheritIO();  // Hiển thị output ra console
            Process process = processBuilder.start();
            process.waitFor();

                // Trả về đường dẫn tới file đầu ra
            return "Audio files merged successfully. Output file: " + outputFilePath;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while merging audio.";
        }
    }

    @Override
    public String cutMedia(MultipartFile file, String startTime, String endTime,String fileExtension) {
        try {
            File tempFile = File.createTempFile("temp_media_", fileExtension);
            file.transferTo(tempFile);

            String outputFilePath = "output_" + tempFile.getName();

            String ffmpegCommand = String.format(
                    "ffmpeg -i %s -ss %s -to %s -c copy %s",
                    tempFile.getAbsolutePath(), startTime, endTime, outputFilePath
            );

            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();

            return "Media file cut successfully. Output file: " + outputFilePath;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while cutting media.";
        }
    }

    @Override
    public String mergeMedia(List<MultipartFile> files, String fileExtension) {
        try {
            if (files.isEmpty()) {
                return "No files provided for merging.";
            }
            if (!fileExtension.equals(".mp3") && !fileExtension.equals(".mp4")) {
                return "Unsupported file format. Only .mp3 and .mp4 are allowed.";
            }
            String resourceType = fileExtension.equals(".mp3") ? "audio" : "video";


            File listFile = File.createTempFile("fileList", ".txt");
            StringBuilder listContent = new StringBuilder();
            List<File> tempFiles = new ArrayList<>();

            for (MultipartFile file : files) {
                File tempFile = File.createTempFile("temp_media_", fileExtension);
                file.transferTo(tempFile);
                tempFiles.add(tempFile);
                listContent.append("file '").append(tempFile.getAbsolutePath()).append("'\n");
            }

            Files.write(listFile.toPath(), listContent.toString().getBytes());

            File outputFile = File.createTempFile("merged_", fileExtension);

            String ffmpegCommand = String.format(
                    "ffmpeg -y -f concat -safe 0 -i %s -c copy %s",
                    listFile.getAbsolutePath(), outputFile.getAbsolutePath()
            );

            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            MultipartFile multipartOutputFile = new MockMultipartFile(
                    outputFile.getName(),
                    outputFile.getName(),
                    "application/octet-stream",
                    Files.readAllBytes(outputFile.toPath())
            );

            String cloudinaryUrl = cloudinaryService.uploadFile(multipartOutputFile, "folder_1",resourceType);
            listFile.delete();
            for (File tempFile : tempFiles) {
                tempFile.delete();
            }
            outputFile.delete();
            return cloudinaryUrl != null ? cloudinaryUrl : "Error uploading merged file to Cloudinary.";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while merging media.";
        }
    }

}
