package com.naturaltel.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.PropertyConfigurator;

import com.naturaltel.cache.Config;
import com.naturaltel.config.entity.RootConfig;
import com.naturaltel.config.entity.SipInfo;
import com.naturaltel.config.entity.UserAgent;

public class ConfigLoader {

    //For test
    public static void main(String[] args) throws Exception {
        ConfigLoader.loadConfiguration();
    }

    private static final String DEFAULT_XML_FILE_PATH = "conf/config.xml";
    private static final String DEFAULT_SIPSTACK_PROP_FILE_PATH = "conf/sipStack.properties";
    private static final String DEFAULT_LOG4J_PROP_FILE_PATH = "conf/log4j.properties";

    public static void loadConfiguration() throws Exception {
        String path = null;
        InputStream is = null;
        try {
            if (Config.getXmlConfigPath() != null && !"".equals(Config.getXmlConfigPath())) {
                path = Config.getXmlConfigPath();
                is = new FileInputStream(new File(path));
            } else {
                path = DEFAULT_XML_FILE_PATH;
                is = ConfigLoader.class.getClassLoader().getResourceAsStream(path);
            }
            System.out.println("path : " + path);
            System.out.println("is: " + is);

            JAXBContext jaxbContext = JAXBContext.newInstance(RootConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            RootConfig rootConfig = (RootConfig) unmarshaller.unmarshal(is);
            System.out.println(rootConfig);
            if(Config.getTestCase() == 0) {
            	Config.setTestCase(rootConfig.getTestCase());
            }
            SipInfo sipInfo = rootConfig.getSipInfo();
            for(UserAgent ua : sipInfo.getUserAgents()) {
            	String uaName = ua.getName();
            	if("simA".equals(uaName)) {
            		Config.setSimA(ua);
            	} else if("simA1".equals(uaName)) {
            		Config.setSimA1(ua);
            	} else if("simB".equals(uaName)) {
            		Config.setSimB(ua);
            	} else if("sb".equals(uaName)) {
            		Config.setServiceBroker(ua);
            	}
            	if(sipInfo.getName() != null && sipInfo.getName().equals(uaName)) {
            		sipInfo.setAddress(ua.getAddress());
            		sipInfo.setPort(ua.getPort());
            		sipInfo.setTransport(ua.getTransport());
            	}
            }
            Config.setSipInfo(sipInfo);
            Config.setDbInfo(rootConfig.getDbInfo());
            Config.setUdpServerInfo(rootConfig.getUdpServerInfo());
            Config.setSipStackFilePath(rootConfig.getSipStackFilePath());
            Config.setLog4jPropFilePath(rootConfig.getLog4jPropFilePath());
            is.close();

            //load sip stack
            Properties sipStack = new Properties();
            String sipStackFilePath = rootConfig.getSipStackFilePath();
            if (sipStackFilePath != null && !"".equals(sipStackFilePath)) {
                path = rootConfig.getSipStackFilePath();
                is = new FileInputStream(new File(path));
            } else {
                path = DEFAULT_SIPSTACK_PROP_FILE_PATH;
                is = ConfigLoader.class.getClassLoader().getResourceAsStream(path);
            }
            sipStack.load(is);
            Config.setSipStack(sipStack);
            is.close();

            //initialize log4j
            Properties log4jProp = new Properties();
            String log4jPropFilePath = rootConfig.getLog4jPropFilePath();
            if (rootConfig.getLog4jPropFilePath() != null && !"".equals(log4jPropFilePath)) {
                path = rootConfig.getLog4jPropFilePath();
                is = new FileInputStream(new File(path));
            } else {
                path = DEFAULT_LOG4J_PROP_FILE_PATH;
                is = ConfigLoader.class.getClassLoader().getResourceAsStream(path);
            }
            log4jProp.load(is);
            PropertyConfigurator.configure(log4jProp);

        } catch (JAXBException e) {

            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
