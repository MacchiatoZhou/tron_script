package myConnection.keepalive;

import myConnection.MessageProcess;
import myConnection.MyChannel;
import myConnection.MyChannelManager;
import myConnection.message.MyMessage;
import myConnection.message.MyPingMessage;
import myConnection.message.MyPongMessage;
import myDiscover.MyConfig;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import static org.tron.p2p.base.Parameter.KEEP_ALIVE_TIMEOUT;
//import static org.tron.p2p.base.Parameter.PING_TIMEOUT;

public class MyKeepAliveService implements MessageProcess {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            new BasicThreadFactory.Builder().namingPattern("keepAlive").build());

    public void init() {
        executor.scheduleWithFixedDelay(() -> {
            try {
                long now = System.currentTimeMillis();
                MyChannelManager.getChannels().values().stream()
                        .filter(p -> !p.isDisconnect())
                        .forEach(p -> {
                            if (p.waitForPong) {
                                if (now - p.pingSent > MyConfig.KEEP_ALIVE_TIMEOUT) {
//                                    p.send(new MyP2pDisconnectMessage(Connect.DisconnectReason.PING_TIMEOUT));
//                                    p.close();
                                }
                            } else {
                                if (now - p.getLastSendTime() > MyConfig.PING_TIMEOUT && p.isFinishHandshake()) {
                                    p.send(new MyPingMessage());
                                    p.waitForPong = true;
                                    p.pingSent = now;
                                }
                            }
                            //p.send(new MyPongMessage());
                        });
            } catch (Exception t) {
                //log.error("Exception in keep alive task", t);
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public void close() {
        executor.shutdown();
    }

    @Override
    public void processMessage(MyChannel channel, MyMessage message) {
        switch (message.getType()) {
            case KEEP_ALIVE_PING:
                channel.send(new MyPongMessage());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                channel.send(new MyPongMessage());
                break;
            case KEEP_ALIVE_PONG:
                channel.updateAvgLatency(System.currentTimeMillis() - channel.pingSent);
                channel.waitForPong = false;
                break;
            default:
                break;
        }
    }
}
