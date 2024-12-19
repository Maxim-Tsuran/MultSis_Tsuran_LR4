package ru.mpei.AgentDetector;

import jade.core.AID;

import java.util.List;
import java.util.Optional;

public class AgentDetectorImpl implements AgentDetector{

    UdpSubscriber sub;
    List<AID> activeAgents;
    boolean flag;
    private int current = 0;

    @Override
    public void startPublishing(AID aid, int port, boolean flag) {
        String agentName = String.format(aid.getName());
        String agentData;
        Optional<String> serialize = SerializatorJason.serialize(aid);
        agentData = serialize.orElse("");

        this.flag = flag;
        System.out.println(flag);
        UdpPublisher udpPublisher = new UdpPublisher();
        udpPublisher.create("127.0.0.1", 9000);
        Thread senderThread = new Thread(() -> {
            boolean flagg = true;
            while(flagg) {
                if (flag == false) {
                    current++;
                }
                if (current >4){
                    flagg = false;
                }
                udpPublisher.send(agentData);
                System.out.println(agentName + " публикует себя");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        senderThread.start();
    }

    @Override
    public void startDiscovering(int port) {
        sub = new UdpSubscriber();
        sub.start(port);
    }

    @Override
    public List<AID> getActiveAgents() {
        activeAgents = sub.getActiveAgents();
        return activeAgents;

    }
}