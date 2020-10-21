package com.github.rulegin;

import com.github.rulegin.actors.install.RuleginInstallService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@EnableAsync
@EnableSwagger2
@SpringBootApplication()
@ComponentScan({"com.gtihub.rulegin"})
public class InstallApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "thingsboard";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(InstallApplication.class);
        application.setAdditionalProfiles("install");
        ConfigurableApplicationContext context = application.run(updateArguments(args));
        context.getBean(RuleginInstallService.class).performInstall();
    }

    private static String[] updateArguments(String[] args) {
        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
            String[] modifiedArgs = new String[args.length + 1];
            System.arraycopy(args, 0, modifiedArgs, 0, args.length);
            modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
            return modifiedArgs;
        }
        return args;
    }
}
