package org.dows.setting;

import lombok.Data;

@Data
public class MailProperties {

    private String referenceSource;

    private Long referenceId;

    /**
     * 邮件服务器主机
     */
    private String host;

    /**
     * 邮件服务器端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 协议（imap或pop3）
     */
    private String protocol;

    /**
     * 邮箱文件夹
     */
    private String folder;

    /**
     * 轮询间隔（毫秒）
     */
    private Long pollInterval;

    /**
     * 是否启用SSL
     */
    private Boolean sslEnabled;

    /**
     * 是否标记为已读
     */
    private Boolean shouldMarkAsRead;

    /**
     * 是否删除邮件
     */
    private Boolean shouldDeleteMessages;

    /**
     * 最大获取数量
     */
    private Integer maxFetchSize;
    public String getKey(){
        return referenceSource + ":" + referenceId;
    }

}
