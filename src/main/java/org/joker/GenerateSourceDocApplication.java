package org.joker;

import org.joker.ui.AppMain;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GenerateSourceDocApplication {

    public static void main(String[] args) {
        runInVisualization(args);
    }

    public static void runInVisualization(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(GenerateSourceDocApplication.class);
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext applicationContext = builder.headless(false).run(args);

        applicationContext.getBean(AppMain.class);
    }

}
