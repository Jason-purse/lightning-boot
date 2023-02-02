package com.jianyue.lightning.client;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 代理选择器
 * <p>
 * 支持apache组件
 *
 * @author konghang
 */
public class LightningProxySelector extends ProxySelector {

    private final Proxy proxy;

    /**
     * 仅支持socks代理
     *
     * @param host 主机
     * @param port 端口
     */
    public LightningProxySelector(String host, Integer port) {
        this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
    }

    @Override
    public List<Proxy> select(URI uri) {
        return new ArrayList<>(List.of(proxy));
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        if (uri == null || sa == null || ioe == null) {
            throw new IllegalArgumentException("Arguments can't be null.");
        }
    }
}
