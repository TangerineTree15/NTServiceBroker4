package com.naturaltel.cache;

import java.util.Properties;

import com.naturaltel.config.entity.DBInfo;
import com.naturaltel.config.entity.SipInfo;
import com.naturaltel.config.entity.UdpServerInfo;
import com.naturaltel.config.entity.UserAgent;

public class Config {
	
	private static int testCase;

    private static SipInfo sipInfo;

    private static String sipStackFilePath;

    private static String log4jPropFilePath;

    private static DBInfo dbInfo;

    private static UdpServerInfo udpServerInfo;

    private static Properties sipStack;

    private static String xmlConfigPath;
    
    private static UserAgent simA;
    
    private static UserAgent simA1;
    
    private static UserAgent simB;
    
    private static UserAgent serviceBroker;
    
	public static int getTestCase() {
		return testCase;
	}

	public static void setTestCase(int testCase) {
		Config.testCase = testCase;
	}

	public static SipInfo getSipInfo() {
		return sipInfo;
	}

	public static String getSipStackFilePath() {
		return sipStackFilePath;
	}

	public static String getLog4jPropFilePath() {
		return log4jPropFilePath;
	}

	public static DBInfo getDbInfo() {
		return dbInfo;
	}

	public static UdpServerInfo getUdpServerInfo() {
		return udpServerInfo;
	}

	public static Properties getSipStack() {
		return sipStack;
	}

	public static String getXmlConfigPath() {
		return xmlConfigPath;
	}

	public static void setSipInfo(SipInfo sipInfo) {
		Config.sipInfo = sipInfo;
	}

	public static void setSipStackFilePath(String sipStackFilePath) {
		Config.sipStackFilePath = sipStackFilePath;
	}

	public static void setLog4jPropFilePath(String log4jPropFilePath) {
		Config.log4jPropFilePath = log4jPropFilePath;
	}

	public static void setDbInfo(DBInfo dbInfo) {
		Config.dbInfo = dbInfo;
	}

	public static void setUdpServerInfo(UdpServerInfo udpServerInfo) {
		Config.udpServerInfo = udpServerInfo;
	}

	public static void setSipStack(Properties sipStack) {
		Config.sipStack = sipStack;
	}

	public static void setXmlConfigPath(String xmlConfigPath) {
		Config.xmlConfigPath = xmlConfigPath;
	}

	public static UserAgent getSimA() {
		return simA;
	}

	public static void setSimA(UserAgent simA) {
		Config.simA = simA;
	}

	public static UserAgent getSimA1() {
		return simA1;
	}

	public static void setSimA1(UserAgent simA1) {
		Config.simA1 = simA1;
	}

	public static UserAgent getSimB() {
		return simB;
	}

	public static void setSimB(UserAgent simB) {
		Config.simB = simB;
	}

	public static UserAgent getServiceBroker() {
		return serviceBroker;
	}

	public static void setServiceBroker(UserAgent serviceBroker) {
		Config.serviceBroker = serviceBroker;
	}


}
