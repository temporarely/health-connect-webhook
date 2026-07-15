package com.samsung.android.sdk.healthdata

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class HealthPermissionManager {

    enum class PermissionType {
        READ,   // ordinal 0
        WRITE   // ordinal 1
    }

    class PermissionKey(
        val dataType: String,
        val permissionType: PermissionType
    ) : Parcelable {

        fun getDataType(): String = dataType
        fun getPermissionType(): PermissionType = permissionType

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(dataType)
            dest.writeInt(permissionType.ordinal)
        }

        override fun describeContents() = 0

        override fun equals(other: Any?): Boolean =
            other is PermissionKey && dataType == other.dataType && permissionType == other.permissionType

        override fun hashCode(): Int = 31 * dataType.hashCode() + permissionType.ordinal

        companion object CREATOR : Parcelable.Creator<PermissionKey> {
            override fun createFromParcel(parcel: Parcel): PermissionKey {
                val dt = parcel.readString() ?: ""
                val ordinal = parcel.readInt()
                val type = PermissionType.values().getOrElse(ordinal) { PermissionType.READ }
                return PermissionKey(dt, type)
            }
            override fun newArray(size: Int) = arrayOfNulls<PermissionKey>(size)
        }
    }

    // Parcelable for reading the result bundle from isHealthDataPermissionAcquired2
    class PermissionResult(
        val permissionKey: PermissionKey?,
        val isAcquired: Boolean
    ) : Parcelable {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeParcelable(permissionKey, flags)
            dest.writeInt(if (isAcquired) 1 else 0)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<PermissionResult> {
            override fun createFromParcel(parcel: Parcel): PermissionResult {
                val key = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    parcel.readParcelable(PermissionKey::class.java.classLoader, PermissionKey::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    parcel.readParcelable(PermissionKey::class.java.classLoader)
                }
                val acquired = parcel.readInt() != 0
                return PermissionResult(key, acquired)
            }
            override fun newArray(size: Int) = arrayOfNulls<PermissionResult>(size)
        }
    }

    companion object {
        const val BUNDLE_KEY = "permissionArrayList"
    }
}
