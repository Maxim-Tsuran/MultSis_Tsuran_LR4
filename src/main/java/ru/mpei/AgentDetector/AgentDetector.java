package ru.mpei.AgentDetector;

import jade.core.AID;

import java.util.List;

public interface AgentDetector {
    void startPublishing(AID aid, int port, boolean flag);
    void startDiscovering(int port);
    List<AID> getActiveAgents();
}