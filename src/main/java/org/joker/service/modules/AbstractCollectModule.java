package org.joker.service.modules;

import org.joker.pojo.ModuleFile;
import org.joker.pojo.ProjectFile;

import java.util.List;
import java.util.Map;

public interface AbstractCollectModule {
    List<ModuleFile> collectModules(ProjectFile projectFile);
}
