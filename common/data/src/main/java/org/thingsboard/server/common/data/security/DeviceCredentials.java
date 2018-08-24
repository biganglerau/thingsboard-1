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
package org.thingsboard.server.common.data.security;

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.id.DeviceCredentialsId;
import org.thingsboard.server.common.data.id.DeviceId;

/**
 * 设备认证
 */
@EqualsAndHashCode(callSuper = true)
public class DeviceCredentials extends BaseData<DeviceCredentialsId> implements DeviceCredentialsFilter {

    private static final long serialVersionUID = -7869261127032877765L;
    /**
     * 设备编号
     */
    private DeviceId deviceId;
    /**
     * 设备认证类型
     */
    private DeviceCredentialsType credentialsType;
    /**
     * 认证编号
     */
    private String credentialsId;
    /**
     * 认证值
     */
    private String credentialsValue;
    
    public DeviceCredentials() {
        super();
    }

    public DeviceCredentials(DeviceCredentialsId id) {
        super(id);
    }

    public DeviceCredentials(DeviceCredentials deviceCredentials) {
        super(deviceCredentials);
        this.deviceId = deviceCredentials.getDeviceId();
        this.credentialsType = deviceCredentials.getCredentialsType();
        this.credentialsId = deviceCredentials.getCredentialsId();
        this.credentialsValue = deviceCredentials.getCredentialsValue();
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public DeviceCredentialsType getCredentialsType() {
        return credentialsType;
    }

    public void setCredentialsType(DeviceCredentialsType credentialsType) {
        this.credentialsType = credentialsType;
    }

    @Override
    public String getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getCredentialsValue() {
        return credentialsValue;
    }

    public void setCredentialsValue(String credentialsValue) {
        this.credentialsValue = credentialsValue;
    }

    @Override
    public String toString() {
        return "DeviceCredentials [deviceId=" + deviceId + ", credentialsType=" + credentialsType + ", credentialsId="
                + credentialsId + ", credentialsValue=" + credentialsValue + ", createdTime=" + createdTime + ", id="
                + id + "]";
    }

}
