package com.example.sakuraanime.feedBack;
import androidx.annotation.NonNull;



import java.io.File;

public class SendMailUtil {

    public static void send(final File file, String toAdd,String content) {
        final MailInfo mailInfo = creatMail(toAdd,content);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo, file);
            }
        }).start();
    }

    public static void send(String toAdd,String content) {
        final MailInfo mailInfo = creatMail(toAdd,content);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    @NonNull
    private static MailInfo creatMail(String toAdd,String content) {

        //        String HOST = ShareUtils.getString(MyApplication.getInstance(), "HOST", "");
//        String PORT = ShareUtils.getString(MyApplication.getInstance(), "PORT", "");
//        String FROM_ADD = ShareUtils.getString(MyApplication.getInstance(), "FROM_ADD", "");
//        String FROM_PSW = ShareUtils.getString(MyApplication.getInstance(), "FROM_PSW", "");
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost("smtp.qq.com");//发送方邮箱服务器
        mailInfo.setMailServerPort("587");//发送方邮箱端口号
        mailInfo.setValidate(true);
        mailInfo.setUserName("1749087531@qq.com"); // 发送者邮箱地址
        mailInfo.setPassword("dacozrxsyqqyccai");// 发送者邮箱授权码
        mailInfo.setFromAddress("1749087531@qq.com"); // 发送者邮箱
        mailInfo.setToAddress(toAdd); // 接收者邮箱
        mailInfo.setSubject("Android Feedback"); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }

}
