package com.samsung.android.sdk.internal.healthdata

import android.os.Parcel
import android.os.Parcelable

class AggregateRequestImpl() : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {}
    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<AggregateRequestImpl> {
        override fun createFromParcel(parcel: Parcel) = AggregateRequestImpl()
        override fun newArray(size: Int) = arrayOfNulls<AggregateRequestImpl>(size)
    }
}
