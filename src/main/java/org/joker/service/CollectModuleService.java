package org.joker.service;

import lombok.Getter;
import org.joker.service.modules.CollectMavenModule;
import org.joker.service.modules.CollectViteModule;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Getter
public class CollectModuleService {
    private CollectMavenModule collectMavenModule;
    private CollectViteModule collectViteModule;

    @PostConstruct
    public void init() {
        collectMavenModule = new CollectMavenModule();
        collectViteModule = new CollectViteModule();
    }
}
