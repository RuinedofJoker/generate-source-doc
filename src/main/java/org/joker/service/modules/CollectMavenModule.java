package org.joker.service.modules;

import org.joker.pojo.ModuleFile;
import org.joker.pojo.ProjectFile;

import java.util.*;

public class CollectMavenModule extends CollectModule implements AbstractCollectModule {
    @Override
    public List<ModuleFile> collectModules(ProjectFile projectFile) {
        Map<String, Integer> srcMap = new HashMap<>();
        Map<String, Integer> configurationMap = new HashMap<>();
        srcMap.put("src", 1);
        configurationMap.put("pom.xml", 1);
        List<ModuleFile> moduleFiles = collectModulesBySrcAndConfiguration(projectFile, srcMap, configurationMap, true);
        for (ModuleFile moduleFile : moduleFiles) {
            moduleFile.setModuleType("maven");
            Set<String> extensionNames = new HashSet<>();
            extensionNames.add("java");
            moduleFile.setExtensionNameSet(extensionNames);
        }
        return moduleFiles;
    }
}
