package org.joker.service;

import org.joker.pojo.ModuleFile;
import org.joker.pojo.ProjectFile;
import org.joker.pojo.TargetFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class PrintProjectInfoService {

    @Autowired
    private TargetFileInfo targetFileInfo;

    /**
     * 生成项目的描述文件
     * @param projectFile
     */
    public void createProjectDescriptionFile(ProjectFile projectFile) {
        createProjectDescriptionFile(projectFile.getTargetFile(), projectFile.getSourceFile().getName(), projectFile);
    }
    public void createProjectDescriptionFile(File targetDir, ProjectFile projectFile) {
        createProjectDescriptionFile(targetDir, projectFile.getSourceFile().getName(), projectFile);
    }
    public void createProjectDescriptionFile(File targetDir, String fileName, ProjectFile projectFile) {
        File descriptionFile = generateTargetFile(targetDir, fileName);
        File projectHomeFile = projectFile.getSourceFile();
        appendText(descriptionFile, targetFileInfo.getTemplate1(projectHomeFile.getName()) + "\r\n");

        List<ModuleFile> modules = projectFile.getModules();
        StringBuilder moduleGeneralView = new StringBuilder("项目模块：\r\n");
        for (ModuleFile moduleFile : modules) {
            moduleGeneralView.append(targetFileInfo.getTemplate3(moduleFile.getModuleType() + "项目 -> " + moduleFile.getModuleHome().getCurrentFile().getName()) + "\r\n");
        }
        appendText(descriptionFile, moduleGeneralView.toString() + "\r\n");

        printReadMe(descriptionFile, findReadMe(projectHomeFile));
        appendText(descriptionFile, "\r\n\r\n");


    }

    /**
     * 生成描述文件
     * @param targetDir
     * @param fileName
     */
    private File generateTargetFile(File targetDir, String fileName) {
        File targetFile = new File(targetDir.getAbsolutePath(), fileName + ".md");
        generateTargetFile(targetFile);
        return targetFile;
    }
    private File generateTargetFile(File targetFile) {
        FileWriter fileWriter = null;
        try {
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }else if (!targetFile.isDirectory()){
                fileWriter = new FileWriter(targetFile);
                fileWriter.write("");
                fileWriter.flush();
            }else {
                targetFile.delete();
                targetFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return targetFile;
    }

    public void printModuleInfoToTextFile(File targetFile, ModuleFile module) {
        appendText(targetFile, module.getModuleHome().getCurrentFile().getName());
        printReadMe(targetFile, module.getModuleHome().getCurrentFile());
    }


    /**
     * 追加一个指定文本字符串
     * @param targetFile
     * @param text
     */
    private void appendText(File targetFile, String text) {
        try (FileOutputStream fos = new FileOutputStream(targetFile, true)) {
            fos.write(text.getBytes());
            fos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入模块中的readme
     * @param targetFile
     * @param readme
     */
    private void printReadMe(File targetFile, File readme) {
        if (readme == null || !readme.exists()) {
            return;
        }
        appendText(targetFile, targetFileInfo.getTemplate2("README") + "\r\n");
        try (FileInputStream fis = new FileInputStream(readme);
             FileOutputStream fos = new FileOutputStream(targetFile, true)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.write('\r');
            fos.write('\n');
            fos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 找寻模块中的readme文件
     * @param moduleDir
     * @return
     */
    private File findReadMe(File moduleDir) {
        File[] files = moduleDir.listFiles();
        for (File file : files) {
            if ("README.md".equals(file.getName())) {
                return file;
            }
        }
        return null;
    }
}
