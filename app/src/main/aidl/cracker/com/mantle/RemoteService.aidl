package cracker.com.mantle;

import cracker.com.mantle.RemoteServiceCallback;

interface RemoteService {

    boolean registerCallback(RemoteServiceCallback cb);
    boolean unregisterCallback(RemoteServiceCallback cb);

    void connect(in String address);

    void readGyro();
}
