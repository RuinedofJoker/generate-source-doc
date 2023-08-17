package org.joker.pojo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ModuleFile {
    private ModuleNodeTree moduleHome;
    private String moduleType;
    private Set<String> extensionNameSet;
    private List<ModuleNodeTree> srcDir;
    private List<ModuleNodeTree> configurationFile;
}
