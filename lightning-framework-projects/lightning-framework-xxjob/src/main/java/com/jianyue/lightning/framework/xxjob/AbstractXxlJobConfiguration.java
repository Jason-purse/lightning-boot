package com.jianyue.lightning.framework.xxjob;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class AbstractXxlJobConfiguration {

    protected Logger logger = LoggerFactory.getLogger(AbstractXxlJobConfiguration.class);

    @Autowired
    protected XxlJobProperties xxlJobProperties;

    protected XxlJobSpringExecutor createXxlJobExecutor(String ip) {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getAppname());
        xxlJobSpringExecutor.setIp(StringUtils.isNoneBlank(xxlJobProperties.getIp()) ? xxlJobProperties.getIp() : ip);
        xxlJobSpringExecutor.setPort(xxlJobProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());

        return xxlJobSpringExecutor;
    }

    protected String getIp() throws UnknownHostException, SocketException {
        final String hostName = System.getenv("HOSTNAME");
        String hostAddress = null;

        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netInt : Collections.list(networkInterfaces)) {
            for (InetAddress inetAddress : Collections.list(netInt.getInetAddresses())) {
                if (hostName.equals(inetAddress.getHostName())) {
                    hostAddress = inetAddress.getHostAddress();
                }

                System.out.printf("Inet %s: %s / %s\n", netInt.getName(),  inetAddress.getHostName(), inetAddress.getHostAddress());
            }
        }
        if (hostAddress == null) {
            throw new UnknownHostException("Cannot find ip address for hostname: " + hostName);
        }
        return hostAddress;
    }
}
