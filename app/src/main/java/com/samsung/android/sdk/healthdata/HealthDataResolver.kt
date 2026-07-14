package com.samsung.android.sdk.healthdata

import android.database.CursorWindow
import android.os.Parcel
import android.os.Parcelable

// Shell class whose inner class name compiles to HealthDataResolver$ReadResult —
// the class name Samsung Health writes into the result Bundle Parcelable.
// Android's Bundle.getParcelable() will find ReadResult.CREATOR via Class.forName.
class HealthDataResolver private constructor() {

    class ReadResult(
        val statusCode: Int,
        val totalCount: Int,
        val hasMore: Boolean,
        val resultId: String?,
        val columnNames: Array<String>?,
        val window: CursorWindow?,
        val rowCount: Int
    ) : Parcelable {

        override fun writeToParcel(dest: Parcel, flags: Int) = throw UnsupportedOperationException()
        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<ReadResult> {
            override fun createFromParcel(source: Parcel): ReadResult {
                // HealthResultHolder.BaseResult fields (3 ints)
                val statusCode = source.readInt()     // field a: result status
                val totalCount = source.readInt()     // field b: total record count
                val hasMore = source.readInt() == 1   // field c: has more pages

                // ReadResult own field
                val resultId = source.readString()    // field e

                // BulkCursorDescriptor — read manually instead of calling readParcelable()
                // to avoid needing the internal BulkCursorDescriptor class in our classpath.
                // readParcelable() would have written the class name string first.
                val descriptorClassName = source.readString()
                var columnNames: Array<String>? = null
                var window: CursorWindow? = null
                var rowCount = 0

                if (descriptorClassName != null) {
                    source.readStrongBinder()              // IBulkCursor (unused; we use CursorWindow)
                    columnNames = source.createStringArray()
                    source.readInt()                       // wantsAllOnMoveCalls
                    rowCount = source.readInt()            // total row count
                    val hasWindow = source.readInt()
                    window = if (hasWindow != 0) CursorWindow.CREATOR.createFromParcel(source) else null
                }

                return ReadResult(statusCode, totalCount, hasMore, resultId, columnNames, window, rowCount)
            }

            override fun newArray(size: Int) = arrayOfNulls<ReadResult>(size)
        }
    }
}
