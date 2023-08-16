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
public class ModuleNodeTree {
    private ProjectFile projectFile;
    private ModuleNodeTree moduleHome;
    private ModuleNodeTree srcDir;
    private ModuleNodeTree configurationFile;
    private ModuleNodeTree parentDir;
    private List<ModuleNodeTree> childDir;
    private List<ModuleNodeTree> childFile;
    private File currentFile;
    private boolean isDir;
}
