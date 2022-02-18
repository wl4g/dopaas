/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.dopaas.lcdp.tools.hbase.bulk;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import org.apache.hadoop.net.NetUtils;

/**
 * {@link SocketChannelTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-02-18 v1.0.0
 * @since v1.0.0
 */
public class SocketChannelTests {

    /**
     * {@link org.apache.hadoop.ipc.Client.Connection#setupConnection()}
     */
    public static void main(String[] args) throws Exception {
        Socket socket = SocketChannel.open().socket();
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        InetSocketAddress server = new InetSocketAddress("127.0.0.1", 8020);
        NetUtils.connect(socket, server, 20000);
        System.out.println(socket.isConnected());
    }

}
