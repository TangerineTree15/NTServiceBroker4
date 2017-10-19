package com.naturaltel.sip;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContentTypeHeader;
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
import com.naturaltel.sip.entity.CallInfo;
import com.naturaltel.udp.UdpManager;
import com.naturaltel.udp.UdpPayload;
import com.naturaltel.udp.UdpPayload.EventType;
import com.naturaltel.udp.UdpPayload.SubType;

public class SipHandler implements StanzaListener {

	private Logger logger = Logger.getLogger("stdout");
	private SipManager sipManager = null;
	private UdpManager udpManager = null;
	private HeaderFactory headerFactory = null;
    private AddressFactory addressFactory = null;
    private MessageFactory messageFactory = null;
    private UdpServerInfo udpServerInfo = null;
    private SipInfo sipInfo = null;
    private UserAgent sb = null;
	
	public SipHandler() {
		try {
			sipManager = SipManager.getInstance();
			udpManager = UdpManager.getInstance();
			headerFactory = sipManager.getHeaderFactory();
			addressFactory = sipManager.getAddressFactory();
			messageFactory = sipManager.getMessageFactory();
			udpServerInfo = Config.getUdpServerInfo();
			sipInfo = Config.getSipInfo();
            sb = Config.getServiceBroker();
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@Override
    public void doAck(ServerTransaction st, Request req, CallInfo callInfo) {
        logger.info("=====***** Get Ack *****=====");
        logger.info(req.toString());
    }

    @Override
    public void doBye(ServerTransaction st, Request req, CallInfo callInfo) {
        try {
            String caller = callInfo.getCaller();
            String callId = callInfo.getCallId();
            String source = callInfo.getSource();
            String callee = callInfo.getCallee();
            int serialNum = callInfo.getSerialNum();
            
            logger.info("<<<<< [" + ++serialNum + "] <<<<< =====XXXXX Get Bye Request XXXXX=====");
            logger.info(req.toString());
            
            String[] subSources = source.split(":");
            String toHost = subSources[0];
            int toPort = Integer.parseInt(subSources[1]);
            Request reqNew = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
            sipManager.sendRequest(reqNew);
            logger.info(">>>>> [" + ++serialNum + "] >>>>> Recieve BYE from " + caller + " and send ack back:\n" + reqNew);
            
            if (Config.getSimA().getName().equals(caller)
            		|| Config.getSimB().getName().equals(caller)) {
                String destAddress = udpServerInfo.getDestAddress();
                int destPort = udpServerInfo.getDestPort();
                UdpPayload payload = new UdpPayload();
                payload.setCallId(callId);
                payload.setCaller(caller);
                payload.setCallee(callee);
            	payload.setType(EventType.report);
            	payload.setSubType(SubType.oDisconnect);
                udpManager.sendPacket(destAddress, destPort, payload);
                logger.info(">>>>> [" + ++serialNum + "] >>>>> ERB: disconnected");
                
            } else if (Config.getSimA1().getName().equals(caller)) {
                reqNew = sipManager.marshal(req, callee, toHost, toPort);
                sipManager.sendRequest(reqNew);
                logger.info(">>>>> [" + ++serialNum + "] >>>>> Recieve BYE from " + caller + "_IMS and forward BYE to " + caller + ":\n" + reqNew);
                
            }
            
            callInfo.setSerialNum(serialNum);
            sipManager.setCallInfo(callInfo);
            
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void doCancel(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doInfo(ServerTransaction st, Request req, CallInfo callInfo) {
        ContentTypeHeader contentTypeHeader = (ContentTypeHeader) req.getHeader(ContentTypeHeader.NAME);
        logger.info("contentTypeHeader.getContentType(): " + contentTypeHeader.getContentType());
    }

    @Override
    public void doMessage(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doInvite(ServerTransaction st, Request req, CallInfo callInfo) throws Exception {
        try {
			String caller = callInfo.getCaller();
			String callId = callInfo.getCallId();
			String callee = callInfo.getCallee();
			String source = callInfo.getSource();
			int serialNum = callInfo.getSerialNum();
			logger.info("<<<<< [" + ++serialNum + "] <<<<< Recieve Invite callInfo.toString():\n " + callInfo.toString());
			
			if(!callee.contains("404")) {
			    String destAddress = udpServerInfo.getDestAddress();
			    int destPort = udpServerInfo.getDestPort();
			    UdpPayload payload = new UdpPayload();
			    payload.setCallId(callId);
			    payload.setCaller(caller);
			    payload.setCallee(callee);
				payload.setType(EventType.init);
			    udpManager.sendPacket(destAddress, destPort, payload);
			    logger.info(">>>>> [" + ++serialNum + "] >>>>> [" + caller + "] Initial DP>>> ");
			    
			} else {
//			    String[] subSources = source.split(":");
//			    String toHost = subSources[0];
//			    int toPort = Integer.parseInt(subSources[1]);
				UserAgent ims = sipManager.getIMS(callee);
				String toHost = ims.getAddress();
				int toPort = ims.getPort();
			    req = sipManager.marshal(req, callee, toHost, toPort);
			    sipManager.sendRequest(req);
			    logger.info(">>>>> [" + ++serialNum + "] >>>>> [" + caller + "_IMS] forward invite to " + caller + ">>> ");
			    
			}
			
			callInfo.setSerialNum(serialNum);
			sipManager.setCallInfo(callInfo);
			
		} catch (Exception e) {
			logger.error("", e);
		}
    }

    @Override
    public void doNotify(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doOptions(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doPrack(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doPublish(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doRefer(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doRegister(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doSubscribe(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void doUpdate(ServerTransaction st, Request req, CallInfo callInfo) {

    }

    @Override
    public void onProvisional(ClientTransaction ct, Response resp, CallInfo callInfo) throws Exception {
        if (resp.getStatusCode() == Response.RINGING) {
            String caller = callInfo.getCaller();
            String source = callInfo.getSource();
            String callee = callInfo.getCallee();
            int serialNum = callInfo.getSerialNum();
            logger.info("<<<<< [" + ++serialNum + "] <<<<< =====$$$$$ Get Ring Response $$$$$=====");
            logger.info(resp.toString());
            
            String[] subSources = source.split(":");
            String toHost = subSources[0];
            int toPort = Integer.parseInt(subSources[1]);
            resp = sipManager.marshal(resp, callee, toHost, toPort, true);
            sipManager.sendResponse(resp);
            if (Config.getSimA().getName().equals(caller)) {
                logger.info(">>>>> [" + ++serialNum + "] >>>>> forward response to" + caller + ":\n " + resp);
                
            } else if (Config.getSimA1().getName().equals(caller)
            		|| Config.getSimB().getName().equals(caller)) {
                logger.info(">>>>> [" + ++serialNum + "] >>>>> forward response to " + caller + "_IMS:\n " + resp);
                
            }
            
            callInfo.setSerialNum(serialNum);
            sipManager.setCallInfo(callInfo);
        }
    }

    @Override
    public void onSuccess(ClientTransaction ct, Response resp, CallInfo callInfo) {
        try {
            final int statusCode = resp.getStatusCode();
            if (Response.OK == statusCode) {
                String caller = callInfo.getCaller();
                String callId = callInfo.getCallId();
                String source = callInfo.getSource();
                String callee = callInfo.getCallee();
                int serialNum = callInfo.getSerialNum();
                logger.info("<<<<< [" + ++serialNum + "] <<<<< =====$$$$$ Get OK Response $$$$$=====");
                logger.info(resp.toString());
                
                String[] subSources = source.split(":");
                String toHost = subSources[0];
                int toPort = Integer.parseInt(subSources[1]);
                Request req = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
                sipManager.sendRequest(req);
                logger.info(">>>>> [" + ++serialNum + "] >>>>> recieve OK from and send ack:\n " + req);
                
                String destAddress = udpServerInfo.getDestAddress();
                int destPort = udpServerInfo.getDestPort();
                UdpPayload payload = new UdpPayload();
                payload.setCallId(callId);
                payload.setCallee(callee);
                payload.setCaller(caller);
                payload.setType(EventType.report);
                payload.setSubType(SubType.oAnswer);
                udpManager.sendPacket(destAddress, destPort, payload);
                logger.info(">>>>> [" + ++serialNum + "] >>>>> ERB: answered");
                
                // these statements for forwarding ok response may be replaced to the method doContinue in UDP handler class
                resp = sipManager.marshal(resp, callee, toHost, toPort, true);
                sipManager.sendResponse(resp);
                logger.info(">>>>> [" + ++serialNum + "] >>>>> recieve OK from " + caller + " and forward OK to " + caller + "_IMS:\n " + resp);
                
                callInfo.setSerialNum(serialNum);
                sipManager.setCallInfo(callInfo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void onRedirection(ClientTransaction ct, Response resp, CallInfo callInfo) {

    }

    @Override
    public void onClientError(ClientTransaction ct, Response resp, CallInfo callInfo) {

    }

    @Override
    public void onServerError(ClientTransaction ct, Response resp, CallInfo callInfo) {

    }

    @Override
    public void onGlobalFailure(ClientTransaction ct, Response resp, CallInfo callInfo) {

    }

    @Override
    public StanzaFilter getFilter() {
        return new StanzaFilter() {

            @Override
            public boolean accept(Message message) {
                return true;
            }

        };
    }

}
