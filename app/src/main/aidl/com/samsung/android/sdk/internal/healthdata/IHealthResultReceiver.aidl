package com.samsung.android.sdk.internal.healthdata;

/** {@hide} */
oneway interface IHealthResultReceiver {
    void send(int resultCode, in Bundle resultData);
}
