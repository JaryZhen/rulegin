/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.neucloud.server;

import cn.neucloud.server.actors.install.NeuruleInstallService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@EnableAsync
@EnableSwagger2
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@ComponentScan({"cn.neucloud.server"})
public class InstallApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "thingsboard";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(InstallApplication.class);
        application.setAdditionalProfiles("install");
        ConfigurableApplicationContext context = application.run(updateArguments(args));
        context.getBean(NeuruleInstallService.class).performInstall();
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
