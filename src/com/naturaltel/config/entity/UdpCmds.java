/*
 * @(#)MonitCheckAliveConfiguration.java 2015年5月27日
 *
 * Copyright (C) 2015 Naturaltel Communication Co., LTD.
 * All right reserved.
 */
package com.naturaltel.config.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "COMMANDS")
@XmlAccessorType(XmlAccessType.FIELD)
public class UdpCmds {
    
    @XmlElement(name = "ACK")
    public String ackCmd;
    
    @XmlElement(name = "PING")
    public String pingCmd;
    
    @XmlElement(name = "RELOAD_CITY_CELL")
    public String reloadCityCellCmd;

    public String getAckCmd() {
        return ackCmd;
    }

    public void setAckCmd(String ackCmd) {
        this.ackCmd = ackCmd;
    }

    public String getPingCmd() {
        return pingCmd;
    }

    public void setPingCmd(String pingCmd) {
        this.pingCmd = pingCmd;
    }

    public String getReloadCityCellCmd() {
        return reloadCityCellCmd;
    }

    public void setReloadCityCellCmd(String reloadCityCellCmd) {
        this.reloadCityCellCmd = reloadCityCellCmd;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UdpCmds [ackCmd=");
        builder.append(ackCmd);
        builder.append(", pingCmd=");
        builder.append(pingCmd);
        builder.append(", reloadCityCellCmd=");
        builder.append(reloadCityCellCmd);
        builder.append("]");
        return builder.toString();
    }

    
    
}
