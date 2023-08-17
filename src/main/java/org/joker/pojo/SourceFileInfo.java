package org.joker.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "generate-info.file.source")
@Data
public class SourceFileInfo {
    private String path;

}
