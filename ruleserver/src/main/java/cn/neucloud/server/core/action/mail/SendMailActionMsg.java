package cn.neucloud.server.core.action.mail;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SendMailActionMsg implements Serializable {

    private final String from;
    private final String to;
    private final String cc;
    private final String bcc;
    private final String subject;
    private final String body;
}
