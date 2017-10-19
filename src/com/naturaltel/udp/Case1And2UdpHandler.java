package com.naturaltel.udp;

import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.apache.log4j.Logger;

import com.naturaltel.cache.Config;
import com.naturaltel.config.entity.SipInfo;
import com.naturaltel.config.entity.UdpServerInfo;
import com.naturaltel.config.entity.UserAgent;
import com.naturaltel.sip.SipManager;
import com.naturaltel.sip.entity.CallInfo;

public class Case1And2UdpHandler implements StanzaListener {
	
	private Logger logger = Logger.getLogger("stdout");
	private SipManager sipManager = null;
	private UdpManager udpManager = null;
	private HeaderFactory headerFactory = null;
    private AddressFactory addressFactory = null;
    private MessageFactory messageFactory = null;
    private UdpServerInfo udpServerInfo = null;
    private UserAgent sb = null;
    private SipInfo sipInfo = null;

	public Case1And2UdpHandler() {
		try {
			sipManager = SipManager.getInstance();
			udpManager = UdpManager.getInstance();
			headerFactory = sipManager.getHeaderFactory();
			addressFactory = sipManager.getAddressFactory();
			messageFactory = sipManager.getMessageFactory();
			udpServerInfo = Config.getUdpServerInfo();
			sb = Config.getServiceBroker();
			sipInfo = Config.getSipInfo();
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
    
	@Override
	public void doInfo(String srcAddress, int srcPort, UdpPayload payload) {
		//TODO
		try {
			String callId = payload.getCallId();
			String callee = payload.getCallee();
			String caller = payload.getCaller();
			CallInfo callInfo = sipManager.getCallInfo(callId);
			int serialNum = callInfo.getSerialNum();
			
			logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
			logger.info("<<<<< [" + ++serialNum + "] <<<<< [" + caller + "] recieve FCI<<<");
			
			if (sipManager.containsCall(callId)) {
				String[] subSources = callInfo.getSource().split(":");
				String toHost = subSources[0];
				int toPort = Integer.parseInt(subSources[1]);
				Request newReq = sipManager.createInfoRequest(callId, callee, toHost, toPort);
				sipManager.sendRequest(newReq);
				logger.info(">>>>> [" + ++serialNum + "] >>>>> Info to " + caller + ":\n " + newReq);
			}
			
			callInfo.setSerialNum(serialNum);
            sipManager.setCallInfo(callInfo);
            
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void doReport(String srcAddress, int srcPort, UdpPayload payload) {
		//TODO
		String caller = payload.getCaller();
		String callId = payload.getCallId();
		CallInfo callInfo = sipManager.getCallInfo(callId);
		int serialNum = callInfo.getSerialNum();
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
		logger.info("<<<<< [" + ++serialNum + "] <<<<< [" + caller + "] recieve BCSM<<<");
		
		callInfo.setSerialNum(serialNum);
        sipManager.setCallInfo(callInfo);
	}

	@Override
	public void doConnect(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO
		try {
			String callId = payload.getCallId();
			String callee = payload.getCallee();
			String caller = payload.getCaller();
			CallInfo callInfo = sipManager.getCallInfo(callId);
			int serialNum = callInfo.getSerialNum();
			
			logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
			logger.info("<<<<< [" + ++serialNum + "] <<<<< [" + caller + "] recieve Connect<<<");
			logger.debug("sipManager.containsCall(callId): " + sipManager.containsCall(callId));
			if (sipManager.containsCall(callId)) {
				String[] subSources = callInfo.getSource().split(":");
				String toHost = subSources[0];
				int toPort = Integer.parseInt(subSources[1]);
				Message message = callInfo.getMessage();
				if(message instanceof Request) {
					Request req = (Request)message;
					req = sipManager.marshal(req, callee, toHost, toPort);
					sipManager.sendRequest(req);
					logger.info(">>>>> [" + ++serialNum + "] >>>>> forward request to " + caller + "_IMS:\n " + req);
				}
			}
			
			callInfo.setSerialNum(serialNum);
	        sipManager.setCallInfo(callInfo);
	        
		} catch (Exception e) {
			logger.error("", e);
		}
		
	}

	@Override
	public void doRelease(String srcAddress, int srcPort, UdpPayload payload) {
		//TODO
		try {
			String callId = payload.getCallId();
			String callee = payload.getCallee();
			String caller = payload.getCaller();
			CallInfo callInfo = sipManager.getCallInfo(callId);
			int serialNum = callInfo.getSerialNum();
			
			logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
			logger.info("<<<<< [" + ++serialNum + "] <<<<< Release ");
			
			if (sipManager.containsCall(callId)) {
				String[] subSources = callInfo.getSource().split(":");
				String toHost = subSources[0];
				int toPort = Integer.parseInt(subSources[1]);
				Message message = callInfo.getMessage();
				if(message instanceof Request) {
					Request req = (Request)message;
					req = sipManager.marshal(req, callee, toHost, toPort);
					sipManager.sendRequest(req);
					if(Config.getSimA().getName().equals(caller)) {
						logger.info(">>>>> [" + ++serialNum + "] >>>>> recieve BYE from " + caller + " and forward BYE to " + caller + "_IMS:\n " + req);
						
					} else if(Config.getSimB().getName().equals(caller)) {
						logger.info(">>>>> [" + ++serialNum + "] >>>>> recieve BYE from " + caller + "_IMS and forward BYE to " + caller + ":\n " + req);
					}
				}
			}
			
			callInfo.setSerialNum(serialNum);
	        sipManager.setCallInfo(callInfo);
	        
		} catch (Exception e) {
			logger.error("", e);
		}
		
	}

	@Override
	public void doContinue(String srcAddress, int srcPort, UdpPayload payload) {
		try {
			String callId = payload.getCallId();
			CallInfo callInfo = sipManager.getCallInfo(callId);
			int serialNum = callInfo.getSerialNum();
			
			logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
			logger.info("<<<<< [" + ++serialNum + "] <<<<< Continue");
			
			callInfo.setSerialNum(serialNum);
	        sipManager.setCallInfo(callInfo);
	        
		} catch (Exception e) {
			logger.error("", e);
		}
		
	}
	
	@Override
	public void doOther(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}

	@Override
	public void doICA(String srcAddress, int srcPort, UdpPayload payload) {
		
	}

	@Override
	public void doCancel(String srcAddress, int srcPort, UdpPayload payload) {
		
	}

	@Override
	public void doResetTimer(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO
		String caller = payload.getCaller();
		String callId = payload.getCallId();
		CallInfo callInfo = sipManager.getCallInfo(callId);
		int serialNum = callInfo.getSerialNum();
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
		logger.info("<<<<< [" + ++serialNum + "] <<<<< [" + caller + "] resetTimer<<<");
		
		callInfo.setSerialNum(serialNum);
        sipManager.setCallInfo(callInfo);
		
	}
	
	@Override
	public StanzaFilter getFilter() {
		return new StanzaFilter() {
			
			@Override
			public boolean accept(String srcAddress, int srcPort, UdpPayload payload) {
				// TODO
				if(Config.getTestCase() == 1 || Config.getTestCase() == 2) {
					return true;
				}
				return false;
			}
		};
	}


}
