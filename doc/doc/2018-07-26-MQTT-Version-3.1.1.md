---
title: MQTT Version 3.1.1
date: 2018-07-26
tags: MQTT
categories: MQTT
---

![MQTT背景图](D:\IdeaProjects\thingsboard-master\doc\pic\MQTT_banner.jpg)



# MQTT认知



## MQTT控制包的结构

MQTT协议通过以定义的方式交换一系列MQTT控制数据包来工作。本节介绍这些数据包的格式。 

MQTT控制包最多由三部分组成，总是按照以下顺序组成。

|   固定标头，存在于所有MQTT控制数据包中   |
| :--------------------------------------: |
|  **变量头，存在于某些MQTT控制数据包中**  |
| **有效负载，存在于某些MQTT控制数据包中** |



## 固定标题