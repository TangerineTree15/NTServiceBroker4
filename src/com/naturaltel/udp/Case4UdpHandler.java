package com.naturaltel.udp;

import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.naturaltel.cache.Config;
import com.naturaltel.config.entity.SipInfo;
import com.naturaltel.config.entity.UdpServerInfo;
import com.naturaltel.config.entity.UserAgent;
import com.naturaltel.sip.SipManager;
import com.naturaltel.sip.entity.CallInfo;

public class Case4UdpHandler implements StanzaListener {
	
	private Logger logger = Logger.getLogger("stdout");
	private SipManager sipManager = null;
	private UdpManager udpManager = null;
	private HeaderFactory headerFactory = null;
    private AddressFactory addressFactory = null;
    private MessageFactory messageFactory = null;
    private UdpServerInfo udpServerInfo = null;
    private UserAgent sb = null;
    private SipInfo sipInfo = null;
	
	public Case4UdpHandler() {
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
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
		String caller = payload.getCaller();
		if(Config.getSimA().getName().equals(caller)) {
			logger.info("<<<<< [I.3] <<<<< [" + caller + "] Need recvice FCI<<<");
			
		} else if(Config.getSimB().getName().equals(caller)) {
			logger.info("<<<<< [I.9] <<<<< [" + caller + "] Need recvice FCI<<<");
            
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
		// TODO Auto-generated method stub
		String callId = payload.getCallId();
		String callee = payload.getCallee();
		String caller = payload.getCaller();
		if(Config.getSimA().getName().equals(caller)) {
			logger.info("<<<<< [B.4] <<<<< Release ");
			
		} else if(Config.getSimB().getName().equals(caller)) {
			logger.info("<<<<< [B.10] <<<<< release ");
			
		}
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
		try {
			if (sipManager.containsCall(callId)) {
				CallInfo callInfo = sipManager.getCallInfo(callId);
				String[] subSources = callInfo.getSource().split(":");
				String toHost = subSources[0];
				int toPort = Integer.parseInt(subSources[1]);
				Message message = callInfo.getMessage();
				if(message instanceof Request) {
					Request req = (Request)message;
					req = sipManager.marshal(req, callee, toHost, toPort);
					sipManager.sendRequest(req);
					if(Config.getSimA().getName().equals(caller)) {
						logger.info(">>>>> [B.5] >>>>> Recvice BYE from " + caller + "_IMS and forward BYE to " + caller + ":\n " + req);
						
					} else if(Config.getSimB().getName().equals(caller)) {
						logger.info(">>>>> [B.11] >>>>> Recvice BYE from " + caller + "_IMS and forward BYE to " + caller + ":\n " + req);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void doContinue(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		String callId = payload.getCallId();
		String callee = payload.getCallee();
		String caller = payload.getCaller();
		if(Config.getSimA().getName().equals(caller)) {
			logger.info("<<<<< [CR.8 / 10] <<<<< Continue");
			
		} else if(Config.getSimB().getName().equals(caller)) {
			logger.info("<<<<< [CRI.4] <<<<< Continue");
			
		}
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
		try {
			if (sipManager.containsCall(callId)) {
				CallInfo callInfo = sipManager.getCallInfo(callId);
				String[] subSources = callInfo.getSource().split(":");
				String toHost = subSources[0];
				int toPort = Integer.parseInt(subSources[1]);
				Message message = callInfo.getMessage();
				if(message instanceof Response) {
					Response resp = (Response)message;
					resp = sipManager.marshal(resp, callee, toHost, toPort, true);
					sipManager.sendResponse(resp);
					if(Config.getSimA().getName().equals(caller)) {
						logger.info(">>>>> [CR.9 / 11] >>>>> simA_IMS recvice OK and forward OK to simA:\n" + resp);
						
					} else if(Config.getSimB().getName().equals(caller)) {
						logger.info(">>>>> [CR.5] >>>>> simB recvice OK and forward OK to simB_IMS:\n " + resp);
						
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		
	}
	
	@Override
	public void doOther(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
		try {
			SipManager sipManager = SipManager.getInstance();
			Request req = sipManager.createRequest(Request.ACK, "callId", Config.getSimA1().getAddress(), Config.getSimA1().getPort(), "123");
			sipManager.sendRequest(req);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void doICA(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doCancel(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO
		
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
		// TODO
		return new StanzaFilter() {
			
			@Override
			public boolean accept(String srcAddress, int srcPort, UdpPayload payload) {
				// TODO
				if(Config.getTestCase() == 4) {
					return true;
				}
				return false;
			}
		};
	}


}
