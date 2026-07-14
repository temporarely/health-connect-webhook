package com.samsung.android.sdk.healthdata;

import com.samsung.android.sdk.healthdata.IDataResolver;
import com.samsung.android.sdk.healthdata.IDataWatcher;
import com.samsung.android.sdk.healthdata.IDeviceManager;
import com.samsung.android.sdk.internal.healthdata.HealthResultReceiver;
import android.content.Intent;

/** {@hide} */
interface IHealth {
    Bundle getUserProfile();
    Bundle getConnectionResult(in String packageName, in int clientVersion);
    HealthResultReceiver waitForInit(in long milliSeconds);

    IDeviceManager getIDeviceManager();
    IDataResolver getIDataResolver();
    IDataWatcher getIDataWatcher();

    HealthResultReceiver requestHealthDataPermissions(in Bundle permissionTypes);
    Bundle isHealthDataPermissionAcquired(in Bundle permissionTypes);
    boolean isKeyAccessible();
    Bundle getConnectionResult2(in Bundle clientInfo);

    Bundle getUserProfile2(in String caller);
    void waitForInit2(in String caller, in HealthResultReceiver receiver, in long milliSeconds);

    Intent requestHealthDataPermissions2(in String caller, in HealthResultReceiver receiver, in Bundle permissionTypes);
    Bundle isHealthDataPermissionAcquired2(in String caller, in Bundle permissionTypes);
}
