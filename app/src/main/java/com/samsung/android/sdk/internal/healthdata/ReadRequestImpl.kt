package com.samsung.android.sdk.internal.healthdata

import android.os.Parcel
import android.os.Parcelable

class ReadRequestImpl(
    val dataType: String,
    val sortOrder: String,
    val packageName: String,
    val startTime: Long,
    val endTime: Long,
    val offset: Int,
    val count: Int,
    val timeAfter: Long
) : Parcelable {

    // Exact 16-field serialization order from Samsung SDK bytecode analysis.
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(dataType)
        dest.writeString(sortOrder)
        dest.writeString(packageName)
        dest.writeLong(startTime)
        dest.writeLong(endTime)
        dest.writeInt(offset)
        dest.writeInt(count)
        dest.writeString(null)  // filter: null Parcelable serialised as null string by writeParcelable(null, 0)
        dest.writeInt(-1)       // null projections list  (writeTypedList(null) writes -1)
        dest.writeInt(-1)       // null deviceUuids list  (writeStringList(null) writes -1)
        dest.writeByte(0)       // aliasOnly = false      (writeByte pads to int)
        dest.writeLong(timeAfter)
        dest.writeString(null)  // localTimeProperty
        dest.writeString(null)  // localTimeOffsetProperty
        dest.writeLong(0L)      // localTimeBegin
        dest.writeLong(0L)      // localTimeEnd
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ReadRequestImpl> {
        override fun createFromParcel(parcel: Parcel) = throw UnsupportedOperationException()
        override fun newArray(size: Int) = arrayOfNulls<ReadRequestImpl>(size)
    }
}
