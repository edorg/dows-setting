package org.dows.setting;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 邮件接收器
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MailReceiver implements MailReceivable{

    private static final String DOWNLOAD_FOLDER = "data";

    private static final String DOWNLOADED_MAIL_FOLDER = "DOWNLOADED";

    public void receive(MimeMessage receivedMessage) {
        try {

            Folder folder = receivedMessage.getFolder();
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            fetchMessagesInFolder(folder, messages);

            Arrays.stream(messages).filter(message -> {
                MimeMessage currentMessage = (MimeMessage) message;
                try {
                    return currentMessage.getMessageID().equalsIgnoreCase(receivedMessage.getMessageID());
                } catch (MessagingException e) {
                    log.error("Error occurred during process message", e);
                    return false;
                }
            }).forEach(this::extractMail);

            copyMailToDownloadedFolder(receivedMessage, folder);

            folder.close(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void fetchMessagesInFolder(Folder folder, Message[] messages) throws MessagingException {
        FetchProfile contentsProfile = new FetchProfile();
        contentsProfile.add(FetchProfile.Item.ENVELOPE);
        contentsProfile.add(FetchProfile.Item.CONTENT_INFO);
        contentsProfile.add(FetchProfile.Item.FLAGS);
        contentsProfile.add(FetchProfile.Item.SIZE);
        folder.fetch(messages, contentsProfile);
    }

    private void copyMailToDownloadedFolder(MimeMessage mimeMessage, Folder folder) throws MessagingException {
        Store store = folder.getStore();
        Folder downloadedMailFolder = store.getFolder(DOWNLOADED_MAIL_FOLDER);
        if (downloadedMailFolder.exists()) {
            downloadedMailFolder.open(Folder.READ_WRITE);
            downloadedMailFolder.appendMessages(new MimeMessage[]{mimeMessage});
            downloadedMailFolder.close();
        }
    }

    private void extractMail(Message message) {
        try {
            final MimeMessage messageToExtract = (MimeMessage) message;

            showMailContent(messageToExtract);

            downloadAttachmentFiles(messageToExtract);

            // To delete downloaded email
            //messageToExtract.setFlag(Flags.Flag.DELETED, true);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void showMailContent(MimeMessage mimeMessage) throws Exception {
        String from = Arrays.toString(mimeMessage.getFrom());
        String to = Arrays.toString(mimeMessage.getRecipients(Message.RecipientType.TO));
        String subject = mimeMessage.getSubject();
        String content = getPlainContent(mimeMessage);
        log.debug("From: {} to: {} | Subject: {}", from, to, subject);
        log.debug("Mail content: {}", content);
    }

    private String getPlainContent(MimeMessage mimeMessage) throws Exception {
        Object content = mimeMessage.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                        bodyPart.getFileName() == null &&
                        "text/plain".equals(bodyPart.getContentType())) {
                    return (String) bodyPart.getContent();
                }
            }
        }
        return "";
    }

    private void downloadAttachmentFiles(MimeMessage mimeMessage) throws Exception {
        Object content = mimeMessage.getContent();
        if (content instanceof Multipart multipart) {
            int attachmentCount = 0;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) ||
                        bodyPart.getFileName() != null) {
                    attachmentCount++;
                    String fileName = bodyPart.getFileName();
                    if (StringUtils.isNotBlank(fileName)) {
                        // 使用MimeUtility解码文件名，处理中文文件名
                        fileName = MimeUtility.decodeText(fileName);
                        String rootDirectoryPath = new FileSystemResource("").getFile().getAbsolutePath();
                        String dataFolderPath = rootDirectoryPath + File.separator + DOWNLOAD_FOLDER;
                        createDirectoryIfNotExists(dataFolderPath);

                        String downloadedAttachmentFilePath = rootDirectoryPath + File.separator + DOWNLOAD_FOLDER + File.separator + fileName;
                        File downloadedAttachmentFile = new File(downloadedAttachmentFilePath);

                        log.info("Save attachment file to: {}", downloadedAttachmentFilePath);

                        try (InputStream in = bodyPart.getInputStream();
                             OutputStream out = new FileOutputStream(downloadedAttachmentFile)) {
                            IOUtils.copy(in, out);
                        } catch (IOException e) {
                            log.error("Failed to save file.", e);
                        }
                    }
                }
            }
            log.debug("Email has {} attachment files", attachmentCount);
        } else {
            log.debug("Email has 0 attachment files");
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        if (!Files.exists(Paths.get(directoryPath))) {
            try {
                Files.createDirectories(Paths.get(directoryPath));
            } catch (IOException e) {
                log.error("An error occurred during create folder: {}", directoryPath, e);
            }
        }
    }
}
