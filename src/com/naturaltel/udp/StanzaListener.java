package com.naturaltel.udp;

import com.naturaltel.udp.StanzaFilter;

public interface StanzaListener {

	public void doInfo(String srcAddress, int srcPort, UdpPayload payload);
	public void doReport(String srcAddress, int srcPort, UdpPayload payload);
	public void doConnect(String srcAddress, int srcPort, UdpPayload payload);
	public void doRelease(String srcAddress, int srcPort, UdpPayload payload);
	public void doContinue(String srcAddress, int srcPort, UdpPayload payload);
	public void doOther(String srcAddress, int srcPort, UdpPayload payload);
	public void doICA(String srcAddress, int srcPort, UdpPayload payload);
	public void doCancel(String srcAddress, int srcPort, UdpPayload payload);
	public void doResetTimer(String srcAddress, int srcPort, UdpPayload payload);
	public StanzaFilter getFilter();
}
