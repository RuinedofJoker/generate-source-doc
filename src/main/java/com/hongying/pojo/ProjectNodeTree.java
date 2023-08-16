package com.hongying.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectNodeTree {
    private ProjectNodeTree projectHome;
    private ProjectNodeTree srcDir;
    private ProjectNodeTree configurationFile;
    private ProjectNodeTree parentDir;
    private List<ProjectNodeTree> childDir;
    private List<ProjectNodeTree> childFile;
    private File currentFile;
    private boolean isDir;
}
