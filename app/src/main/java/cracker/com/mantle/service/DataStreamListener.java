package cracker.com.mantle.service;

public interface DataStreamListener {

    void onDataReceive(String msg);

    void onHeartReceive(String msg);
}
