package org.dows.setting;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dows.rfa.api.ResumeFileApi;
import org.dows.rfa.open.PostResumeFileEntityRequest;
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
public class MailReceiver implements MailReceivable {

    private static final String DOWNLOAD_FOLDER = "data";

    private static final String DOWNLOADED_MAIL_FOLDER = "DOWNLOADED";
    private static final String ATTACHMENT_SAVE_PATH = "D:/tencent_exmail_attachments/";
    private final ResumeFileApi resumeFileApi;

    public void receive(MimeMessage receivedMessage) {
        try {

            Folder folder = receivedMessage.getFolder();
            //folder.open(Folder.READ_WRITE);
            folder.open(Folder.READ_ONLY);
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


    /**
     * 解析邮件基础信息（发件人、收件人、主题、发送时间等）
     */
    private static void parseMailBasicInfo(Message message) throws Exception {
        System.out.println("1. 邮件基础信息");
        System.out.println("   邮件主题：" + message.getSubject());
        System.out.println("   发送时间：" + message.getSentDate());
        System.out.println("   邮件是否已读：" + message.isSet(Flags.Flag.SEEN));
        System.out.println("   发件人：" + message.getFrom()[0]);
        System.out.println("   收件人：" + message.getRecipients(Message.RecipientType.TO)[0]);
    }

    /**
     * 解析邮件正文内容（支持纯文本和HTML格式）
     */
    private static void parseMailContent(Message message) throws Exception {
        System.out.println("2. 邮件正文内容");
        // 邮件内容可能是简单文本或多部分内容（正文+附件）
        Object content = message.getContent();
        if (content instanceof MimeMultipart) {
            // 多部分内容（处理正文，忽略附件）
            MimeMultipart multipart = (MimeMultipart) content;
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
                // 判断是否为附件（附件通常有文件名）
                if (bodyPart.isMimeType("text/plain") && !Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    System.out.println("   纯文本正文：" + bodyPart.getContent());
                } else if (bodyPart.isMimeType("text/html") && !Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    System.out.println("   HTML格式正文：" + bodyPart.getContent());
                }
            }
        } else {
            // 简单文本内容
            System.out.println("   纯文本正文：" + content);
        }
    }

    /**
     * 解析并下载邮件附件到本地指定目录
     */
    private static void downloadMailAttachments(Message message) throws Exception {
        System.out.println("3. 邮件附件处理");
        // 先创建附件保存目录（若不存在）
        File attachmentDir = new File(ATTACHMENT_SAVE_PATH);
        if (!attachmentDir.exists()) {
            attachmentDir.mkdirs();
        }

        Object content = message.getContent();
        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            int partCount = multipart.getCount();
            boolean hasAttachment = false;

            for (int i = 0; i < partCount; i++) {
                MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
                // 判断是否为附件（两种判断方式：1. 有Disposition=ATTACHMENT；2. 有文件名且非正文类型）
                boolean isAttachment = Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                        || (bodyPart.getFileName() != null && !bodyPart.isMimeType("text/plain") && !bodyPart.isMimeType("text/html"));

                if (isAttachment) {
                    hasAttachment = true;
                    String fileName = bodyPart.getFileName();
                    // 解决中文文件名乱码问题
                    fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                    String attachmentPath = ATTACHMENT_SAVE_PATH + File.separator + fileName;

                    // 下载附件到本地
                    try (InputStream in = bodyPart.getInputStream();
                         FileOutputStream out = new FileOutputStream(attachmentPath)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                    System.out.println("   已下载附件：" + fileName + "，保存路径：" + attachmentPath);
                }
            }

            if (!hasAttachment) {
                System.out.println("   该邮件无附件");
            }
        } else {
            System.out.println("   该邮件无附件");
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
            // todo 构建请求
            PostResumeFileEntityRequest request = new PostResumeFileEntityRequest();
            // 解析邮件基本信息
            parseMailBasicInfo(message);
            parseMailContent(message);
            showMailContent(messageToExtract);

            downloadAttachmentFiles(messageToExtract);
            // todo 保存请求
            resumeFileApi.postEntity(request);

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
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) || bodyPart.getFileName() != null) {
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
