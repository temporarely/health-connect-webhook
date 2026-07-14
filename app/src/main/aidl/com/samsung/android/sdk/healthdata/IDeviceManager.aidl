package com.samsung.android.sdk.healthdata;

import com.samsung.android.sdk.healthdata.HealthDevice;

/** {@hide} */
interface IDeviceManager {
    HealthDevice getLocalDevice();
    List<HealthDevice> getAllRegisteredDevices();
    HealthDevice getRegisteredDevice(in String deviceSeed);
    HealthDevice getRegisteredDeviceByUuid(in String uuid);
    String registerDevice(in HealthDevice device);
    boolean changeDeviceName(in String uuid, in String name);
    HealthDevice getDeviceByUuid(in String uuid);
    List<String> getDeviceUuidsBy(in String name, in int type);
}
