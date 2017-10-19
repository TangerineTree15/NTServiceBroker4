package com.naturaltel.sip;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.message.Request;
import javax.sip.message.Response;

import com.naturaltel.sip.entity.CallInfo;

public interface StanzaListener {
    
//    public void handleProcessTimeout(TimeoutEvent event);
//    
//    public void handleProcessTransactionTerminated(TransactionTerminatedEvent event);
//    
//    public void handleProcessDialogTerminated(DialogTerminatedEvent event);
//    
//    public void handleProcessIOException(IOExceptionEvent event);
    
    public void doAck(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doBye(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doCancel(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doInfo(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doMessage(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doInvite(ServerTransaction st, Request req, CallInfo callInfo) throws Exception;
    
    public void doNotify(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doOptions(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doPrack(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doPublish(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doRefer(ServerTransaction st, Request req, CallInfo callInfo);

    public void doRegister(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doSubscribe(ServerTransaction st, Request req, CallInfo callInfo);
    
    public void doUpdate(ServerTransaction st, Request req, CallInfo callInfo);

    /**
     * (1xx): Request received and being processed.
     */
    public void onProvisional(ClientTransaction ct, Response resp, CallInfo callInfo) throws Exception;
    
    /**
     * (2xx): The action was successfully received, understood, and accepted.
     */
    public void onSuccess(ClientTransaction ct, Response resp, CallInfo callInfo);
    
    
    /**
     * (3xx): Further action needs to be taken (typically by sender) to complete the request.
     */
    public void onRedirection(ClientTransaction ct, Response resp, CallInfo callInfo);
    
    /**
     * (4xx): The request contains bad syntax or cannot be fulfilled at the server.
     */
    public void onClientError(ClientTransaction ct, Response resp, CallInfo callInfo);
    
    /**
     * (5xx): The server failed to fulfill an apparently valid request.
     */
    public void onServerError(ClientTransaction ct, Response resp, CallInfo callInfo);
    
    
    /**
     * (6xx): The request cannot be fulfilled at any server.
     */
    public void onGlobalFailure(ClientTransaction ct, Response resp, CallInfo callInfo);
    
    public StanzaFilter getFilter();
}
