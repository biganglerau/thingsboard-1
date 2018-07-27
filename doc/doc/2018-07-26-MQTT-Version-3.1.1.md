---
title: MQTT Version 3.1.1
date: 2018-07-26
tags: MQTT
categories: MQTT
---

![MQTT背景图](/home/james/workspace/thingsboard/doc/pic/MQTT_banner.jpg)



# MQTT认知



## MQTT控制包的结构

MQTT协议通过以定义的方式交换一系列MQTT控制数据包来工作。本节介绍这些数据包的格式。 

MQTT控制包最多由三部分组成，总是按照以下顺序组成。

| **固定标头，存在于==所有==MQTT控制数据包中** |
| :------------------------------------------: |
|    变量头，存在于==某些==MQTT控制数据包中    |
|   有效负载，存在于==某些==MQTT控制数据包中   |



## 固定标题

|    名称     |  值  |  流动方向  | 描述                   |
| :---------: | :--: | :--------: | ---------------------- |
|  Reserved   |  0   |   被禁止   | 保留的                 |
|   CONNECT   |  1   |  C --> S   | 客户端请求连接到服务器 |
|   CONNACK   |  2   |  S --> C   | 连接确认               |
|   PUBLISH   |  3   | C < -- > S | 推送消息               |
|   PUBACK    |  4   | C < -- > S | 推送确认               |
|   PUBREC    |  5   | C < -- > S | 推送接受               |
|   PUBREL    |  6   | C < -- > S | 推送放开               |
|   PUBCOMP   |  7   | C < -- > S | 推送完成               |
|  SUBSCRIBE  |  8   |  C --> S   | 客户端订阅请求         |
|   SUBACK    |  9   |  S --> C   | 订阅确认               |
| UNSUBSCRIBE |  10  |  C --> S   | 取消订阅请求           |
|  UNSUBACK   |  11  |  S --> C   | 取消订阅确认           |
|   PINGREQ   |  12  |  C --> S   | PING请求               |
|  PINGRESP   |  13  |  S --> C   | PING响应               |
| DISCONNECT  |  14  |  C --> S   | 客户端正在断开连接     |
|  Reserved   |  15  |   被禁止   | 保留的                 |

