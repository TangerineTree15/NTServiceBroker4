package com.naturaltel.udp;

public interface StanzaFilter {

    public boolean accept(String srcAddress, int srcPort, UdpPayload payload);
    
}
