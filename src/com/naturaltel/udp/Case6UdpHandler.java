package com.naturaltel.udp;

import org.apache.log4j.Logger;

import com.naturaltel.cache.Config;

public class Case6UdpHandler implements StanzaListener {
	
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void doInfo(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}

	@Override
	public void doReport(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}

	@Override
	public void doConnect(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}

	@Override
	public void doRelease(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}

	@Override
	public void doContinue(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void doOther(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}


	@Override
	public void doICA(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doCancel(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doResetTimer(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public StanzaFilter getFilter() {
		// TODO
		return new StanzaFilter() {
			
			@Override
			public boolean accept(String srcAddress, int srcPort, UdpPayload payload) {
				// TODO
				if(Config.getTestCase() == 6) {
					return true;
				}
				return false;
			}
		};
	}

}
