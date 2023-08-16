package com.hongying.utils;

import com.hongying.pojo.ProjectFile;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Data
public class FileUtil {
    private ProjectFile projectFile;
    @Value("${hy.file.source.path}")
    String initReadPath;
    @Value("${hy.file.target.path}")
    String initWritePath;

    public void setSourcePath(String sourcePath) {
        projectFile.setSourceFilePath(sourcePath);
    }

    public String getInitReadPath() {
        return initReadPath;
    }

    public String getInitWritePath() {
        return initWritePath;
    }

    public void setTargetPath(String targetPath) {
        projectFile.setTargetFilePath(targetPath);
    }
    public boolean fileIsLegitimate() {
        return projectFile.isLegitimate();
    }

    @PostConstruct
    public void init() {
        projectFile = new ProjectFile();
        projectFile.setSourceFilePath(initReadPath);
        projectFile.setTargetFilePath(initWritePath);
    }
}
