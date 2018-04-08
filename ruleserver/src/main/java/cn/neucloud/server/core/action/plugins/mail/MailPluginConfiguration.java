package cn.neucloud.server.core.action.plugins.mail;

import lombok.Data;

@Data
public class MailPluginConfiguration {
    private String host;
    private Integer port;
    private String username;
    private String password;
}
