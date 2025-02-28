package com.example.spring_boot_react_demo.service.impl;

import com.example.spring_boot_react_demo.model.entity.Video;
import com.example.spring_boot_react_demo.repository.VideoRepo;
import com.example.spring_boot_react_demo.service.CloudinaryService;
import com.example.spring_boot_react_demo.service.FFmpegService;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FFmpegServiceImpl implements FFmpegService {
    private final CloudinaryService cloudinaryService;
    private final VideoRepo videoRepo;

    @Override
    public String extractAudio(String inputVideoPath, String outputVideoPath) {
        String command = "ffmpeg -i \"" + inputVideoPath + "\" \"" + outputVideoPath + "\"";

        try {
            // In ra câu lệnh FFmpeg để debug
            System.out.println("FFmpeg command: " + command);

            // Khởi tạo tiến trình và thực thi câu lệnh
            Process process = Runtime.getRuntime().exec(command);

            // Đọc output và error từ FFmpeg
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // Đọc luồng output (thông tin từ FFmpeg)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Đọc luồng error (dữ liệu lỗi từ FFmpeg)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }

            // Chờ tiến trình hoàn thành
            process.waitFor();

            // In kết quả của quá trình
            System.out.println("FFmpeg Output: " + output.toString());
            System.out.println("FFmpeg Error: " + error.toString());

            // Kiểm tra kết quả quá trình chuyển đổi
            if (process.exitValue() == 0) {
                return "Chuyển đổi video thành công! Video đã được lưu tại: " + outputVideoPath;
            } else {
                return "Chuyển đổi video thất bại.";
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Lỗi khi chuyển đổi video: " + e.getMessage();
        }


}

    public String extractAudioToVideo(String inputVideoPath, String outputVideoPath) {
        // Xây dựng câu lệnh ffmpeg để tạo video từ âm thanh và thêm hình nền
        String command = "ffmpeg -f lavfi -t 00:00:10 -i color=c=white:s=1280x720:r=30 -i \""
                + inputVideoPath + "\" -c:v libx264 -c:a aac -strict experimental -shortest -y \""
                + outputVideoPath + "\"";



        try {
            // In ra câu lệnh FFmpeg để debug
            System.out.println("FFmpeg command: " + command);

            // Khởi tạo tiến trình và thực thi câu lệnh
            Process process = Runtime.getRuntime().exec(command);

            // Đọc output và error từ FFmpeg
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // Đọc luồng output (thông tin từ FFmpeg)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Đọc luồng error (dữ liệu lỗi từ FFmpeg)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }

            // Chờ tiến trình hoàn thành
            process.waitFor();

            // In kết quả của quá trình
            System.out.println("FFmpeg Output: " + output.toString());
            System.out.println("FFmpeg Error: " + error.toString());

            // Kiểm tra kết quả quá trình
            if (process.exitValue() == 0) {
                return "Chuyển đổi video thành công! Video đã được lưu tại: " + outputVideoPath;
            } else {
                return "Chuyển đổi video thất bại.";
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Lỗi khi chuyển đổi video: " + e.getMessage();
        }
    }





    @Override
    public String cutAudio(MultipartFile file, String startTime, String endTime) {
        try {
            File tempFile = File.createTempFile("temp_audio_", ".mp3");
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

        } catch (IOException | InterruptedException e) {
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
    public String cutMedia(MultipartFile file, String startTime, String endTime, String fileExtension) {
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

            String cloudinaryUrl = cloudinaryService.uploadFile(multipartOutputFile, "folder_1", resourceType);
            listFile.delete();
            for (File tempFile : tempFiles) {
                tempFile.delete();
            }
            outputFile.delete();
            Video newVideo = new Video();
            newVideo.setUrl(cloudinaryUrl);
            videoRepo.save(newVideo);
            return cloudinaryUrl != null ? cloudinaryUrl : "Error uploading merged file to Cloudinary.";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while merging media.";
        }
    }
    public String mergeAudioVideo(String videoFilePath, String audioFilePath, String outputFilePath) {
        try {
            // Lệnh FFmpeg cần chạy:
            // ffmpeg -i "ShopHomepage.mp4" -i "ROSÉ - APT..mp3" -c:v copy -c:a aac -map 0:v:0 -map 1:a:0 output-test.mp4

            //ffmpeg -i "C:\Users\NITRO 5\Downloads\ShopHomepage.mp4" -i "C:\Users\NITRO 5\Downloads\ROSÉ - APT..mp3"
            // -c:v copy -c:a aac -map 0:v:0 -map 1:a:0 "C:\Users\NITRO 5\Downloads\output-tes.mp4"
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoFilePath,
                    "-i", audioFilePath,
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-map", "0:v:0",
                    "-map", "1:a:0",
                    outputFilePath
            );

            // Kết hợp đầu ra và lỗi để đọc log dễ dàng
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder processOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    processOutput.append(line).append("\n");
                }
            }


            return "Merge successful, output file: " + outputFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "Merge failed: " + e.getMessage();
        }
    }



    public String mergeSubtitlesToVideo(MultipartFile videoFile, MultipartFile subtitleFile) {
        try {
            // Tạo file tạm cho video và phụ đề
            File tempVideoFile = File.createTempFile("temp_video_", ".mp4");
            File tempSubtitleFile = File.createTempFile("temp_subtitle_", ".srt");

            videoFile.transferTo(tempVideoFile);
            subtitleFile.transferTo(tempSubtitleFile);

            // Định nghĩa đường dẫn đầu ra
            String outputFilePath = "D:/output_burned.mp4";

            // Chuyển đường dẫn file phụ đề sang định dạng phù hợp với FFmpeg
            // Lấy đường dẫn gốc
            String subtitlePath = tempSubtitleFile.getAbsolutePath();

// Chuyển đổi cho FFmpeg (thoát ký tự)
            String escapedSubtitlePath = subtitlePath.replace("\\", "\\\\").replace(":", "\\:");

            // Tạo lệnh chạy FFmpeg
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-y",
                    "-i", tempVideoFile.getAbsolutePath(),
                    "-vf", "subtitles='" + escapedSubtitlePath + "'",
                    "-c:v", "libx264", "-crf", "23", "-preset", "fast",
                    "-c:a", "copy", outputFilePath
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Ghi lại log FFmpeg
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "✅ Subtitles added successfully. Output file: " + outputFilePath;
            } else {
                return "❌ Error while adding subtitles. FFmpeg output:\n" + output.toString();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "❌ Error during processing: " + e.getMessage();
        }
    }

}