package com.hongying.service;

import com.hongying.pojo.ProjectNodeTree;
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
    public List<ProjectNodeTree> collectSrcPath(File sourceFile, String srcFileName, String configurationFileName, int srcWithProject, int configurationWithSrc) {
        List<ProjectNodeTree> srcFileNodeTrees = new ArrayList<>();
        recursionSearchSrc(srcFileNodeTrees, sourceFile, srcFileName, configurationFileName, srcWithProject, configurationWithSrc);
        return srcFileNodeTrees;
    }

    private void recursionSearchSrc(List<ProjectNodeTree> srcFileNodeTrees, File currentFile, String srcFileName, String configurationFileName, int srcWithProject, int configurationWithSrc) {
        ProjectNodeTree fileNodeTree = judgeFileIsProjectByConfigurationFile(currentFile, srcFileName, configurationFileName, srcWithProject, configurationWithSrc);
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
    private ProjectNodeTree judgeFileIsProjectByConfigurationFile(File file, String srcFileName, String configurationFileName, int srcWithProject, int configurationWithProject) {
        if (!file.exists()) {
            return null;
        }
        if (!file.isDirectory()) {
            return null;
        }
        File[] childFiles = file.listFiles();
        boolean findConfigurationFile = configurationFileName == null;
        boolean findSrc = false;
        ProjectNodeTree projectHome = null;

        for (File current : childFiles) {
            if (current.isDirectory() && srcFileName.equals(current.getName())) {
                ProjectNodeTree src = new ProjectNodeTree();
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
            projectHome = new ProjectNodeTree();
        }
        return projectHome;
    }

    public static void main(String[] args) {
        List<ProjectNodeTree> src = new CollectProjectSrcService().collectSrcPath(new File("C:\\code\\长沙经开区产业链供需集市\\后端\\jkqcyl"), "src", "pom.xml", 1, 1);
        System.out.println();
    }
}
