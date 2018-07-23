/**
 * Copyright © 2016-2018 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.transport.mqtt;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.transport.SessionMsgProcessor;
import org.thingsboard.server.common.transport.auth.DeviceAuthService;
import org.thingsboard.server.common.transport.quota.host.HostRequestsQuotaService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.transport.mqtt.adaptors.MqttTransportAdaptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Andrew Shvayka
 */
@Service("MqttTransportService")
@ConditionalOnProperty(prefix = "mqtt", value = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class MqttTransportService {

    private static final String V1 = "v1";
    private static final String DEVICE = "device";

    /**
     * Bean工厂ApplicationContext
     */
    @Autowired(required = false)
    private ApplicationContext appContext;

    /**
     * 会话消息处理接口
     */
    @Autowired(required = false)
    private SessionMsgProcessor processor;

    /**
     * 设备接口处理接口类
     */
    @Autowired(required = false)
    private DeviceService deviceService;

    @Autowired(required = false)
    /**
     * 设备权限接口类
     */
    private DeviceAuthService authService;

    @Autowired(required = false)
    /**
     * 关系接口类
     */
    private RelationService relationService;

    @Autowired(required = false)
    /**
     * SSL处理器
     */
    private MqttSslHandlerProvider sslHandlerProvider;

    @Autowired(required = false)
    /**
     * TODO
     */
    private HostRequestsQuotaService quotaService;

    /**
     * MQTT 服务参数:
     * 绑定地址:0.0.0.0
     * 绑定端口:1883
     * 适配器名:JsonMqttAdaptor
     * netty内存检测级别: DISABLED
     * boss线程组线程数: 1
     * work线程组线程数: 12
     * 帧内容长度限制: 65536
     *
     *
     * DISABLED: 不进行内存泄露的检测；
     * SIMPLE: 抽样检测，且只对部分方法调用进行记录，消耗较小，有泄漏时可能会延迟报告，默认级别；
     * ADVANCED: 抽样检测，记录对象最近几次的调用记录，有泄漏时可能会延迟报告；
     * PARANOID: 每次创建一个对象时都进行泄露检测，且会记录对象最近的详细调用记录。是比较激进的内存泄露检测级别，消耗最大，建议只在测试时使用。
     */
    @Value("${mqtt.bind_address}")
    private String host;
    @Value("${mqtt.bind_port}")
    private Integer port;
    @Value("${mqtt.adaptor}")
    private String adaptorName;

    @Value("${mqtt.netty.leak_detector_level}")
    private String leakDetectorLevel;
    @Value("${mqtt.netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;
    @Value("${mqtt.netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;
    @Value("${mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    private MqttTransportAdaptor adaptor;

    private Channel serverChannel;
    // 创建两个 EventLoopGroup 对象
    private EventLoopGroup bossGroup;//创建boss线程组 用于服务端接受客户端的连接
    private EventLoopGroup workerGroup;//创建worker线程组 用于进行SocketChannel的数据读写

    @PostConstruct
    public void init() throws Exception {
        //设置服务端Netty内存读写泄露检测级别，缺省条件下为:DISABLED
        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));

        log.info("Starting MQTT transport...");
        log.info("Lookup MQTT transport adaptor {}", adaptorName);
        //适配器名加载成Bean类
        this.adaptor = (MqttTransportAdaptor) appContext.getBean(adaptorName);

        log.info("Starting MQTT transport server");
        //设置boss线程组和work线程组的线程数量
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
        //创建ServerBootstrap对象
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)//设置使用的EventLoopGroup
                .channel(NioServerSocketChannel.class)//设置要被实例化的为NioServerSocketChannel类
                .childHandler(new MqttTransportServerInitializer(processor, deviceService, authService, relationService,
                        adaptor, sslHandlerProvider, quotaService, maxPayloadSize));//设置连入服务端的Client的SocketChannel的处理器
        /**
         * 绑定端口，并同步等待成功，即启动服务器
         */
        serverChannel = b.bind(host, port).sync().channel();
        log.info("Mqtt transport started!");
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping MQTT transport!");
        try {
            /**
             * 监听服务端关闭，并阻塞等待
             */
            serverChannel.close().sync();
        } finally {
            //优雅关闭俩个EventLoopGroup对象
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("MQTT transport stopped!");
    }
}
