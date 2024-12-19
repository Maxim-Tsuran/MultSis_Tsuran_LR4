package ru.mpei.AgentDetector;

import com.sun.jna.NativeLibrary;
import jade.core.AID;
import lombok.SneakyThrows;
import org.pcap4j.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class UdpSubscriber {

    static {
        NativeLibrary.addSearchPath("wpcap", "C:\\Windows\\System32\\Npcap");
    }

    private final Map<AID, Long> activeAgents = new HashMap<>();
    private PcapHandle pcapHandle;
    private final long TIMEOUT_MS = 3000; // Тайм-аут для удаления неактуальных агентов (5 секунд)

    @SneakyThrows
    public void start(int port) {

        //настраиваем сеть приема, сетевые интерфейсы
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        PcapNetworkInterface pcapNetworkInterface = allDevs.stream()
                .filter(e -> e.getName().equals("\\Device\\NPF_Loopback"))
                .findAny()
                .orElseThrow();

        //ловим пакеты
        pcapHandle = pcapNetworkInterface.openLive(1500, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
        //поток для обработки пакетов
        Thread t = new Thread(() -> {
            try {
                //фильтр пакетов
                pcapHandle.setFilter("ip proto \\udp && dst port " + port, BpfProgram.BpfCompileMode.NONOPTIMIZE);
                pcapHandle.loop(-1, (PacketListener) packet -> {
                    byte[] rawData = packet.getRawData();

                    String aid = new String(rawData, 32, rawData.length - 32);
                    //Десереализируем
                    Optional<AgentsGetSet> deserialize = SerializatorJason.deserialize(aid, AgentsGetSet.class);

                    if (deserialize.isPresent()) {
                        AID agent = deserialize.get().getName();
                        synchronized (activeAgents) {
                            activeAgents.put(agent, System.currentTimeMillis()); // Обновляем время активности агента
                        }
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();

        // Поток для удаления устаревших агентов
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // Период проверки (1 секунда)
                    synchronized (activeAgents) {
                        long currentTime = System.currentTimeMillis();
                        Iterator<Map.Entry<AID, Long>> iterator = activeAgents.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<AID, Long> entry = iterator.next();
                            if (currentTime - entry.getValue() > TIMEOUT_MS) {
                                iterator.remove(); // Удаляем агента, если он неактивен
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        cleanupThread.start();
    }

    public List<AID> getActiveAgents() {
        synchronized (activeAgents) {
            return new ArrayList<>(activeAgents.keySet());
        }
    }
}