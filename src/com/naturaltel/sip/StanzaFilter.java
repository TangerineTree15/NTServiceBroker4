package com.naturaltel.sip;

import javax.sip.message.Message;

public interface StanzaFilter {

    public boolean accept(Message message);
    
}
