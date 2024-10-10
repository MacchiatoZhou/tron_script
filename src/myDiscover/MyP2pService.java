package myDiscover;

import org.tron.p2p.P2pService;
import org.tron.p2p.connection.ChannelManager;
import org.tron.p2p.connection.business.pool.ConnPoolService;

public class MyP2pService extends P2pService {
    public void killConnPoolService(){
        ConnPoolService poolService=ChannelManager.getConnPoolService();
        poolService.close();
        System.out.println("ConnPoolService shutdown");
    }
}
