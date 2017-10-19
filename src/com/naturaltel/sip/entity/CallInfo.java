package com.naturaltel.sip.entity;

import java.util.Date;

import javax.sip.message.Message;

public class CallInfo {

    private String callId;
    
    private String caller;
    
    private String callee;
    
    private Date eventTime;
    
    private String source;

    private Message message;
    
    private int serialNum;
    
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}
	
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CallInfo [callId=");
		builder.append(callId);
		builder.append(", caller=");
		builder.append(caller);
		builder.append(", callee=");
		builder.append(callee);
		builder.append(", eventTime=");
		builder.append(eventTime);
		builder.append(", source=");
		builder.append(source);
		builder.append(", message=");
		builder.append(message);
		builder.append(", serialNum=");
		builder.append(serialNum);
		builder.append("]");
		return builder.toString();
	}

    
}
