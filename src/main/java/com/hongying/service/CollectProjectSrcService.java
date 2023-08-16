package com.hongying.service;

import com.hongying.pojo.ModuleNodeTree;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectProjectSrcService {

    /**
     * 根据源目录获取目录内所有项目src文件
     * @param sourceFile 源目录
     * @param srcFileName 当前代码模板下src目录名
     * @param configurationFileName 当前代码模板下配置文件全名
     * @param srcWithProject src与项目目录层级关系(0代表同级,+1代表配置文件为src子一级,-1代表配置文件为src父一级)
     * @param configurationWithSrc 配置文件与项目目录层级关系(0代表同级,+1代表配置文件为src子一级,-1代表配置文件为src父一级)
     * @return
     */
    public List<ModuleNodeTree> collectSrcPath(File sourceFile, String srcFileName, String configurationFileName, int srcWithProject, int configurationWithSrc) {
        List<ModuleNodeTree> srcFileNodeTrees = new ArrayList<>();
        recursionSearchSrc(srcFileNodeTrees, sourceFile, srcFileName, configurationFileName, srcWithProject, configurationWithSrc);
        return srcFileNodeTrees;
    }

    private void recursionSearchSrc(List<ModuleNodeTree> srcFileNodeTrees, File currentFile, String srcFileName, String configurationFileName, int srcWithProject, int configurationWithSrc) {
        ModuleNodeTree fileNodeTree = judgeFileIsProjectByConfigurationFile(currentFile, srcFileName, configurationFileName, srcWithProject, configurationWithSrc);
        if (fileNodeTree != null) {
            srcFileNodeTrees.add(fileNodeTree);
            return;
        }
        if (!currentFile.exists()) {
            return;
        }
        if (!currentFile.isDirectory()) {
            return;
        }
        File[] childFiles = currentFile.listFiles();
        for (File childFile : childFiles) {
            recursionSearchSrc(srcFileNodeTrees, childFile, srcFileName, configurationFileName, srcWithProject, configurationWithSrc);
        }
    }

    /**
     * 判断一个目录是否为项目,是的话返回改目录的封装
     * @param file 当前检查是否为项目home的目录
     * @param srcFileName 当前代码模板下src目录名
     * @param configurationFileName 当前代码模板下配置文件全名
     * @param srcWithProject src与项目目录层级关系(0代表同级,+1代表配置文件为src子一级,-1代表配置文件为src父一级)
     * @param configurationWithProject 配置文件与项目目录层级关系(0代表同级,+1代表配置文件为src子一级,-1代表配置文件为src父一级)
     * @return
     */
    private ModuleNodeTree judgeFileIsProjectByConfigurationFile(File file, String srcFileName, String configurationFileName, int srcWithProject, int configurationWithProject) {
        if (!file.exists()) {
            return null;
        }
        if (!file.isDirectory()) {
            return null;
        }
        File[] childFiles = file.listFiles();
        boolean findConfigurationFile = configurationFileName == null;
        boolean findSrc = false;
        ModuleNodeTree projectHome = null;
        ModuleNodeTree src = null;
        ModuleNodeTree configurationFile = null;


        for (File current : childFiles) {
            if (current.isDirectory() && srcFileName.equals(current.getName())) {
                src = new ModuleNodeTree();
                findSrc = true;
                src.setDir(true);
                src.setCurrentFile(current);
            }
            if (configurationFileName != null && (!current.isDirectory() && configurationFileName.equals(current.getName()))) {
                findConfigurationFile = true;
            }
            if (findSrc && findConfigurationFile) {
                break;
            }
        }

        if (findSrc && findConfigurationFile) {
            projectHome = new ModuleNodeTree();
        }
        return projectHome;
    }

    /**
     * 根据坐标文件寻找目标文件
     * @param currentFile 当前传入的坐标文件
     * @param targetFileName 目标文件名
     * @param isDir 目标文件是否为目录
     * @param relativePosition
     * @return 目标文件与坐标文件的相对位置(0代表同级,+1代表目标文件为坐标文件子一级,-1代表目标文件为坐标文件父一级)
     */
    public File findFileFromCurrentFile(File currentFile, String targetFileName, boolean isDir, int relativePosition) {
        File result = null;
        if (relativePosition <= 0) {
            result = findParentFileFromCurrentFile(currentFile, targetFileName, isDir, Math.abs(relativePosition));
        }else {
            result = findChildFileFromCurrentFile(currentFile, targetFileName, isDir, relativePosition);
        }
        return result;
    }
    /**
     * 寻找子目录中的目标文件(不能找同级/relativePosition传0,找同级用findParentFileFromCurrentFile)
     * @param currentFile 当前传入的坐标文件
     * @param targetFileName 目标文件名
     * @param isDir 目标文件是否为目录
     * @param relativePosition 目标文件与坐标文件的相对位置(1代表目标文件为坐标文件的下一级目录)
     * @return
     */
    private File findChildFileFromCurrentFile(File currentFile, String targetFileName, boolean isDir, int relativePosition) {
        if (relativePosition == 0 && currentFile.exists() && currentFile.getName().equals(targetFileName) && isDir == currentFile.isDirectory()) {
            return currentFile;
        }
        if (!currentFile.exists() || !currentFile.isDirectory()) {
            return null;
        }
        File[] childFiles = currentFile.listFiles();

        for (File childFile : childFiles) {
            File childFind = findChildFileFromCurrentFile(childFile, targetFileName, isDir, relativePosition - 1);
            if (childFind != null) {
                return childFind;
            }
        }

        return null;
    }

    /**
     * 寻找父目录中的目标文件
     * @param currentFile
     * @param targetFileName
     * @param isDir
     * @param relativePosition
     * @return
     */
    private File findParentFileFromCurrentFile(File currentFile, String targetFileName, boolean isDir, int relativePosition) {
        File targetLevelDir = currentFile;
        while (relativePosition != -1) {
            targetLevelDir = targetLevelDir.getParentFile();
            if (!targetLevelDir.exists()) {
                return null;
            }
            relativePosition--;
        }

        if (!targetLevelDir.exists() && !targetLevelDir.isDirectory()) {
            return null;
        }
        File[] files = targetLevelDir.listFiles();
        for (File file : files) {
            if (file.getName().equals(targetFileName) && file.isDirectory() == isDir) {
                return file;
            }
        }

        return null;
    }
}
