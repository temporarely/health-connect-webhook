package com.samsung.android.sdk.internal.healthdata;

import com.samsung.android.sdk.internal.healthdata.IHealthResultReceiver;

/** {@hide} */
oneway interface ICallbackRegister {
    void setCallback(int resultCode, in IHealthResultReceiver callback);
    void cancel(int status);
}
