package org.joker.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "generate-info.file.target")
@Data
public class TargetFileInfo {
    private String path;
    private String template1;
    private String templateReplace1;
    private String template2;
    private String templateReplace2;
    private String template3;
    private String templateReplace3;
    private String segmentationSign1;

    public String getTemplate1(String title) {
        return template1.replace(templateReplace1, title);
    }

    public String getTemplate2(String title) {
        return template2.replace(templateReplace2, title);
    }

    public String getTemplate3(String title) {
        return template3.replace(templateReplace3, title);
    }
}
