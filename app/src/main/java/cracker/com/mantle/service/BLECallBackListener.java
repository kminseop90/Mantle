package cracker.com.mantle.service;

import java.util.ArrayList;
import java.util.HashMap;

public interface BLECallBackListener {

    void getScanDevices(ArrayList<ArrayList<HashMap<String, String>>> scanDevices);

}
