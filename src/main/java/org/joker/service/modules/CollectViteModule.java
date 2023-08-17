package org.joker.service.modules;

import org.joker.pojo.ModuleFile;
import org.joker.pojo.ProjectFile;

import java.util.*;

public class CollectViteModule extends CollectModule implements AbstractCollectModule {
    @Override
    public List<ModuleFile> collectModules(ProjectFile projectFile) {
        Map<String, Integer> srcMap = new HashMap<>();
        Map<String, Integer> configurationMap = new HashMap<>();
        srcMap.put("src", 1);
        configurationMap.put("vue.config.js", 1);
        configurationMap.put("package.json", 1);
        List<ModuleFile> moduleFiles = collectModulesBySrcAndConfiguration(projectFile, srcMap, configurationMap, true);
        for (ModuleFile moduleFile : moduleFiles) {
            moduleFile.setModuleType("vite");
            Set<String> extensionNames = new HashSet<>();
            extensionNames.add(".vue");
            extensionNames.add(".html");
            extensionNames.add(".css");
            extensionNames.add(".js");
            moduleFile.setExtensionNameSet(extensionNames);
        }
        return moduleFiles;
    }
}
