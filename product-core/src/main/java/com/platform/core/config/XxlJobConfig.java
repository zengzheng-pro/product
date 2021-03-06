//package com.platform.core.config;
//
//import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class XxlJobConfig {
//    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);
//
//    @Value("${xxl.job.admin.addresses:localhost}")
//    private String adminAddresses;
//
//    @Value("${xxl.job.accessToken:localhost}")
//    private String accessToken;
//
//    @Value("${xxl.job.executor.appname:localhost}")
//    private String appname;
//
//    @Value("${xxl.job.executor.address:localhost}")
//    private String address;
//
//    @Value("${xxl.job.executor.ip:localhost}")
//    private String ip;
//
//    @Value("${xxl.job.executor.port:1}")
//    private int port;
//
////    @Value("${xxl.job.executor.logpath}")
////    private String logPath;
//
//    @Value("${xxl.job.executor.logretentiondays:1}")
//    private int logRetentionDays;
//
//
//    @Bean
//    public XxlJobSpringExecutor xxlJobExecutor() {
//        if (adminAddresses.equals("localhost") || accessToken.equals("localhost") || appname.equals("localhost")
//                || address.equals("localhost") || ip.equals("localhost") || port == 1) {
//            return null;
//        }
//        logger.info(">>>>>>>>>>> xxl-job config init.");
//        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
//        xxlJobSpringExecutor.setAppname(appname);
//        xxlJobSpringExecutor.setAddress(address);
//        xxlJobSpringExecutor.setIp(ip);
//        xxlJobSpringExecutor.setPort(port);
//        xxlJobSpringExecutor.setAccessToken(accessToken);
////        xxlJobSpringExecutor.setLogPath(logPath);
//        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
//
//        return xxlJobSpringExecutor;
//    }
//
//    public String getAdminAddresses() {
//        return adminAddresses;
//    }
//
//    public void setAdminAddresses(String adminAddresses) {
//        this.adminAddresses = adminAddresses;
//    }
//
//    public String getAccessToken() {
//        return accessToken;
//    }
//
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }
//
//    public String getAppname() {
//        return appname;
//    }
//
//    public void setAppname(String appname) {
//        this.appname = appname;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getIp() {
//        return ip;
//    }
//
//    public void setIp(String ip) {
//        this.ip = ip;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public int getLogRetentionDays() {
//        return logRetentionDays;
//    }
//
//    public void setLogRetentionDays(int logRetentionDays) {
//        this.logRetentionDays = logRetentionDays;
//    }
//}
