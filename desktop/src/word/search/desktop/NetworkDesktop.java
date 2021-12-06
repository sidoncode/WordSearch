package word.search.desktop;


import word.search.platform.Network;

public class NetworkDesktop implements Network {

    @Override
    public boolean isConnected() {
        return true;
    }
}
