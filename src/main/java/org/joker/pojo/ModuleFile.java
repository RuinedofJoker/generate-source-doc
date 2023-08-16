package org.joker.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ModuleFile {
    private ModuleNodeTree moduleHome;
    private String moduleType;
    private List<ModuleNodeTree> srcDir;
    private List<ModuleNodeTree> configurationFile;
}
