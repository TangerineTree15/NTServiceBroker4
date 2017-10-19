package com.naturaltel.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.naturaltel.cache.Config;
import com.naturaltel.udp.UdpPayload.EventType;

public class UdpManager implements Runnable {

    private static UdpManager instance;
    private Map<String, StanzaListener> listeners;
    
    private DatagramSocket udpSocket;
    private boolean isRun = false;
    private Thread thread;
    private Gson gson = null;

    private Logger logger = Logger.getLogger(getClass());

    private UdpManager() {
    	listeners = new ConcurrentHashMap<>();
    	gson = new Gson();
    }

    public static UdpManager getInstance() {
        if (instance == null) {
            instance = new UdpManager();
        }
        return instance;
    }
    
    public void wireListeners() {
    	addListener(new UdpHandler());
    	addListener(new Case1And2UdpHandler());
    	addListener(new Case3UdpHandler());
    	addListener(new Case4UdpHandler());
    	addListener(new Case5UdpHandler());
    	addListener(new Case6UdpHandler());
    	addListener(new Case7UdpHandler());
    }
    
    public void addListener(StanzaListener listener) {
        String key = listener.getClass().getName();
        if (!listeners.containsKey(key)) {
            listeners.put(key, listener);
        }
    }

    public boolean isRun() {
        return thread == null ? false : isRun;
    }

    public void stop() {
        isRun = false;
        if (udpSocket != null) {
            udpSocket.close();
            udpSocket = null;
        }
    }

    public void start() throws SocketException {
        isRun = true;
        udpSocket = new DatagramSocket(Config.getUdpServerInfo().getPort());
//        udpSocket = new DatagramSocket(new InetSocketAddress(Config.getUdpServerInfo().getAddress(), Config.getUdpServerInfo().getPort()));
        thread = new Thread(this);
        thread.start();
        logger.info("UDP server is started");

    }

    public void sendPacket(String destAddress, int destPort, UdpPayload payload) throws IOException, Exception {
    	InetSocketAddress bindAddr = new InetSocketAddress(destAddress, destPort);
    	String payloadStr = gson.toJson(payload, UdpPayload.class);
        byte[] payloadData = payloadStr.getBytes("UTF-8");
        DatagramPacket packet = new DatagramPacket(payloadData, payloadData.length, bindAddr);
        logger.info("[ dest = " + destAddress + ":" + destPort + " ] " + "UDP payload: " + payload);
        udpSocket.send(packet);
    }

    public void run() {
        while (isRun) {
            try {
                byte[] recData = new byte[1460];
                DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
                udpSocket.receive(recPacket);
                InetAddress inetAddress = recPacket.getAddress();
                String srcAddress = inetAddress.getHostAddress();
                int srcPort = recPacket.getPort();
                String dataStr = new String(recPacket.getData(), "UTF-8").trim();
                UdpPayload payload = gson.fromJson(dataStr, UdpPayload.class);
                for (String key : listeners.keySet()) {
                	StanzaListener listener = listeners.get(key);
                	StanzaFilter filter = listener.getFilter();
                	if(filter != null && filter.accept(srcAddress, srcPort, payload)) {
                		if(EventType.info.equals(payload.getType())) {
                			listener.doInfo(srcAddress, srcPort, payload);
                		} else if(EventType.report.equals(payload.getType())) {
                			listener.doReport(srcAddress, srcPort, payload);
                		} else if(EventType.connect.equals(payload.getType())) {
                			listener.doConnect(srcAddress, srcPort, payload);
                		} else if(EventType.release.equals(payload.getType())) {
                			listener.doRelease(srcAddress, srcPort, payload);
                		} else if(EventType.continueFlow.equals(payload.getType())) {
                			listener.doContinue(srcAddress, srcPort, payload);
                		} else if(EventType.ica.equals(payload.getType())) {
                			listener.doICA(srcAddress, srcPort, payload);
                		} else if(EventType.cancel.equals(payload.getType())) {
                			listener.doCancel(srcAddress, srcPort, payload);
                		} else if(EventType.resetTimer.equals(payload.getType())) {
                			listener.doResetTimer(srcAddress, srcPort, payload);
                		} else if(EventType.other.equals(payload.getType())) {
                			listener.doOther(srcAddress, srcPort, payload);
                		} 
                		
                	}
                }
            } catch (Exception e) {
                logger.error("", e);

            }
        }
    }
}
