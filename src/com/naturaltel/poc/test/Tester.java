package com.naturaltel.poc.test;

import com.naturaltel.cache.Config;
import com.naturaltel.config.ConfigLoader;
import com.naturaltel.udp.UdpManager;
import com.naturaltel.udp.UdpPayload;
import com.naturaltel.udp.UdpPayload.EventType;

public class Tester {
	public static void main(String[] args) {
		try {
			ConfigLoader.loadConfiguration();
			Config.getUdpServerInfo().setPort(8618);
			UdpManager udpManager = UdpManager.getInstance();
			UdpPayload payload = new UdpPayload();
			payload.setCaller("udpTester");
			payload.setCallee("SB");
			payload.setCallId("123321");
			payload.setType(EventType.other);
			udpManager.start();
			udpManager.sendPacket("192.168.10.28", 8617, payload);
			
			payload.setType(EventType.connect);
			udpManager.sendPacket("192.168.10.28", 8617, payload);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
