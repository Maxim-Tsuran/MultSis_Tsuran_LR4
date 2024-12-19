package ru.mpei.AgentDetector;

import java.io.IOException;
import java.net.*;

public class UdpPublisher {

    private DatagramSocket socket;
    private int port;
    private InetAddress ip;

    public void create(String ip, int port){
        this.port = port;
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String data){
        DatagramPacket datagramPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, ip, port);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
