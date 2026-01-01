package org.dows.setting;

import jakarta.mail.internet.MimeMessage;

public interface MailReceivable {
    void receive(MimeMessage payload);
}
