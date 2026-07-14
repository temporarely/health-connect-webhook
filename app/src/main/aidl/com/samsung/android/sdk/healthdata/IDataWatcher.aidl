package com.samsung.android.sdk.healthdata;

import com.samsung.android.sdk.healthdata.IHealthDataObserver;

/** {@hide} */
interface IDataWatcher {
    void registerDataObserver(in String dataTypeName, in IHealthDataObserver observer);
    void unregisterDataObserver(in IHealthDataObserver observer);

    void registerDataObserver2(in String caller, in String dataTypeName, in IHealthDataObserver observer);
    void unregisterDataObserver2(in String caller, in IHealthDataObserver observer);
}
