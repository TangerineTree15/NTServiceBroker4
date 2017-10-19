package com.naturaltel.config.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "USER_AGENT")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAgent {

    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "address")
    private String address;
    
    @XmlAttribute(name = "port")
    private int port;

    @XmlAttribute(name = "transport")
    private String transport;
    
    @XmlAttribute(name = "domain_name")
    private String domainName;
    
    @XmlAttribute(name = "path_name")
    private String pathName;
    
    @XmlAttribute(name = "pnp")
    private String pnp;
    
    @XmlAttribute(name = "msisdn")
    private String msisdn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getPnp() {
		return pnp;
	}

	public void setPnp(String pnp) {
		this.pnp = pnp;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAgent [name=");
		builder.append(name);
		builder.append(", address=");
		builder.append(address);
		builder.append(", port=");
		builder.append(port);
		builder.append(", transport=");
		builder.append(transport);
		builder.append(", domainName=");
		builder.append(domainName);
		builder.append(", pathName=");
		builder.append(pathName);
		builder.append(", pnp=");
		builder.append(pnp);
		builder.append(", msisdn=");
		builder.append(msisdn);
		builder.append("]");
		return builder.toString();
	}

    
}
