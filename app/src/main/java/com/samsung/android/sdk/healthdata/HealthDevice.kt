package com.samsung.android.sdk.healthdata

import android.os.Parcel
import android.os.Parcelable

class HealthDevice() : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {}
    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<HealthDevice> {
        override fun createFromParcel(parcel: Parcel) = HealthDevice()
        override fun newArray(size: Int) = arrayOfNulls<HealthDevice>(size)
    }
}
