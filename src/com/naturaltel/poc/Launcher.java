package com.naturaltel.poc;

import org.apache.log4j.Logger;

import com.naturaltel.cache.Config;
import com.naturaltel.config.ConfigLoader;
import com.naturaltel.sip.SipManager;
import com.naturaltel.udp.UdpManager;
import com.naturaltel.util.Utils;

public class Launcher {
    private static final Logger logger = Logger.getLogger("stdout");

    private SipManager sipManager = null;
    private UdpManager udpManager = null;
    Utils utils = null;

    private Launcher() {
        try {
        	utils = new Utils();
        	utils.inputTestCase();
        	
            ConfigLoader.loadConfiguration();
            logger.info("Init " + Config.getSipInfo().getName());
            udpManager = UdpManager.getInstance();
            udpManager.wireListeners();
            udpManager.start();
            sipManager = SipManager.getInstance();
            sipManager.wireListeners();
            sipManager.start();

            logger.info("=============================" + Config.getSipInfo().getName() + " is started==============================");

        } catch (Exception e) {
            logger.error(Config.getSipInfo().getName() + " is failed to start...", e);
            System.exit(1);
        }
    }

    public void execute() {
    }



    public static void main(String[] args) {
        String xmlConfigPath = null;
        if (args.length > 0) {
            xmlConfigPath = args[0].trim();
        }
        Config.setXmlConfigPath(xmlConfigPath);
        Launcher launcher = new Launcher();
        launcher.execute();
    }
}
