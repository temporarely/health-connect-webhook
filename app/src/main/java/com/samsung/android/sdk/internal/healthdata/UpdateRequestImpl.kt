package com.samsung.android.sdk.internal.healthdata

import android.os.Parcel
import android.os.Parcelable

class UpdateRequestImpl() : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {}
    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<UpdateRequestImpl> {
        override fun createFromParcel(parcel: Parcel) = UpdateRequestImpl()
        override fun newArray(size: Int) = arrayOfNulls<UpdateRequestImpl>(size)
    }
}
