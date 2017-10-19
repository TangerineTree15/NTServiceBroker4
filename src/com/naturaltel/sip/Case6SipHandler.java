package com.naturaltel.sip;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
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

import gov.nist.javax.sip.address.SipUri;

public class Case6SipHandler implements StanzaListener {

	private Logger logger = Logger.getLogger("stdout");
	private SipManager sipManager = null;
	private UdpManager udpManager = null;
	private HeaderFactory headerFactory = null;
    private AddressFactory addressFactory = null;
    private MessageFactory messageFactory = null;
    private UdpServerInfo udpServerInfo = null;
    private SipInfo sipInfo = null;
    private UserAgent sb = null;
	
	public Case6SipHandler() {
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
        SipInfo sipInfo = Config.getSipInfo();
        UserAgent sb = Config.getServiceBroker();
        try {
            String caller = callInfo.getCaller();
            String callId = callInfo.getCallId();
            String source = callInfo.getSource();
            String callee = callInfo.getCallee();
            UserAgent uac = null;
            if ("simB".equals(caller)) {
                logger.info("<<<<< [B.1] <<<<< =====XXXXX Get Bye Request XXXXX=====");
                logger.info(req.toString());
                uac  = Config.getSimB();
                String[] subSources = source.split(":");
                String toHost = subSources[0];
                int toPort = Integer.parseInt(subSources[1]);
                Request reqNew = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
                sipManager.sendRequest(reqNew);
                logger.info(">>>>> [B.2] >>>>> Recvice BYE from simB and send ack back:\n" + reqNew);
                logger.info(">>>>> [B.3] >>>>> ERB: disconnected");
                logger.info("<<<<< [B.4] <<<<< Release");
                reqNew = sipManager.marshal(req, callee, toHost, toPort);
                sipManager.sendRequest(reqNew);
                logger.info(">>>>> [B.5] >>>>> Recvice BYE from simB and forward BYE to simB_IMS:\n" + reqNew);
            } else if ("simA".equals(caller)) {
                logger.info("<<<<< [B.7] <<<<< =====XXXXX Get Bye Request XXXXX=====");
                logger.info(req.toString());
                uac  = Config.getSimA();
                String[] subSources = source.split(":");
                String toHost = subSources[0];
                int toPort = Integer.parseInt(subSources[1]);
                Request reqNew = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
                sipManager.sendRequest(reqNew);
                logger.info(">>>>> [B.8] >>>>> Recvice BYE from simA_IMS and send ack back:\n" + reqNew);
                logger.info(">>>>> [B.9] >>>>> ERB: disconnected");
                logger.info("<<<<< [B.10] <<<<< Release");
                reqNew = sipManager.marshal(req, callee, uac.getAddress(), uac.getPort());
                sipManager.sendRequest(reqNew);
                logger.info(">>>>> [B.11] >>>>> Recvice BYE from simA_IMS and forward BYE to simA:\n" + reqNew);
                UserAgent uac1 = Config.getSimA1();
                reqNew = sipManager.marshal(req, callee, uac1.getAddress(), uac1.getPort());
                sipManager.sendRequest(reqNew);
                logger.info(">>>>> [B.12] >>>>> Recvice BYE from simA_IMS and forward BYE to simA1:\n" + reqNew);
//                reqNew = sipManager.marshal(req, callee, toHost, toPort);
//                sipManager.sendRequest(reqNew);
//                logger.info(">>>>> [B.9] >>>>> Recvice BYE from simA_IMS and forward BYE to simA1_IMS:\n" + reqNew);
            } else if ("simA1".equals(caller)) {
//                logger.info("<<<<< [B.11] <<<<< =====XXXXX Get Bye Request XXXXX=====");
//                logger.info(req.toString());
//                uac  = Config.getSimA1();
//                String[] subSources = source.split(":");
//                String toHost = subSources[0];
//                int toPort = Integer.parseInt(subSources[1]);
//                Request reqNew = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
//                sipManager.sendRequest(reqNew);
//                logger.info(">>>>> [B.12] >>>>> Recvice BYE from simB_IMS and send ack back:\n" + reqNew);
//                logger.info(">>>>> [B.9] >>>>> ERB disconnected");
//                logger.info("<<<<< [B.10] <<<<< release ");
//                reqNew = sipManager.marshal(req, callee, toHost, toPort);
//                sipManager.sendRequest(reqNew);
//                logger.info(">>>>> [B.11] >>>>> Recvice BYE from simB_IMS and forward BYE to simB:" + reqNew);
            } else {
                logger.info("=====XXXXX Get Bye Request XXXXX=====");
                logger.info(req.toString());
            }
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
        String caller = callInfo.getCaller();
        String callId = callInfo.getCallId();
//        if ("0936010123".equals(caller) || "100".equals(caller) || "simA".equals(caller)) {
        if ("simB".equals(caller)) {
            logger.info("<<<<< [1] <<<<< Recvice Invite callInfo.toString():\n " + callInfo.toString());
            logger.info(">>>>> [I.2] >>>>> Initial DP>>> ");
            logger.info("<<<<< [I.3] <<<<< Need recvice FCI<<<");
            logger.info("<<<<< [I.4] <<<<< Need recvice BCSM<<<");
            logger.info("<<<<< [I.5] <<<<< Need recvice Connect[4040936010124]<<<");
            String callee = callInfo.getCallee();
            if ("101".equals(callee)) {
                callee = "4040936010124";
            }

            SipInfo sipInfo = Config.getSipInfo();
            UserAgent sb = Config.getServiceBroker();
            UserAgent uac = Config.getSimB();
            String viaSource = callInfo.getSource();
            logger.debug("caller: " + caller);
            logger.debug("viaSource: " + viaSource);

            if (sipManager.containsCall(callId)) {
                logger.debug(String.format("containsCall: %s", callId));
                logger.info("callId: " + callId + "/ requestUri: " + callee);
                SipUri toUri = new SipUri();
                toUri.setUser(callee);
                toUri.setHost(uac.getAddress());
                toUri.setPort(uac.getPort());
                SipUri fromUri = new SipUri();
                fromUri.setUser("SB");
                fromUri.setHost(sipInfo.getAddress());
                fromUri.setPort(sipInfo.getPort());
                req.setRequestURI(toUri);
                ViaHeader viaHeader = headerFactory.createViaHeader(sipInfo.getAddress(),
                        sipInfo.getPort(), sipInfo.getTransport(), null);
                req.addHeader(viaHeader);
                Address contactAddress = addressFactory.createAddress(fromUri);
                ContactHeader ccontact = headerFactory.createContactHeader(contactAddress);
                req.setHeader(ccontact);
                ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri),
                        null);
                req.setHeader(toHeader);
                // first requestÂàªø½X«á­×§ï
//                String callIdPlusSrc = String.format("%s:%s", callId, viaSource);
                String callIdPlusSrc = String.format("%s:%s", callId, caller);
                logger.info(String.format("XXXXX Get request for callId+Source=%s", callIdPlusSrc));
                Request tmp = SipManager.getInstance().getReq(callIdPlusSrc);
                if (tmp != null) {
                    tmp.setHeader(toHeader);
                    SipManager.getInstance().resetReq(callIdPlusSrc, tmp);
                }
                FromHeader fromHeader = headerFactory
                        .createFromHeader(addressFactory.createAddress(fromUri), "tag");
                req.setHeader(fromHeader);
                sipManager.sendRequest(req);
                logger.info(">>>>> [6] >>>>> Invoie Out request for simB_IMS:\n " + req);
            }
//        } else if ("0936030124".equals(caller) || "101".equals(caller) || "simA1".equals(caller) || "4040936010124".equals(caller)) {
        } else if ("simA1".equals(caller)) {
            logger.info("<<<<< [7] <<<<< Recvice Invite callInfo.toString():\n " + callInfo.toString());
            logger.info(">>>>> [I.8] >>>>> Initial DP>>> ");
            logger.info("<<<<< [I.9] <<<<< Need recvice FCI<<<");
            logger.info("<<<<< [I.10] <<<<< Need recvice BCSM<<<");
            logger.info("<<<<< [I.11] <<<<< Need recvice Connect[4040936010124]<<<");
            String callee = callInfo.getCallee();
            if ("101".equals(callee)) {
                callee = "4040936010124";
            }
            SipInfo sipInfo = Config.getSipInfo();
            UserAgent uac = Config.getSimA1();
            String viaSource = callInfo.getSource();
            logger.debug("caller: " + caller);
            logger.debug("viaSource: " + viaSource);

            if (sipManager.containsCall(callId)) {
                logger.debug(String.format("containsCall: %s", callId));
                logger.debug("callId: " + callId + "/ requestUri: " + callee);
                SipUri toUri = new SipUri();
                toUri.setUser(callee);
                toUri.setHost(uac.getAddress());
                toUri.setPort(uac.getPort());
                SipUri fromUri = new SipUri();
                fromUri.setUser("SB");
                fromUri.setHost(sipInfo.getAddress());
                fromUri.setPort(sipInfo.getPort());
                req.setRequestURI(toUri);

                ViaHeader viaHeader = headerFactory.createViaHeader(sipInfo.getAddress(),
                        sipInfo.getPort(), sipInfo.getTransport(), null);
                req.addHeader(viaHeader);
                Address contactAddress = addressFactory.createAddress(fromUri);
                ContactHeader ccontact = headerFactory.createContactHeader(contactAddress);
                req.setHeader(ccontact);
                ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri),
                        null);
                req.setHeader(toHeader);
                FromHeader fromHeader = headerFactory
                        .createFromHeader(addressFactory.createAddress(fromUri), "tag");
                req.setHeader(fromHeader);
                sipManager.sendRequest(req);
                logger.info(">>>>> [12] >>>>> Invoie Out request for simA1_IMS:\n " + req);
//                logger.info("<<<<< [I.13] <<<<< Request Report BCSM Event");
//                logger.info("<<<<< [I.14] <<<<< ICA 4040936020125");
//                UserAgent uac1 = Config.getSimA1();
//                SipUri toUri1 = new SipUri();
//                toUri1.setUser("4040936020125");
//                toUri1.setHost(uac1.getAddress());
//                toUri1.setPort(uac1.getPort());
//                req.setRequestURI(toUri1);
//                ToHeader toHeader1 = headerFactory.createToHeader(addressFactory.createAddress(toUri1), null);
//                req.setHeader(toHeader1);
//                sipManager.sendRequest(req);
//                logger.info(">>>>> [15] >>>>> Invoie Out request for simA_IMS:\n " + req);
            }
        } else if ("0936030125".equals(caller) || "simA1".equals(caller)) {
            logger.error("NGNGNGNGNG NO simA1 invite NGNGNGNGNG");
//            logger.info("<<<<< [7] <<<<< Recvice Invite callInfo.toString():\n " + callInfo.toString());
//            logger.info(">>>>> [I.8] >>>>> [B1] Initial DP>>> ");
//            logger.info("<<<<< [I.9] <<<<< [B1] Need recvice FCI<<<");
//            logger.info("<<<<< [I.10] <<<<< [B1] Need recvice BCSM<<<");
//            logger.info("<<<<< [I.11] <<<<< [B1] Need recvice Connect<<<");
//            String callee = callInfo.getCallee();
//            SipInfo sipInfo = Config.getSipInfo();
//            UserAgent uac = Config.getSimB();
//            String viaSource = callInfo.getSource();
//            logger.debug("caller: " + caller);
//            logger.debug("viaSource: " + viaSource);
//
//            if (sipManager.containsCall(callId)) {
//                logger.debug(String.format("containsCall: %s", callId));
//                logger.debug("callId: " + callId + "/ requestUri: " + callee);
//                SipUri toUri = new SipUri();
//                toUri.setUser(callee);
//                toUri.setHost(uac.getAddress());
//                toUri.setPort(uac.getPort());
//                SipUri fromUri = new SipUri();
//                fromUri.setUser("SB");
//                fromUri.setHost(sipInfo.getAddress());
//                fromUri.setPort(sipInfo.getPort());
//                req.setRequestURI(toUri);
//
//                ViaHeader viaHeader = headerFactory.createViaHeader(sipInfo.getAddress(),
//                        sipInfo.getPort(), sipInfo.getTransport(), null);
//                req.addHeader(viaHeader);
//                Address contactAddress = addressFactory.createAddress(fromUri);
//                ContactHeader ccontact = headerFactory.createContactHeader(contactAddress);
//                req.setHeader(ccontact);
//                ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri),
//                        null);
//                req.setHeader(toHeader);
//                FromHeader fromHeader = headerFactory
//                        .createFromHeader(addressFactory.createAddress(fromUri), "tag");
//                req.setHeader(fromHeader);
//                sipManager.sendRequest(req);
//                logger.info(">>>>> [12] >>>>> Invoie Out request for simB_IMS:\n " + req);
//            }
        } else {
            logger.info("<<<<< Recvice Invite callInfo.toString(): " + callInfo.toString());
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
            String callId = callInfo.getCallId();
            SipInfo sipInfo = Config.getSipInfo();
            SipUri fromUri = new SipUri();
            fromUri.setUser("SB");
            fromUri.setHost(sipInfo.getAddress());
            fromUri.setPort(sipInfo.getPort());
            Address contactAddress = addressFactory.createAddress(fromUri);
            ContactHeader ccontact = headerFactory.createContactHeader(contactAddress);
            if ("simB".equals(caller)) {
                logger.info("<<<<< [19] <<<<< =====$$$$$ Get Response $$$$$===== Wrong Ring");
                logger.info(resp.toString());
//                logger.info(">>>>> [25] >>>>> ERB term size");
//                if (sipManager.containsCall(callId)) {
//                    resp.removeFirst(ViaHeader.NAME);
//                    resp.setHeader(ccontact);
//                    sipManager.sendResponse(resp);
//                    logger.info(">>>>> [26] >>>>> Response out for simB:\n " + resp);
//                    String callIdPlusSrc = String.format("%s:%s", callId, caller);
//                    Request newReq = createInfoRequest(SipManager.getInstance().getReq(callIdPlusSrc));
//                    SipManager.getInstance().sendRequest(newReq);
//                    logger.info(">>>>> [27] >>>>> Response out for simB:\n " + newReq);
//                }
            } else if ("simA".equals(caller)) {
                logger.info("<<<<< [19] <<<<< =====$$$$$ Get Response $$$$$=====");
                logger.info(resp.toString());
                logger.info(">>>>> [20] >>>>> ERB term size");
                String viaSource = callInfo.getSource();
                logger.debug("viaSource: " + viaSource);

                if (sipManager.containsCall(callId)) {
                    resp.removeFirst(ViaHeader.NAME);
                    resp.setHeader(ccontact);
                    sipManager.sendResponse(resp);
                    logger.info(">>>>> [21] >>>>>  Response out for simA:\n " + resp);
//                    String callIdPlusSrc = String.format("%s:%s", callId, caller);
//                    Request newReq = createInfoRequest(callId, callInfo.getCallee(), SipManager.getInstance().getReq(callIdPlusSrc));
//                    SipManager.getInstance().sendRequest(newReq);
                    UserAgent uaa = Config.getSimA();
                    Request newReq = SipManager.getInstance().createRequest(Request.INFO, callId, uaa.getAddress(), uaa.getPort(), callInfo.getCallee());
                    ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "cs1+");
                    contentTypeHeader.setParameter("encoding", "UTF-8");
                    newReq.addHeader(contentTypeHeader);
                    SipManager.getInstance().sendRequest(newReq);
                    logger.info(">>>>> [22] >>>>> Info out for simA:\n" + newReq);
                    UserAgent ua = Config.getSimB();
                    Request req2B = SipManager.getInstance().createRequest(Request.INFO, callId, ua.getAddress(), ua.getPort(), callInfo.getCallee());
//                    ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "cs1+");
//                    contentTypeHeader.setParameter("encoding", "UTF-8");
                    req2B.addHeader(contentTypeHeader);
                    SipManager.getInstance().sendRequest(req2B);
                    logger.info(">>>>> [23] >>>>> Info out for simB:\n" + req2B);
                }
            } else if ("simA1".equals(caller)) {
                logger.info("<<<<< [19] <<<<< =====$$$$$ Get Response $$$$$===== Wrong Ring");
                logger.info(resp.toString());
//                logger.info(">>>>> [21] >>>>> ERB term size");
//                String viaSource = callInfo.getSource();
//                logger.debug("viaSource: " + viaSource);
//
//                if (sipManager.containsCall(callId)) {
//                    resp.removeFirst(ViaHeader.NAME);
//                    resp.setHeader(ccontact);
//                    sipManager.sendResponse(resp);
//                    logger.info(">>>>> [22] >>>>>>Response out for simA1:\n " + resp);
//                    String callIdPlusSrc = String.format("%s:%s", callId, caller);
//                    Request newReq = createInfoRequest(SipManager.getInstance().getReq(callIdPlusSrc));
//                    SipManager.getInstance().sendRequest(newReq);
//                    logger.info(">>>>> [23] >>>>> out for simA1:\n" + resp);
//                }
            } else {
                logger.info("=====$$$$$ Get Response $$$$$=====");
                logger.info(resp.toString());
            }
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
                UserAgent uac = null;
                if ("simA".equals(caller)) {
                    logger.info("<<<<< [CR.A.1] <<<<< =====$$$$$ Get OK Response $$$$$=====");
                    logger.info(resp.toString());
                    uac  = Config.getSimA();
                    String[] subSources = source.split(":");
                    String toHost = subSources[0];
                    int toPort = Integer.parseInt(subSources[1]);
                    Request req = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
                    sipManager.sendRequest(req);
                    logger.info(">>>>> [CR.A.2] >>>>> simA_IMS recvice OK and send ack:\n " + req);
                    logger.info(">>>>> [CR.A.3] >>>>> ERB: answereed");
                    logger.info("<<<<< [CR.A.4] <<<<< Continue");
                    resp = sipManager.marshal(resp, callee, uac.getAddress(), uac.getPort(), true);
                    sipManager.sendResponse(resp);
                    logger.info(">>>>> [CR.A.5] >>>>> simA_IMS recvice OK and forward OK to simA:\n" + resp);
                } else if ("simA1".equals(caller)) {
                    logger.info("<<<<< [CR.A1.1] <<<<< =====$$$$$ Get OK Response $$$$$=====");
                    logger.info(resp.toString());
                    uac  = Config.getSimA1();
                    String[] subSources = source.split(":");
                    String toHost = subSources[0];
                    int toPort = Integer.parseInt(subSources[1]);
                    Request req = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
                    sipManager.sendRequest(req);
                    logger.info(">>>>> [CR.A1.2] >>>>> simA1 recvice OK and send ack:\n " + req);
                    resp = sipManager.marshal(resp, callee, uac.getAddress(), uac.getPort(), true);
                    sipManager.sendResponse(resp);
                    logger.info(">>>>> [CR.A1.3] >>>>> simA1 recvice OK and forward OK to simA1_IMS:\n " + resp);
                } else if ("simB".equals(caller)) {
                    logger.info("<<<<< [CR.B.1] <<<<<< =====$$$$$ Get OK Response $$$$$=====");
                    logger.info(resp.toString());
                    uac  = Config.getSimB();
                    String[] subSources = source.split(":");
                    String toHost = subSources[0];
                    int toPort = Integer.parseInt(subSources[1]);
                    Request req = sipManager.createRequest(Request.ACK, callId, toHost, toPort, callee);
                    sipManager.sendRequest(req);
                    logger.info(">>>>> [CR.B.2] >>>>> simB recvice OK and send ack:\n " + req);
                    logger.info(">>>>> [CRI.B.3] >>>>> ERB answreed");
                    logger.info("<<<<< [CRI.B.4] <<<<< Continue");
                    resp = sipManager.marshal(resp, callee, uac.getAddress(), uac.getPort(), true);
                    sipManager.sendResponse(resp);
                    logger.info(">>>>> [CR.B.5] >>>>> simB recvice OK and forward OK to simB_IMS:\n " + resp);
                } else {
                    logger.info("=====$$$$$ Get OK Response $$$$$=====");
                    logger.info(resp.toString());
                }
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
        try {
            final int statusCode = resp.getStatusCode();
            if (Response.BUSY_HERE == statusCode || Response.TEMPORARILY_UNAVAILABLE == statusCode) {
                logger.info("<<<<< [13] <<<<< ====== Get Error Response ======");
                logger.info(resp.toString());
                logger.info(">>>>> [14] >>>>> ERB: term tCalledPartyBusy");
                logger.info("<<<<< [15] <<<<< Split Leg");
                logger.info("<<<<< [16] <<<<< Request Report BCSM Event");
                logger.info("<<<<< [17] <<<<< ICA 4040936020123");
                String callId = callInfo.getCallId();
                String callee = callInfo.getCallee();
                String callIdCaller = String.format("%s:simA1", callId);
                Request orgReq = SipManager.getInstance().getReq(callIdCaller);
//                logger.info("orgReq="+orgReq);
                @SuppressWarnings("unchecked")
                ListIterator<ViaHeader> list = orgReq.getHeaders(ViaHeader.NAME);
                UserAgent ua = Config.getSimA();
//                Request req = SipManager.getInstance().createRequest(Request.INVITE, callInfo.getCallId(), ua.getAddress(),
//                        ua.getPort(), callInfo.getCallee());
                SipInfo sipInfo = Config.getSipInfo();
                String fromHost = sipInfo.getAddress();
                int fromPort = sipInfo.getPort();
                String num = callee;
                SipUri toUri = new SipUri();
                toUri.setUser(num);
                toUri.setHost(ua.getAddress());
                toUri.setPort(ua.getPort());
                SipUri fromUri = new SipUri();
                fromUri.setUser(sipInfo.getName());                                                                                                                                                                                                          
                fromUri.setHost(fromHost);
                fromUri.setPort(fromPort);
                
                CallIdHeader callIdHeader = SipManager.getInstance().getNewCallIdHeader();
                if(callId != null) {
                    callIdHeader.setCallId(callId);
                }
                long cseq = 10;
                CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(cseq, Request.INVITE);
                FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromUri), "tag");
                ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri), null);
                List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
//                ViaHeader viaHeader = headerFactory.createViaHeader(fromHost, fromPort, sipInfo.getTransport(), null);
//                viaHeaders.add(viaHeader);
                while (list.hasNext()) {
                    viaHeaders.add(list.next());
                }
                MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
                ContactHeader contactHeader = headerFactory.createContactHeader();
                contactHeader.setAddress(addressFactory.createAddress(fromUri));
                Request req = messageFactory.createRequest(toUri, Request.INVITE, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader);
                req.setHeader(contactHeader);
                SipManager.getInstance().sendRequest(req);
                logger.info(">>>>> [18] >>>>> Send Invite to SimA. req=\n"+req);
            }
        } catch (Exception e) {
            logger.error(e);
        }
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
            	if(Config.getTestCase() == 6) {
            		return true;
            	}
                return false;
            }

        };
    }

}
