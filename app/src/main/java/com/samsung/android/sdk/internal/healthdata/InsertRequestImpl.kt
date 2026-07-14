package com.samsung.android.sdk.internal.healthdata

import android.os.Parcel
import android.os.Parcelable

class InsertRequestImpl() : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {}
    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<InsertRequestImpl> {
        override fun createFromParcel(parcel: Parcel) = InsertRequestImpl()
        override fun newArray(size: Int) = arrayOfNulls<InsertRequestImpl>(size)
    }
}
