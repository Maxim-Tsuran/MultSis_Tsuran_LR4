// -gui
//-agents
//Agent1:ru.mpei.AgentDetector.UdpAgent(false);Agent2:ru.mpei.AgentDetector.UdpAgent(true);Agent3:ru.mpei.AgentDetector.UdpAgent(true);

package ru.mpei.AgentDetector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.List;

public class UdpAgent extends Agent {

    private AgentDetectorImpl agentDetectorImpl;
    AID aid;
    boolean flag;
    @Override
    protected void setup() {
        Object[] args = this.getArguments();
        if (args[0].equals("false")) {
            flag = false;
        } else {
            flag = true;
        }
        this.aid = new AID(this.getLocalName(), false);

        this.agentDetectorImpl = new AgentDetectorImpl();
        //Начало поиска
        this.agentDetectorImpl.startDiscovering(9000); // Инициализируем прослушивание

        //Начало отправления
        agentDetectorImpl.startPublishing(aid, 9000, flag);
        System.out.println(getLocalName() + " публикует себя впервые");


        this.addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                List<AID> activeAgents = agentDetectorImpl.getActiveAgents();
                System.err.println(getLocalName() + " нашел агентов: " + activeAgents);
            }
        });
    }
}