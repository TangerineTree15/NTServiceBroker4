package com.naturaltel.config.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class RootConfig {

	@XmlElement(name = "TC")
	private int testCase;
	
    @XmlElement(name = "SIP_INFO")
    private SipInfo sipInfo;
    
    @XmlElement(name = "SIPSTACK_PROPERTIES")
    private String sipStackFilePath;
    
    @XmlElement(name = "DATABASE_INFO")
    private DBInfo dbInfo;
    
    @XmlElement(name = "UDP_SERVER_INFO")
    private UdpServerInfo udpServerInfo;
    
    @XmlElement(name = "LOG4J_PROPERTIES")
    private String log4jPropFilePath;
    
    
    public int getTestCase() {
		return testCase;
	}

	public void setTestCase(int testCase) {
		this.testCase = testCase;
	}

	public SipInfo getSipInfo() {
        return sipInfo;
    }

    public void setSipInfo(SipInfo sipInfo) {
        this.sipInfo = sipInfo;
    }

    public String getSipStackFilePath() {
        return sipStackFilePath;
    }

    public void setSipStackFilePath(String sipStackFilePath) {
        this.sipStackFilePath = sipStackFilePath;
    }

    public DBInfo getDbInfo() {
        return dbInfo;
    }

    public void setDbInfo(DBInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public UdpServerInfo getUdpServerInfo() {
        return udpServerInfo;
    }

    public void setUdpServerInfo(UdpServerInfo udpServerInfo) {
        this.udpServerInfo = udpServerInfo;
    }

    public String getLog4jPropFilePath() {
        return log4jPropFilePath;
    }

    public void setLog4jPropFilePath(String log4jPropFilePath) {
        this.log4jPropFilePath = log4jPropFilePath;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RootConfig [testCase=");
		builder.append(testCase);
		builder.append(", sipInfo=");
		builder.append(sipInfo);
		builder.append(", sipStackFilePath=");
		builder.append(sipStackFilePath);
		builder.append(", dbInfo=");
		builder.append(dbInfo);
		builder.append(", udpServerInfo=");
		builder.append(udpServerInfo);
		builder.append(", log4jPropFilePath=");
		builder.append(log4jPropFilePath);
		builder.append("]");
		return builder.toString();
	}

    
}
