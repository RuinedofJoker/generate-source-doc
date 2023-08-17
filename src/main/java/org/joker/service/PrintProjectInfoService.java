package org.joker.service;

import org.joker.pojo.ModuleFile;
import org.joker.pojo.ModuleNodeTree;
import org.joker.pojo.ProjectFile;
import org.joker.pojo.TargetFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PrintProjectInfoService {

    @Autowired
    private TargetFileInfo targetFileInfo;

    @Autowired
    private CollectCodeFileService collectCodeFileService;

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

        for (ModuleFile moduleFile : modules) {
            printModuleInfoToTextFile(descriptionFile, moduleFile);
        }
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

    /**
     * 生成模块信息
     * @param targetFile
     * @param module
     */
    public void printModuleInfoToTextFile(File targetFile, ModuleFile module) {
        String moduleName = module.getModuleHome().getCurrentFile().getName();
        appendText(targetFile, targetFileInfo.getSegmentationSign1());
        appendText(targetFile, targetFileInfo.getTemplate2(module.getModuleHome().getCurrentFile().getName()) + "\r\n");
        appendText(targetFile, targetFileInfo.getSegmentationSign1());
        printReadMe(targetFile, findReadMe(module.getModuleHome().getCurrentFile()));
        appendText(targetFile, "\r\n");

        appendText(targetFile, targetFileInfo.getTemplate3(moduleName + "项目配置文件：") + "\r\n\r\n");
        printModuleConfigurations(targetFile, module.getConfigurationFile());
        appendText(targetFile, "\r\n\r\n");

        appendText(targetFile, targetFileInfo.getTemplate3("项目源代码：\r\n\r\n"));

        //广度优先打印源码
        List<ModuleNodeTree> srcDirList = module.getSrcDir();
        for (int i = 1; i <= srcDirList.size(); i++) {
            appendText(targetFile, targetFileInfo.getTemplate3(moduleName + "模块第" + i + "个源代码目录") + "\r\n\r\n");
            printSourceCode(targetFile, srcDirList.get(i - 1), module);
            appendText(targetFile, "\r\n");
        }

        appendText(targetFile, "\r\n\r\n\r\n");
    }

    public void printSourceCode(File targetFile, ModuleNodeTree srcNode, ModuleFile module) {
        File srcRootFile = srcNode.getCurrentFile();
        printCurrentLayerCode(targetFile, srcRootFile, module);
    }
    private void printCurrentLayerCode(File targetFile, File packageDir, ModuleFile module) {
        Set<String> extensionNameSet = module.getExtensionNameSet();
        File[] childFiles = packageDir.listFiles();
        List<File> childPackages = new ArrayList<>();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                childPackages.add(childFile);
            }else {
                if (extensionNameSet.contains(collectCodeFileService.getExtensionName(childFile))) {
                    appendText(targetFile, targetFileInfo.getSegmentationSign1() + childFile.getName() + "\r\n" + targetFileInfo.getSegmentationSign1());
                    appendFile(childFile, targetFile);
                    appendText(targetFile, "\r\n");
                }
            }
        }

        for (File childPackage : childPackages) {
            printCurrentLayerCode(targetFile, childPackage, module);
        }
    }

    public void printModuleConfigurations(File targetFile, List<ModuleNodeTree> configurationList) {
        for (ModuleNodeTree configuration : configurationList) {
            File configurationFile = configuration.getCurrentFile();
            if (configurationFile == null || !configurationFile.exists()) {
                return;
            }
            appendText(targetFile, targetFileInfo.getTemplate3(configurationFile.getName()) + ":\r\n");

            appendFile(configurationFile, targetFile);
        }
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
     * 追加复制文件
     * @param src
     * @param dst
     */
    private void appendFile(File src, File dst) {
        if (src == null || dst == null) {
            return;
        }
        if (!src.exists() || !dst.exists()) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dst, true)) {
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
     * 写入模块中的readme
     * @param targetFile
     * @param readme
     */
    private void printReadMe(File targetFile, File readme) {
        if (readme == null || !readme.exists()) {
            return;
        }
        appendText(targetFile, targetFileInfo.getTemplate3("README") + "\r\n");
        appendFile(readme, targetFile);
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
