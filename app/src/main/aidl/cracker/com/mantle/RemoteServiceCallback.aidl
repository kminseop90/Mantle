package cracker.com.mantle;

oneway interface RemoteServiceCallback {

    void valueChange(in String value);

    void onServiceDiscovered();
    void onConnectFailed(in String address);
}
