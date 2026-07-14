package com.samsung.android.sdk.internal.healthdata

import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable

// Parcelable wrapper passed to IDataResolver.readData2() as the "receiver" parameter.
// Serialized as Samsung's HealthResultReceiver.ForwardAsync format so Samsung Health
// can call back our IHealthResultReceiver binder when data is ready.
class HealthResultReceiver(val binder: IBinder) : Parcelable {

    // WriteToParcel format matches HealthResultReceiver$ForwardAsync.writeToParcel:
    //   int mVersion=1, IBinder (our IHealthResultReceiver.Stub), int type=2, String receiverId=null
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(1)             // mVersion
        dest.writeStrongBinder(binder)
        dest.writeInt(2)             // type = ForwardAsync
        dest.writeString(null)       // receiverId
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<HealthResultReceiver> {
        override fun createFromParcel(parcel: Parcel): HealthResultReceiver {
            parcel.readInt()          // mVersion
            val b = parcel.readStrongBinder() ?: error("null binder in HealthResultReceiver")
            parcel.readInt()          // type
            parcel.readString()       // receiverId
            return HealthResultReceiver(b)
        }
        override fun newArray(size: Int) = arrayOfNulls<HealthResultReceiver>(size)
    }
}
