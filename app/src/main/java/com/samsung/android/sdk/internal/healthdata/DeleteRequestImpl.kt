package com.samsung.android.sdk.internal.healthdata

import android.os.Parcel
import android.os.Parcelable

class DeleteRequestImpl() : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {}
    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<DeleteRequestImpl> {
        override fun createFromParcel(parcel: Parcel) = DeleteRequestImpl()
        override fun newArray(size: Int) = arrayOfNulls<DeleteRequestImpl>(size)
    }
}
