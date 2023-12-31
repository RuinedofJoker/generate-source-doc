package org.joker.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleNodeTree {
    private ModuleNodeTree parentDir;
    private List<ModuleNodeTree> packageList = new ArrayList<>();
    private List<ModuleNodeTree> codeList = new ArrayList<>();
    private File currentFile;
}
