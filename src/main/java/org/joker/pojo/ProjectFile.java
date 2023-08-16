package org.joker.pojo;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class ProjectFile {

    private File sourceFile;
    private File targetFile;

    private boolean isLegitimate = false;

    private List<ModuleFile> modules;

    public void setSourceFilePath(String sourceFilePath) {
        sourceFile = new File(sourceFilePath);
        checkIsLegitimate();
    }

    public void setTargetFilePath(String targetFilePath) {
        targetFile = new File(targetFilePath);
        checkIsLegitimate();
    }

    public boolean isLegitimate() {
        checkIsLegitimate();
        return isLegitimate;
    }

    private String checkIsLegitimate() {
        if (sourceFile == null) {
            isLegitimate = false;
            return "未提交源目录";
        }
        if (targetFile == null) {
            isLegitimate = false;
            return "未提交目标文件";
        }
        if ((!sourceFile.exists()) && sourceFile.isDirectory()) {
            isLegitimate = false;
            return "源目录不存在";
        }
        isLegitimate = true;
        return "格式合法";
    }
}
