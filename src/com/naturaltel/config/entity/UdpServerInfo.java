/*
 * @(#)MonitCheckAliveConfiguration.java 2015年5月27日
 *
 * Copyright (C) 2015 Naturaltel Communication Co., LTD.
 * All right reserved.
 */
package com.naturaltel.config.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UDP_SERVER_INFO")
@XmlAccessorType(XmlAccessType.FIELD)
public class UdpServerInfo {
    
    @XmlAttribute(name = "address")
    public String address;
    
    @XmlAttribute(name = "port")
    public int port;
    
    @XmlAttribute(name = "destAddress")
    public String destAddress;
    
    @XmlAttribute(name = "destPort")
    public int destPort;
    
    @XmlElement(name = "COMMANDS")
    public UdpCmds udpCmds;

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public UdpCmds getUdpCmds() {
        return udpCmds;
    }

    public void setUdpCmds(UdpCmds udpCmds) {
        this.udpCmds = udpCmds;
    }

    public String getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(String destAddress) {
		this.destAddress = destAddress;
	}

	public int getDestPort() {
		return destPort;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UdpServerInfo [address=");
		builder.append(address);
		builder.append(", port=");
		builder.append(port);
		builder.append(", destAddress=");
		builder.append(destAddress);
		builder.append(", destPort=");
		builder.append(destPort);
		builder.append(", udpCmds=");
		builder.append(udpCmds);
		builder.append("]");
		return builder.toString();
	}

    
    
}
