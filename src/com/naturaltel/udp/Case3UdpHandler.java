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

public class Case3UdpHandler implements StanzaListener {
	
	private Logger logger = Logger.getLogger("stdout");
	private SipManager sipManager = null;
	private UdpManager udpManager = null;
	private HeaderFactory headerFactory = null;
    private AddressFactory addressFactory = null;
    private MessageFactory messageFactory = null;
    private UdpServerInfo udpServerInfo = null;
    private UserAgent sb = null;
    private SipInfo sipInfo = null;

	public Case3UdpHandler() {
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
					logger.info(">>>>> [" + ++serialNum + "] >>>>> Invoie Out request for " + caller + "_IMS:\n " + req);
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
					logger.info(">>>>> [" + ++serialNum + "] >>>>> recieve BYE from " + caller + " and forward BYE to " + caller + "_IMS:\n " + req);
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
		//TODO
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
		logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
	}

	@Override
	public void doICA(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO
		try {
			String callId = payload.getCallId();
			String callee = payload.getCallee();
			String caller = payload.getCaller();
			CallInfo callInfo = sipManager.getCallInfo(callId);
			int serialNum = callInfo.getSerialNum();
			
			logger.info("<<<<< [" + ++serialNum + "] <<<<< [" + caller + "] recieve ICA<<<");
			logger.info("[ src = " + srcAddress + ":" + srcPort + " ] UDP payload: " + payload);
			
			if (sipManager.containsCall(callId)) {
				Message message = callInfo.getMessage();
				if(message instanceof Request) {
					Request req = (Request)message;
					
					String toHost = null;
					int toPort = 0;
					if(Config.getSimA().getMsisdn().equals(callee)) {
						toHost = Config.getSimA().getAddress();
						toPort = Config.getSimA().getPort();
						
					} else if(Config.getSimA1().getMsisdn().equals(callee)) {
						toHost = Config.getSimA1().getAddress();
						toPort = Config.getSimA1().getPort();
					}
					req = sipManager.marshal(req, callee, toHost, toPort);
					sipManager.sendRequest(req);
					logger.info(">>>>> [" + ++serialNum + "] >>>>> [" + caller + "] Invoie Out request>>>");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	@Override
	public void doCancel(String srcAddress, int srcPort, UdpPayload payload) {
		// TODO
		String callId = payload.getCallId();
		String callee = payload.getCallee();
		String caller = payload.getCaller();
		if(Config.getSimA().getName().equals(caller)) {
			if(Config.getSimA().getMsisdn().equals(callee)) {
				logger.info("<<<<< [B.10] <<<<< cancel ");
				
			} else if(Config.getSimA1().getMsisdn().equals(callee)) {
				logger.info("<<<<< [B.12] <<<<< cancel ");
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
						if(Config.getSimA().getMsisdn().equals(callee)) {
							logger.info(">>>>> [B.11] >>>>> recieve cancel from " + caller + "_IMS and forward BYE to " + caller + ":\n " + req);
						} else if(Config.getSimA1().getMsisdn().equals(callee)) {
							logger.info(">>>>> [B.13] >>>>> recieve cancel from " + caller + "_IMS and forward BYE to " + caller + ":\n " + req);
						}
						
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}	
		}
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
				if(Config.getTestCase() == 1 || Config.getTestCase() == 2) {
					return true;
				}
				return false;
			}
		};
	}


}
