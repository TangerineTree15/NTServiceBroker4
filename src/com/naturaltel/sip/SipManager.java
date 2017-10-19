package com.naturaltel.sip;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
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
import com.naturaltel.config.entity.UserAgent;
import com.naturaltel.sip.entity.CallInfo;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.address.TelURLImpl;
import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;

public class SipManager implements SipListener {

    private static SipManager instance;

    private SipFactory sipFactory;
    private static MessageFactory messageFactory;
    private static HeaderFactory headerFactory;
    private static AddressFactory addressFactory;
    private ListeningPoint listeningPoint;
    private SipStack sipStack;
    private SipProvider sipProvider;

    private Map<String, CallInfo> callInfoMap;
    private Map<String, ListenerAndFilterPair> listeners;
    private List<String> onCallUserList;
    private List<String> onRingingUserList;
//    private Map<String, Request> reqMap;

    private Logger logger = Logger.getLogger("debug");

    private class ListenerAndFilterPair {

        private StanzaListener listener;
        private StanzaFilter filter;

        public StanzaListener getListener() {
            return listener;
        }

        public StanzaFilter getFilter() {
            return filter;
        }

        public void setListener(StanzaListener listener) {
            this.listener = listener;
        }

        public void setFilter(StanzaFilter filter) {
            this.filter = filter;
        }
    }

    private SipManager() throws Exception {
        sipFactory = SipFactory.getInstance();
        messageFactory = sipFactory.createMessageFactory();
        headerFactory = sipFactory.createHeaderFactory();
        addressFactory = sipFactory.createAddressFactory();
        sipStack = sipFactory.createSipStack(Config.getSipStack());

        InetAddress address = InetAddress.getByName(Config.getSipInfo().getAddress());
        listeningPoint = sipStack.createListeningPoint(address.getHostAddress(), Config.getSipInfo().getPort(), Config.getSipInfo().getTransport());
        sipProvider = sipStack.createSipProvider(listeningPoint);
        sipProvider.setAutomaticDialogSupportEnabled(false);
        sipProvider.addSipListener(this);

        callInfoMap = new ConcurrentHashMap<String, CallInfo>();
//        reqMap = new ConcurrentHashMap<String, Request>();
        listeners = new ConcurrentHashMap<String, ListenerAndFilterPair>();
        onCallUserList = new ArrayList<>();
        onRingingUserList = new ArrayList<>();
    }
    
    public void wireListeners() {
    	addStanzaListener(new SipHandler());
        addStanzaListener(new Case1And2SipHandler());
        addStanzaListener(new Case3SipHandler());
        addStanzaListener(new Case4SipHandler());
        addStanzaListener(new Case5SipHandler());
        addStanzaListener(new Case6SipHandler());
        addStanzaListener(new Case7SipHandler());
    }

    public static final SipManager getInstance() throws Exception {
        if (instance == null) {
            instance = new SipManager();
        }
        return instance;
    }

    public void addListener(StanzaListener listener, StanzaFilter filter) {
        String key = listener.getClass().getName();
        if (!listeners.containsKey(key)) {
            ListenerAndFilterPair pair = new ListenerAndFilterPair();
            pair.setListener(listener);
            pair.setFilter(filter);
            listeners.put(key, pair);
        }
    }

    public void addGenericListener(StanzaListener listener) {
        addListener(listener, new StanzaFilter() {

            public boolean accept(Message message) {
                return true;
            }

        });
    }
    
    public void addStanzaListener(StanzaListener listener) {
        addListener(listener, listener.getFilter());
    }
    
    public void addStanzaListener(StanzaListener listener, StanzaFilter filter) {
        addListener(listener, filter);
    }

    public final void start() throws SipException {
        if (sipStack != null) {
            sipStack.start();
        }
    }

    public final void stop() {
        sipStack.stop();
    }

    public CallInfo getCallInfo(String callId) {
        if (callInfoMap.containsKey(callId)) {
            return callInfoMap.get(callId);
        }
        return null;
    }

    public void setCallInfo(CallInfo callInfo) {
        String callId = callInfo.getCallId();
        callInfoMap.put(callId, callInfo);
    }
    
    public boolean containsCall(String callId) {
        return callInfoMap.containsKey(callId);
    }
    
    public void removeCall(String callId) {
        callInfoMap.remove(callId);
    }
    
    public void addToOnCallList(String msisdn) {
    	if(!onCallUserList.contains(msisdn)) {
    		onCallUserList.add(msisdn);
    		
    	}
    }
    
    public void removeFromOnCallList(String msisdn) {
    	onCallUserList.remove(msisdn);
    }
    
    public boolean isOnCall(String msisdn) {
    	if(onCallUserList.contains(msisdn)) {
    		return true;
    	}
    	return false;
    }
    
    public void addToOnRingingList(String msisdn) {
    	if(!onRingingUserList.contains(msisdn)) {
    		onRingingUserList.add(msisdn);
    		
    	}
    }
    
    public void removeFromOnRingingList(String msisdn) {
    	onRingingUserList.remove(msisdn);
    }
    
    public boolean isOnRinging(String msisdn) {
    	if(onRingingUserList.contains(msisdn)) {
    		return true;
    	}
    	return false;
    }

//    public Request getReq(String callIdPlusSrc) {
//        if (reqMap.containsKey(callIdPlusSrc)) {
//            return reqMap.get(callIdPlusSrc);
//        }
//        return null;
//    }
//
//    public void setReq(String callIdPlusSrc, Request req) {
//        reqMap.put(callIdPlusSrc, req);
//    }
//
//    public void resetReq(String callIdPlusSrc, Request req) {
//        reqMap.replace(callIdPlusSrc, req);
//    }
//    
//    public boolean containsReq(String callIdPlusSrc) {
//        return reqMap.containsKey(callIdPlusSrc);
//    }
//    
//    public void removeReq(String callIdPlusSrc) {
//        reqMap.remove(callIdPlusSrc);
//    }


    public final String getNewCallId() {
        return ((CallIdHeader) sipProvider.getNewCallId()).getCallId();
    }

    public CallIdHeader getNewCallIdHeader() {
        return (CallIdHeader) sipProvider.getNewCallId();
    }

    /**
     * For client to send stateful request.
     *
     * @param request
     * @return
     * @throws TransactionAlreadyExistsException
     * @throws TransactionUnavailableException
     */
    public final ClientTransaction getNewClientTransaction(Request request) throws TransactionAlreadyExistsException, TransactionUnavailableException {
        return sipProvider.getNewClientTransaction(request);
    }

    /**
     * For server to send stateful response to the request.
     *
     * @param request
     * @return
     * @throws TransactionAlreadyExistsException
     * @throws TransactionUnavailableException
     */
    public final ServerTransaction getNewServerTransaction(Request request) throws TransactionAlreadyExistsException, TransactionUnavailableException {
        return sipProvider.getNewServerTransaction(request);
    }

    /**
     * Send request stateless.
     *
     * @param request
     * @throws SipException
     */
    public final void sendRequest(Request request) throws SipException {
        sipProvider.sendRequest(request);
    }
    
    /**
     * Send response stateless.
     *
     * @param response
     * @throws SipException
     */
    public final void sendResponse(Response response) throws SipException {
        sipProvider.sendResponse(response);
    }

    public void processDialogTerminated(DialogTerminatedEvent event) {
        logger.info(event.getSource());

    }

    public void processIOException(IOExceptionEvent event) {
        logger.info(event.getSource());

    }

    public void processRequest(RequestEvent event) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                final Request req = event.getRequest();
                ServerTransaction st = event.getServerTransaction();
                Date eventTime = new Date();
                CallInfo callInfo = null;
                try {
                    logger.info("=====$$$$$ Get Request $$$$$=====");
                    logger.info(req.toString());
                	callInfo = parseCallInfo(req);
                    callInfo.setEventTime(eventTime);
                    setCallInfo(callInfo);
                    for (String key : listeners.keySet()) {
                        ListenerAndFilterPair pair = listeners.get(key);
                        StanzaListener listener = pair.getListener();
                        StanzaFilter filter = pair.getFilter();
                        if (filter != null && filter.accept(req)) {
                            final String method = req.getMethod();
                            logger.info(String.format("<<<<< Request method is %s", method));
                            if (Request.ACK.equals(method)) {
                                listener.doAck(st, req, callInfo);
                            } else if (Request.BYE.equals(method)) {
                                listener.doBye(st, req, callInfo);
                            } else if (Request.CANCEL.equals(method)) {
                                listener.doCancel(st, req, callInfo);
                            } else if (Request.INFO.equals(method)) {
                                listener.doInfo(st, req, callInfo);
                            } else if (Request.MESSAGE.equals(method)) {
                                listener.doMessage(st, req, callInfo);
                            } else if (Request.INVITE.equals(method)) {
                                listener.doInvite(st, req, callInfo);
                            } else if (Request.NOTIFY.equals(method)) {
                                listener.doNotify(st, req, callInfo);
                            } else if (Request.OPTIONS.equals(method)) {
                                listener.doOptions(st, req, callInfo);
                            } else if (Request.PRACK.equals(method)) {
                                listener.doPrack(st, req, callInfo);
                            } else if (Request.PUBLISH.equals(method)) {
                                listener.doPublish(st, req, callInfo);
                            } else if (Request.REFER.equals(method)) {
                                listener.doRefer(st, req, callInfo);
                            } else if (Request.REGISTER.equals(method)) {
                                listener.doRegister(st, req, callInfo);
                            } else if (Request.SUBSCRIBE.equals(method)) {
                                listener.doSubscribe(st, req, callInfo);
                            } else if (Request.UPDATE.equals(method)) {
                                listener.doUpdate(st, req, callInfo);
                            }
                        }
                    }
                } catch (Exception e) {
                	StringBuffer strBuffer = new StringBuffer();
                	strBuffer.append("event.getRequest(): ").append(event.getRequest().toString());
                	if(callInfo != null) {
                		strBuffer.append("callInfo: ").append(callInfo.toString());
                	}
                	logger.error(strBuffer.toString(), e);
                }

            }
        }).start();;

    }

    public void processResponse(ResponseEvent event) {
        Response resp = event.getResponse();
        ClientTransaction ct = event.getClientTransaction();
        for (String key : listeners.keySet()) {
            try {
                ListenerAndFilterPair pair = listeners.get(key);
                StanzaListener listener = pair.getListener();
                StanzaFilter filter = pair.getFilter();
                if (filter != null && filter.accept(resp)) {
                    final int statusCode = resp.getStatusCode();
                    final String callId = ((CallIdHeader) resp.getHeader(CallIdHeader.NAME)).getCallId();
                    if (containsCall(callId)) {
                        CallInfo callInfo = parseCallInfo(resp);
                        if (statusCode < 200) {
                            listener.onProvisional(ct, resp, callInfo);
                        } else if (statusCode < 300) {
                            listener.onSuccess(ct, resp, callInfo);
                        } else if (statusCode < 400) {
                            listener.onRedirection(ct, resp, callInfo);
                        } else if (statusCode < 500) {
                            listener.onClientError(ct, resp, callInfo);
                        } else if (statusCode < 600) {
                            listener.onServerError(ct, resp, callInfo);
                        } else if (statusCode < 700) {
                            listener.onGlobalFailure(ct, resp, callInfo);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(event.getResponse().toString(), e);
            }

        }

    }

    private CallInfo parseCallInfo(Message message) {
        CallInfo result = new CallInfo();
        String caller = null;
        String callee = null;
        String callId = null;
        String source = null;

        try {
        	result.setMessage(message);
        	
            //Get CallID.
            callId = ((CallIdHeader) message.getHeader(CallIdHeader.NAME)).getCallId();
            result.setCallId(callId);
            
            if(containsCall(callId)) {
            	// update the recent status of log serial number
            	CallInfo oldCallInfo = getCallInfo(callId);
            	result.setSerialNum(oldCallInfo.getSerialNum());
            }

            //Get Callee
            if(message instanceof Request) {
            	Request request = (Request)message;
            	URI calleeUri = request.getRequestURI();
            	if (calleeUri.isSipURI()) {
            		logger.debug(calleeUri + " is isSipURI ");
            		callee = ((SipUri) calleeUri).getUser();
              
            	} else {
            		logger.debug(calleeUri + " is isTelURI ");
            		callee = ((TelURLImpl) calleeUri).getPhoneNumber();
            	}
            } else if(message instanceof Response) {
            	CallInfo callInfo = getCallInfo(callId);
            	if(callInfo != null) {
            		callee = callInfo.getCallee();
            	}
            }
            result.setCallee(callee);
            
            //Get Caller
            PAssertedIdentityHeader pAssertedIdentityHeader = ((PAssertedIdentityHeader) message.getHeader(PAssertedIdentityHeader.NAME));
            URI callerUri = null;
            if (pAssertedIdentityHeader == null) {
                callerUri = ((FromHeader) message.getHeader(FromHeader.NAME)).getAddress().getURI();
            } else {
                callerUri = pAssertedIdentityHeader.getAddress().getURI();
            }
            if (callerUri.isSipURI()) {
            	logger.debug(callerUri + " is isSipURI ");
            	SipUri sipUri = ((SipUri) callerUri);
                caller = sipUri.getUser();
            } else {
            	logger.debug(callerUri + " is isTelURI ");
            	TelURLImpl telURLImpl = ((TelURLImpl) callerUri);
                caller = telURLImpl.getPhoneNumber();
            }
            result.setCaller(caller);
            
            ContactHeader contactHeader = (ContactHeader)message.getHeader(ContactHeader.NAME);
            SipUri contactUri = (SipUri)contactHeader.getAddress().getURI();
            source = contactUri.getHost() + ":" + contactUri.getPort();
            result.setSource(source);
            logger.info("source from contactHeader: " + source);

        } catch (Exception e) {
            logger.error("", e);
        }

        return result;
    }

    public void processTimeout(TimeoutEvent event) {
        if (event.isServerTransaction()) {
        } else {
        }

    }

    public void processTransactionTerminated(TransactionTerminatedEvent event) {
        logger.info(event.getSource());

    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public HeaderFactory getHeaderFactory() {
        return headerFactory;
    }

    public AddressFactory getAddressFactory() {
        return addressFactory;
    }

    public Request createRequest(String method, String callId, String toHost, int toPort, String callee) throws Exception{
        Request req = null;
        try {
            SipInfo sipInfo = Config.getSipInfo();
            String fromHost = sipInfo.getAddress();
            int fromPort = sipInfo.getPort();
            String num = callee;
            SipUri toUri = new SipUri();
            toUri.setUser(num);
            toUri.setHost(toHost);
            toUri.setPort(toPort);
            SipUri fromUri = new SipUri();
            fromUri.setUser(sipInfo.getName());                                                                                                                                                                                                          
            fromUri.setHost(fromHost);
            fromUri.setPort(fromPort);
            
            CallIdHeader callIdHeader = getNewCallIdHeader();
            if(callId != null) {
                callIdHeader.setCallId(callId);
            }
            long cseq = 10;
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(cseq, method);
            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromUri), "tag");
            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri), null);
            List<ViaHeader> viaHeaders = new ArrayList<>();
            ViaHeader viaHeader = headerFactory.createViaHeader(fromHost, fromPort, sipInfo.getTransport(), null);
            viaHeaders.add(viaHeader);
            MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
            ContactHeader contactHeader = headerFactory.createContactHeader();
            contactHeader.setAddress(addressFactory.createAddress(fromUri));
            
            req = messageFactory.createRequest(toUri, method, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader);
            req.setHeader(contactHeader);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
        
        return req;
    }

    public Request marshal(Request req, String callee, String toHost, int toPort) throws Exception {
        try {
            SipInfo sipInfo = Config.getSipInfo();
            SipUri toUri = new SipUri();
            toUri.setUser(callee);
            toUri.setHost(toHost);
            toUri.setPort(toPort);
            SipUri fromUri = new SipUri();
            fromUri.setUser(sipInfo.getName());
            fromUri.setHost(sipInfo.getAddress());
            fromUri.setPort(sipInfo.getPort());
            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromUri), "tag");
            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri), null);
            ViaHeader viaHeader = headerFactory.createViaHeader(sipInfo.getAddress(), sipInfo.getPort(), sipInfo.getTransport(), null);
            ContactHeader contactHeader = headerFactory.createContactHeader();
            contactHeader.setAddress(addressFactory.createAddress(fromUri));
            req.setHeader(contactHeader);
            req.setHeader(fromHeader);
            req.setHeader(toHeader);
            req.addHeader(viaHeader);
            req.setRequestURI(toUri);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
        return req;
    }
    
    public Response marshal(Response resp, String callee, String toHost, int toPort, boolean isForwarded) throws Exception{
        try {
            SipInfo sipInfo = Config.getSipInfo();
            if(isForwarded) {
                resp.removeFirst(ViaHeader.NAME);
            }
            SipUri toUri = new SipUri();
            toUri.setUser(callee);
            toUri.setHost(toHost);
            toUri.setPort(toPort);
            SipUri fromUri = new SipUri();
            fromUri.setUser(sipInfo.getName());
            fromUri.setHost(sipInfo.getAddress());
            fromUri.setPort(sipInfo.getPort());
            ContactHeader contactHeader = headerFactory.createContactHeader();
            contactHeader.setAddress(addressFactory.createAddress(fromUri));
            resp.setHeader(contactHeader);
            ToHeader toHeader = headerFactory.createToHeader(addressFactory.createAddress(toUri), null);
            resp.setHeader(toHeader);
            FromHeader fromHeader = headerFactory.createFromHeader(addressFactory.createAddress(fromUri), "tag");
            resp.setHeader(fromHeader);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
        return resp;
    }
    
    public Request createInfoRequest(String callId, String callee, String toHost, int toPort) throws Exception {
        Request newReq = createRequest(Request.INFO, callId, toHost, toPort, callee);
        ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "cs1+");
        contentTypeHeader.setParameter("encoding", "UTF-8");
        newReq.addHeader(contentTypeHeader);
        return newReq;
        
    }
    
	public UserAgent getIMS(String callee) {
		if(callee != null && !"".equals(callee)) {
			SipInfo sipInfo = Config.getSipInfo();
			for(UserAgent ua : sipInfo.getUserAgents()) {
				String msisdn = ua.getMsisdn();
				if(callee.trim().contains(msisdn)) {
					return ua;
				}
			}
		}
		return null;
	}
	
}
