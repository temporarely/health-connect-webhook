package com.samsung.android.sdk.healthdata;

/** {@hide} */
oneway interface IHealthDataObserver {
    void onChange(String dataTypeName);
}
