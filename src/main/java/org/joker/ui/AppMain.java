package org.joker.ui;

import org.joker.common.AwareContent;
import org.joker.pojo.ModuleFile;
import org.joker.pojo.ProjectFile;
import org.joker.service.CollectModuleService;
import org.joker.service.PrintProjectInfoService;
import org.joker.utils.FileUtil;
import org.joker.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
public class AppMain extends JFrame {

    @Autowired
    private AwareContent awareContent;

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private CollectModuleService collectModuleService;

    @Autowired
    private PrintProjectInfoService printProjectInfoService;


    FlowLayout layout = new FlowLayout();

    JLabel readLabel = new JLabel("请输入源代码文件夹路径:");
    JLabel writeLabel = new JLabel("请输入输出文件路径:");
    JTextField readPathArea = new JTextField(50);
    JTextField writePathArea = new JTextField(50);
    JButton readButton = new JButton("浏览");
    JButton writeButton = new JButton("浏览");
    JButton submit = new JButton("提交");
    JLabel taskStateLabel = new JLabel("没有任务");

    public AppMain() throws HeadlessException {

        layout.setAlignment(FlowLayout.LEFT);

        readButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(), "选择");
            File file = chooser.getSelectedFile();
            readPathArea.setText(file.getAbsoluteFile().toString());
        });

        writeButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(), "选择");
            File file = chooser.getSelectedFile();
            writePathArea.setText(file.getAbsoluteFile().toString());
        });

        submit.addActionListener(e -> {
            submit.setEnabled(false);
            fileUtil.setSourcePath(readPathArea.getText());
            fileUtil.setTargetPath(writePathArea.getText());
            taskStateLabel.setText("任务正在处理,请稍后");

            System.out.println("------------------------任务开始,请不要打开读取目录中的文件------------------------");
            //开始收集模块
            ProjectFile projectFile = fileUtil.getProjectFile();
            List<ModuleFile> mavenModuleFiles = collectModuleService.getCollectMavenModule().collectModules(projectFile);
            List<ModuleFile> viteModuleFiles = collectModuleService.getCollectViteModule().collectModules(projectFile);
            mavenModuleFiles.addAll(viteModuleFiles);
            projectFile.setModules(mavenModuleFiles);
            //收集模块完成

            //开始收集模块内所有源代码

            //生成描述文档
            printProjectInfoService.createProjectDescriptionFile(projectFile);
            System.out.println();
            //只有选择的文件合法才能进入处理
            if (fileUtil.fileIsLegitimate()) {
                taskStateLabel.setText("任务完成");
            }else {
                taskStateLabel.setText("文件不合法");
            }
            System.out.println("------------------------任务完成------------------------");

            submit.setEnabled(true);
        });

        add(readLabel);
        add(readPathArea);
        add(readButton);

        add(writeLabel);
        add(writePathArea);
        add(writeButton);

        add(submit);
        add(taskStateLabel);

        setBounds(700, 300, 800, 500);//设置位置与大小
        setVisible(true);//设置可见
        setBackground(new Color(77,77,77));
        setResizable(false);//设置不可改变大小
        setLayout(layout);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @PostConstruct
    public void init() {
        readPathArea.setText(StringUtil.unicodeToStr(fileUtil.getInitReadPath()));
        writePathArea.setText(StringUtil.unicodeToStr(fileUtil.getInitWritePath()));
    }
}
