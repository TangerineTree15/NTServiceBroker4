package com.naturaltel.sip;

import gov.nist.javax.sip.address.SipUri;

public class TelUri extends SipUri {

    private static final long serialVersionUID = 8496818192825691259L;

    private String num;
    
    public TelUri(String num) {
        scheme = "tel:";
        this.num = num;
    }

    @Override
    public String encode() {
        return encode(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder encode(StringBuilder buffer) {
        buffer.append(scheme).append(num);
//        if (authority != null)
//            authority.encode(buffer);
        if (!uriParms.isEmpty()) {
            buffer.append(SEMICOLON);
            uriParms.encode(buffer);
        }
        if (!qheaders.isEmpty()) {
            buffer.append(QUESTION);
            qheaders.encode(buffer);
        }
        return buffer;
    }

    @Override
    public String toString() {
        return this.encode();
    }


}
