package com.example.ffmpeg;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FFmpegProperties {

    @Value("${FFmpeg.file-temp-dir}")
    private String fileTempDir;

    @Value("${FFmpeg.file-m3u8-dir}")
    private String m3u8Dir;

    private static String ffmpegWinPath;

    private static String ffmpegLinuxPath;

    private static String ffprobeWinPath;

    private static String ffprobeLinuxPath;

    @Value("${FFmpeg.ffmpeg-linux-path}")
    public void setFfmpegLinuxPath(String ffmpegLinuxPath) {
        FFmpegProperties.ffmpegLinuxPath = ffmpegLinuxPath;
    }

    @Value("${FFmpeg.ffmpeg-win-path}")
    public void setFfmpegWinPath(String ffmpegWinPath) {
        FFmpegProperties.ffmpegWinPath = ffmpegWinPath;
    }

    @Value("${FFmpeg.ffprobe-win-path}")
    public void setFfprobeWinPath(String ffprobeWinPath) {
        FFmpegProperties.ffprobeWinPath = ffprobeWinPath;
    }

    @Value("${FFmpeg.ffprobe-linux-path}")
    public void setFfprobeLinuxPath(String ffprobeLinuxPath) {
        FFmpegProperties.ffprobeLinuxPath = ffprobeLinuxPath;
    }

    public static String getFfmpegWinPath() {
        return ffmpegWinPath;
    }

    public static String getFfmpegLinuxPath() {
        return ffmpegLinuxPath;
    }

    public static String getFfprobeWinPath() {
        return ffprobeWinPath;
    }

    public static String getFfprobeLinuxPath() {
        return ffprobeLinuxPath;
    }
}
