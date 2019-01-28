package cracker.com.mantle.service;

public interface ConnectListener {

    void onServiceDiscovered();

    void onConnectFailed(String address);

}
