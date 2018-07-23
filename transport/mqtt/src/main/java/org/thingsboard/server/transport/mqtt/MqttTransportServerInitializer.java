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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import org.thingsboard.server.common.transport.SessionMsgProcessor;
import org.thingsboard.server.common.transport.auth.DeviceAuthService;
import org.thingsboard.server.common.transport.quota.QuotaService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.transport.mqtt.adaptors.MqttTransportAdaptor;

/**
 * @author Andrew Shvayka
 */
public class MqttTransportServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SessionMsgProcessor processor;
    private final DeviceService deviceService;
    private final DeviceAuthService authService;
    private final RelationService relationService;
    private final MqttTransportAdaptor adaptor;
    private final MqttSslHandlerProvider sslHandlerProvider;
    private final QuotaService quotaService;
    /**
     * 负载内容
     */
    private final int maxPayloadSize;

    public MqttTransportServerInitializer(SessionMsgProcessor processor, DeviceService deviceService, DeviceAuthService authService, RelationService relationService,
                                          MqttTransportAdaptor adaptor, MqttSslHandlerProvider sslHandlerProvider,
                                          QuotaService quotaService, int maxPayloadSize) {
        this.processor = processor;
        this.deviceService = deviceService;
        this.authService = authService;
        this.relationService = relationService;
        this.adaptor = adaptor;
        this.sslHandlerProvider = sslHandlerProvider;
        this.quotaService = quotaService;
        this.maxPayloadSize = maxPayloadSize;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();//设置ChannelPipeLine
        SslHandler sslHandler = null;
        //判断SSL处理器处理类是否为空，如果不为空，将SSL处理器加入到ChannelPipeLine
        if (sslHandlerProvider != null) {
            sslHandler = sslHandlerProvider.getSslHandler();
            pipeline.addLast(sslHandler);
        }
        //添加负载内容的解编码器
        pipeline.addLast("decoder", new MqttDecoder(maxPayloadSize));
        pipeline.addLast("encoder", MqttEncoder.INSTANCE);

        MqttTransportHandler handler = new MqttTransportHandler(processor, deviceService, authService, relationService,
                adaptor, sslHandler, quotaService);

        //添加Mqtt协议处理器
        pipeline.addLast(handler);
        //异步操作完成时回调
        ch.closeFuture().addListener(handler);
    }

}
