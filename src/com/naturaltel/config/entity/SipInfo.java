package com.naturaltel.config.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SIP_INFO")
@XmlAccessorType(XmlAccessType.FIELD)
public class SipInfo {

    @XmlAttribute(name = "name")
    private String name;
    
    private String address;
    
    private int port;
    
    private String transport;
    
    @XmlElementWrapper(name = "USER_AGENTS")
    @XmlElement(name = "USER_AGENT")
    private List<UserAgent> userAgents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserAgent> getUserAgents() {
		return userAgents;
	}

	public void setUserAgents(List<UserAgent> userAgents) {
		this.userAgents = userAgents;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SipInfo [name=");
		builder.append(name);
		builder.append(", address=");
		builder.append(address);
		builder.append(", port=");
		builder.append(port);
		builder.append(", transport=");
		builder.append(transport);
		builder.append(", userAgents=");
		builder.append(userAgents);
		builder.append("]");
		return builder.toString();
	}

}
