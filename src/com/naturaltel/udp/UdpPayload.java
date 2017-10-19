package com.naturaltel.udp;

import com.google.gson.Gson;

public class UdpPayload {

	public enum EventType {
		init,
		info,
		report,
		connect,
		release,
		continueFlow,
		ica,
		cancel,
		other,
		resetTimer,
	}
	
	public enum SubType {
		oAnswer,
		oDisconnect,
	}
	
	private EventType type;
	
	private SubType subType;
	
	private String callId;
	
	private String caller;
	
	private String callee;
	
	private String etb;
	
	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

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
	
	public String getEtb() {
		return etb;
	}

	public void setEtb(String etb) {
		this.etb = etb;
	}

	public SubType getSubType() {
		return subType;
	}

	public void setSubType(SubType subType) {
		this.subType = subType;
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
		
	}
	
}
