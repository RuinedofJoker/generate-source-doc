package org.joker.service;

import org.joker.pojo.ModuleFile;
import org.joker.pojo.ModuleNodeTree;
import org.joker.pojo.ProjectFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectProjectSrcService {

    /**
     * 根据源目录获取目录内所有项目src文件
     * @param projectFile
     * @param srcWithModule
     * @param configurationWithModule 配置文件名与模块根目录的相对位置(0代表同级,+1代表目标文件为坐标文件子一级)
     * @return
     */
    public List<ModuleFile> collectModules(ProjectFile projectFile, Map<String, Integer> srcWithModule, Map<String, Integer> configurationWithModule, boolean allConf) {
        List<ModuleFile> moduleFilesResult = new ArrayList<>();
        recursionSearchModules(moduleFilesResult, projectFile.getSourceFile(), srcWithModule, configurationWithModule, allConf);
        return moduleFilesResult;
    }

    private void recursionSearchModules(List<ModuleFile> moduleFilesResult, File currentFile, Map<String, Integer> srcWithModule, Map<String, Integer> configurationWithModule, boolean allConf) {
        ModuleFile fileNodeTree = judgeFileIsModule(currentFile, srcWithModule, configurationWithModule, allConf);
        if (fileNodeTree != null) {
            moduleFilesResult.add(fileNodeTree);
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
            recursionSearchModules(moduleFilesResult, childFile, srcWithModule, configurationWithModule, allConf);
        }
    }

    /**
     * 判断一个目录是否为项目模块,是的话返回该目录的封装
     * @param moduleHomeFile
     * @param srcWithModule
     * @param configurationWithModule
     * @return
     */
    private ModuleFile judgeFileIsModule(File moduleHomeFile, Map<String, Integer> srcWithModule, Map<String, Integer> configurationWithModule, boolean allConf) {
        if (!moduleHomeFile.exists()) {
            return null;
        }
        if (!moduleHomeFile.isDirectory()) {
            return null;
        }

        ModuleFile moduleHome = null;
        List<ModuleNodeTree> src = new ArrayList<>();
        List<ModuleNodeTree> configuration = new ArrayList<>();

        for (String srcFileName : srcWithModule.keySet()) {
            File srcFile = findFileFromCurrentFile(moduleHomeFile, srcFileName, true, srcWithModule.get(srcFileName));
            if (srcFile != null) {
                ModuleNodeTree srcModuleNode = new ModuleNodeTree();
                srcModuleNode.setCurrentFile(srcFile);
                src.add(srcModuleNode);
            }
        }
        for (String configurationFileName : configurationWithModule.keySet()) {
            File configurationFile = findFileFromCurrentFile(moduleHomeFile, configurationFileName, false, configurationWithModule.get(configurationFileName));
            if (configurationFile != null) {
                ModuleNodeTree configurationModuleNode = new ModuleNodeTree();
                configurationModuleNode.setCurrentFile(configurationFile);
                configuration.add(configurationModuleNode);
            }
        }

        if (src.size() == srcWithModule.size() && ((!allConf) || configuration.size() == configurationWithModule.size())) {
            moduleHome = new ModuleFile();
            ModuleNodeTree moduleHomeNode = new ModuleNodeTree();
            moduleHomeNode.setCurrentFile(moduleHomeFile);
            moduleHome.setModuleHome(moduleHomeNode);
            moduleHome.setSrcDir(src);
            moduleHome.setConfigurationFile(configuration);
        }
        return moduleHome;
    }

    /**
     * 根据坐标文件寻找目标文件(返回null表示没找到)
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

    public static void main(String[] args) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setSourceFilePath("C:\\code\\长沙经开区产业链供需集市\\后端\\jkqcyl");
        HashMap<String, Integer> src = new HashMap<>();
        src.put("src", 1);
        HashMap<String, Integer> con = new HashMap<>();
        con.put("pom.xml", 1);
        List<ModuleFile> moduleFiles = new CollectProjectSrcService().collectModules(projectFile, src, con, true);
        projectFile.setModules(moduleFiles);
        System.out.println();
    }
}
