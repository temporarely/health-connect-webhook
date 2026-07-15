package com.hcwebhook.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.CursorWindow
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.IHealth
import com.samsung.android.sdk.healthdata.IDataResolver
import com.samsung.android.sdk.internal.healthdata.HealthResultReceiver
import com.samsung.android.sdk.internal.healthdata.IHealthResultReceiver
import com.samsung.android.sdk.internal.healthdata.ReadRequestImpl
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class SamsungHealthManager(private val context: Context) {

    companion object {
        private const val SHEALTH_PACKAGE = "com.sec.android.app.shealth"
        private const val SERVICE_CLASS = "com.samsung.android.sdk.healthdata.HealthDataService"

        private const val TYPE_STEPS = "com.samsung.health.step_count"
        private const val TYPE_HEART_RATE = "com.samsung.health.heart_rate"
        private const val TYPE_SLEEP = "com.samsung.health.sleep"
        private const val TYPE_EXERCISE = "com.samsung.health.exercise"
        private const val TYPE_BLOOD_PRESSURE = "com.samsung.health.blood_pressure"
        private const val TYPE_BLOOD_GLUCOSE = "com.samsung.health.blood_glucose"
        private const val TYPE_OXYGEN_SATURATION = "com.samsung.health.oxygen_saturation"
        private const val TYPE_BODY_TEMPERATURE = "com.samsung.health.body_temperature"
        private const val TYPE_WEIGHT = "com.samsung.health.weight"
        private const val TYPE_HEIGHT = "com.samsung.health.height"
        private const val TYPE_BODY_FAT = "com.samsung.health.body_fat"
        private const val TYPE_WATER_INTAKE = "com.samsung.health.water_intake"

        // Samsung exercise type int → Health Connect string (most common subset)
        private val EXERCISE_TYPE_MAP = mapOf(
            1001 to "walking",
            1002 to "running",
            1003 to "biking",
            1004 to "aerobics",
            1009 to "hiking",
            1010 to "elliptical",
            1013 to "yoga",
            1021 to "stretching",
            1024 to "badminton",
            1025 to "basketball",
            1028 to "swimming_open_water",
            1029 to "swimming_pool",
            1031 to "tennis",
            1034 to "football_american",
            1037 to "volleyball",
            1039 to "martial_arts",
            1040 to "pilates",
            1043 to "dancing",
            1044 to "jump_rope",
            1053 to "hiking",
            11007 to "strength_training",
            11001 to "weight_training"
        )

        // mg/dL → mmol/L conversion for blood glucose
        private const val MG_DL_TO_MMOL_L = 1.0 / 18.0182
    }

    suspend fun readHealthData(
        enabledTypes: Set<HealthDataType>,
        lastSyncTimestamps: Map<HealthDataType, Instant?>,
        timeRangeDays: Int? = null,
        start: Instant? = null,
        end: Instant? = null
    ): Result<HealthData> = withContext(Dispatchers.IO) {
        val endTime = end ?: Instant.now()
        val startTime = when {
            start != null -> start
            timeRangeDays != null -> endTime.minus(timeRangeDays.toLong(), ChronoUnit.DAYS)
            else -> endTime.minus(24, ChronoUnit.HOURS)
        }

        val conn = SamsungHealthConnection()
        try {
            val resolver = conn.connect()
                ?: return@withContext Result.failure(Exception("Samsung Health is not installed or service unavailable"))

            suspend fun <T> safe(block: suspend () -> List<T>): List<T> =
                try { block() } catch (_: Exception) { emptyList() }

            val steps = if (HealthDataType.STEPS in enabledTypes)
                safe { readSteps(resolver, startTime, endTime) } else emptyList()
            val sleep = if (HealthDataType.SLEEP in enabledTypes)
                safe { readSleep(resolver, startTime, endTime) } else emptyList()
            val heartRate = if (HealthDataType.HEART_RATE in enabledTypes)
                safe { readHeartRate(resolver, startTime, endTime) } else emptyList()
            val exercise = if (HealthDataType.EXERCISE in enabledTypes)
                safe { readExercise(resolver, startTime, endTime) } else emptyList()
            val bloodPressure = if (HealthDataType.BLOOD_PRESSURE in enabledTypes)
                safe { readBloodPressure(resolver, startTime, endTime) } else emptyList()
            val bloodGlucose = if (HealthDataType.BLOOD_GLUCOSE in enabledTypes)
                safe { readBloodGlucose(resolver, startTime, endTime) } else emptyList()
            val oxygenSaturation = if (HealthDataType.OXYGEN_SATURATION in enabledTypes)
                safe { readOxygenSaturation(resolver, startTime, endTime) } else emptyList()
            val bodyTemperature = if (HealthDataType.BODY_TEMPERATURE in enabledTypes)
                safe { readBodyTemperature(resolver, startTime, endTime) } else emptyList()
            val weight = if (HealthDataType.WEIGHT in enabledTypes)
                safe { readWeight(resolver, startTime, endTime) } else emptyList()
            val height = if (HealthDataType.HEIGHT in enabledTypes)
                safe { readHeight(resolver, startTime, endTime) } else emptyList()
            val bodyFat = if (HealthDataType.BODY_FAT in enabledTypes)
                safe { readBodyFat(resolver, startTime, endTime) } else emptyList()
            val hydration = if (HealthDataType.HYDRATION in enabledTypes)
                safe { readWaterIntake(resolver, startTime, endTime) } else emptyList()

            Result.success(HealthData(
                steps = steps,
                sleep = sleep,
                heartRate = heartRate,
                heartRateVariability = emptyList(),
                distance = emptyList(),
                activeCalories = emptyList(),
                totalCalories = emptyList(),
                weight = weight,
                height = height,
                bloodPressure = bloodPressure,
                bloodGlucose = bloodGlucose,
                oxygenSaturation = oxygenSaturation,
                bodyTemperature = bodyTemperature,
                skinTemperature = emptyList(),
                respiratoryRate = emptyList(),
                restingHeartRate = emptyList(),
                exercise = exercise,
                hydration = hydration,
                nutrition = emptyList(),
                basalMetabolicRate = emptyList(),
                bodyFat = bodyFat,
                leanBodyMass = emptyList(),
                vo2Max = emptyList(),
                boneMass = emptyList()
            ))
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn.disconnect()
        }
    }

    // ── Data readers ──────────────────────────────────────────────────────────

    private suspend fun readSteps(resolver: IDataResolver, start: Instant, end: Instant): List<StepsData> {
        val result = queryRaw(resolver, TYPE_STEPS, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val endMs = window.longCol(cols, row, "end_time") ?: return@mapRows null
            val count = window.longCol(cols, row, "count") ?: return@mapRows null
            StepsData(count = count, startTime = Instant.ofEpochMilli(startMs), endTime = Instant.ofEpochMilli(endMs))
        }
    }

    private suspend fun readHeartRate(resolver: IDataResolver, start: Instant, end: Instant): List<HeartRateData> {
        val result = queryRaw(resolver, TYPE_HEART_RATE, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val hr = window.doubleCol(cols, row, "heart_rate") ?: return@mapRows null
            val hrMin = window.doubleCol(cols, row, "heart_rate_min") ?: hr
            val hrMax = window.doubleCol(cols, row, "heart_rate_max") ?: hr
            HeartRateData(
                bpm = hr.toLong(),
                bpmMin = hrMin.toLong(),
                bpmMax = hrMax.toLong(),
                measurementsCount = window.longCol(cols, row, "heart_beat_count") ?: 1L,
                time = Instant.ofEpochMilli(startMs)
            )
        }
    }

    private suspend fun readSleep(resolver: IDataResolver, start: Instant, end: Instant): List<SleepData> {
        val result = queryRaw(resolver, TYPE_SLEEP, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val endMs = window.longCol(cols, row, "end_time") ?: return@mapRows null
            val sessionStart = Instant.ofEpochMilli(startMs)
            val sessionEnd = Instant.ofEpochMilli(endMs)
            SleepData(
                sessionEndTime = sessionEnd,
                duration = Duration.between(sessionStart, sessionEnd),
                stages = emptyList()
            )
        }
    }

    private suspend fun readExercise(resolver: IDataResolver, start: Instant, end: Instant): List<ExerciseData> {
        val result = queryRaw(resolver, TYPE_EXERCISE, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val endMs = window.longCol(cols, row, "end_time") ?: return@mapRows null
            val typeCode = window.longCol(cols, row, "exercise_type")?.toInt() ?: 0
            val sessionStart = Instant.ofEpochMilli(startMs)
            val sessionEnd = Instant.ofEpochMilli(endMs)
            ExerciseData(
                type = EXERCISE_TYPE_MAP[typeCode] ?: "type_$typeCode",
                startTime = sessionStart,
                endTime = sessionEnd,
                duration = Duration.between(sessionStart, sessionEnd),
                distanceMeters = window.doubleCol(cols, row, "distance"),
                steps = window.longCol(cols, row, "count")
            )
        }
    }

    private suspend fun readBloodPressure(resolver: IDataResolver, start: Instant, end: Instant): List<BloodPressureData> {
        val result = queryRaw(resolver, TYPE_BLOOD_PRESSURE, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val systolic = window.doubleCol(cols, row, "systolic") ?: return@mapRows null
            val diastolic = window.doubleCol(cols, row, "diastolic") ?: return@mapRows null
            BloodPressureData(systolic = systolic, diastolic = diastolic, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readBloodGlucose(resolver: IDataResolver, start: Instant, end: Instant): List<BloodGlucoseData> {
        val result = queryRaw(resolver, TYPE_BLOOD_GLUCOSE, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val glucoseMgDl = window.doubleCol(cols, row, "glucose") ?: return@mapRows null
            BloodGlucoseData(mmolPerLiter = glucoseMgDl * MG_DL_TO_MMOL_L, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readOxygenSaturation(resolver: IDataResolver, start: Instant, end: Instant): List<OxygenSaturationData> {
        val result = queryRaw(resolver, TYPE_OXYGEN_SATURATION, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val spo2 = window.doubleCol(cols, row, "spo2") ?: return@mapRows null
            OxygenSaturationData(percentage = spo2, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readBodyTemperature(resolver: IDataResolver, start: Instant, end: Instant): List<BodyTemperatureData> {
        val result = queryRaw(resolver, TYPE_BODY_TEMPERATURE, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val temp = window.doubleCol(cols, row, "body_temperature") ?: return@mapRows null
            BodyTemperatureData(celsius = temp, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readWeight(resolver: IDataResolver, start: Instant, end: Instant): List<WeightData> {
        val result = queryRaw(resolver, TYPE_WEIGHT, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val kg = window.doubleCol(cols, row, "weight") ?: return@mapRows null
            WeightData(kilograms = kg, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readHeight(resolver: IDataResolver, start: Instant, end: Instant): List<HeightData> {
        val result = queryRaw(resolver, TYPE_HEIGHT, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val cm = window.doubleCol(cols, row, "height") ?: return@mapRows null
            HeightData(meters = cm / 100.0, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readBodyFat(resolver: IDataResolver, start: Instant, end: Instant): List<BodyFatData> {
        val result = queryRaw(resolver, TYPE_BODY_FAT, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val pct = window.doubleCol(cols, row, "body_fat") ?: return@mapRows null
            BodyFatData(percentage = pct, time = Instant.ofEpochMilli(startMs))
        }
    }

    private suspend fun readWaterIntake(resolver: IDataResolver, start: Instant, end: Instant): List<HydrationData> {
        val result = queryRaw(resolver, TYPE_WATER_INTAKE, start, end) ?: return emptyList()
        return result.mapRows { window, cols, row ->
            val startMs = window.longCol(cols, row, "start_time") ?: return@mapRows null
            val endMs = window.longCol(cols, row, "end_time") ?: startMs
            val ml = window.doubleCol(cols, row, "amount") ?: return@mapRows null
            HydrationData(liters = ml / 1000.0, startTime = Instant.ofEpochMilli(startMs), endTime = Instant.ofEpochMilli(endMs))
        }
    }

    // ── Low-level query ───────────────────────────────────────────────────────

    private suspend fun queryRaw(
        resolver: IDataResolver,
        dataType: String,
        start: Instant,
        end: Instant
    ): HealthDataResolver.ReadResult? = withTimeoutOrNull(30_000L) {
        suspendCancellableCoroutine { cont ->
            val receiverStub = object : IHealthResultReceiver.Stub() {
                override fun send(resultCode: Int, resultData: Bundle?) {
                    if (cont.isActive) cont.resume(parseBundle(resultData))
                }
            }
            val request = ReadRequestImpl(
                dataType = dataType,
                sortOrder = "start_time ASC",
                packageName = context.packageName,
                startTime = start.toEpochMilli(),
                endTime = end.toEpochMilli(),
                offset = 0,
                count = Int.MAX_VALUE,
                timeAfter = 0L
            )
            try {
                resolver.readData2(context.packageName, HealthResultReceiver(receiverStub.asBinder()), request)
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWithException(e)
            }
        }
    }

    private fun parseBundle(bundle: Bundle?): HealthDataResolver.ReadResult? {
        if (bundle == null) return null
        val type = bundle.getInt("type", -1)
        if (type in 1..3) return null  // BaseResult, PermissionResult, AggregateResult — not data
        // Set our classloader so Android can find HealthDataResolver$ReadResult when unparcelling.
        // Bundles received from a foreign process default to null/system classloader.
        bundle.classLoader = HealthDataResolver::class.java.classLoader
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable("parcel", HealthDataResolver.ReadResult::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable("parcel")
        }
    }

    // ── CursorWindow helpers ──────────────────────────────────────────────────

    private fun CursorWindow.longCol(cols: Array<String>?, row: Int, name: String): Long? {
        val idx = cols?.indexOf(name) ?: return null
        if (idx < 0) return null
        return try { getLong(row, idx) } catch (_: Exception) { null }
    }

    private fun CursorWindow.doubleCol(cols: Array<String>?, row: Int, name: String): Double? {
        val idx = cols?.indexOf(name) ?: return null
        if (idx < 0) return null
        return try { getDouble(row, idx) } catch (_: Exception) { null }
    }

    private inline fun <T> HealthDataResolver.ReadResult.mapRows(
        crossinline block: (CursorWindow, Array<String>?, Int) -> T?
    ): List<T> {
        val w = window ?: return emptyList()
        return try {
            (0 until rowCount).mapNotNull { row ->
                try { block(w, columnNames, row) } catch (_: Exception) { null }
            }
        } finally {
            w.close()
        }
    }

    // ── Permission check / request ────────────────────────────────────────────

    suspend fun getPermissionRequestIntentIfNeeded(enabledTypes: Set<HealthDataType>): Intent? = withContext(Dispatchers.IO) {
        val dataTypes = enabledTypes.map { it.toSamsungType() }.filterNotNull().distinct()
        if (dataTypes.isEmpty()) return@withContext null

        val conn = SamsungHealthIHealthConnection()
        try {
            val health = conn.connect() ?: return@withContext null
            val bundle = buildPermissionBundle(dataTypes)

            // Check which permissions are already granted
            val resultBundle = try {
                health.isHealthDataPermissionAcquired2(context.packageName, bundle)
            } catch (_: Exception) { null }

            val allGranted = resultBundle != null && parseAllGranted(resultBundle, dataTypes)
            if (allGranted) return@withContext null

            // Request missing permissions — returns an Intent to launch
            return@withContext try {
                val receiverStub = object : IHealthResultReceiver.Stub() {
                    override fun send(resultCode: Int, resultData: Bundle?) {}
                }
                health.requestHealthDataPermissions2(
                    context.packageName,
                    HealthResultReceiver(receiverStub.asBinder()),
                    bundle
                )
            } catch (_: Exception) { null }
        } finally {
            conn.disconnect()
        }
    }

    private fun HealthDataType.toSamsungType(): String? = when (this) {
        HealthDataType.STEPS -> TYPE_STEPS
        HealthDataType.HEART_RATE -> TYPE_HEART_RATE
        HealthDataType.SLEEP -> TYPE_SLEEP
        HealthDataType.EXERCISE -> TYPE_EXERCISE
        HealthDataType.BLOOD_PRESSURE -> TYPE_BLOOD_PRESSURE
        HealthDataType.BLOOD_GLUCOSE -> TYPE_BLOOD_GLUCOSE
        HealthDataType.OXYGEN_SATURATION -> TYPE_OXYGEN_SATURATION
        HealthDataType.BODY_TEMPERATURE -> TYPE_BODY_TEMPERATURE
        HealthDataType.WEIGHT -> TYPE_WEIGHT
        HealthDataType.HEIGHT -> TYPE_HEIGHT
        HealthDataType.BODY_FAT -> TYPE_BODY_FAT
        HealthDataType.HYDRATION -> TYPE_WATER_INTAKE
        else -> null
    }

    private fun buildPermissionBundle(dataTypes: List<String>): Bundle {
        val permKeys = ArrayList<HealthPermissionManager.PermissionKey>(dataTypes.size)
        for (dt in dataTypes) {
            permKeys.add(HealthPermissionManager.PermissionKey(dt, HealthPermissionManager.PermissionType.READ))
        }
        return Bundle().apply {
            putParcelableArrayList(HealthPermissionManager.BUNDLE_KEY, permKeys)
        }
    }

    private fun parseAllGranted(result: Bundle, requestedTypes: List<String>): Boolean {
        result.classLoader = HealthPermissionManager::class.java.classLoader
        return try {
            val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.getParcelableArrayList(HealthPermissionManager.BUNDLE_KEY, HealthPermissionManager.PermissionResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.getParcelableArrayList<HealthPermissionManager.PermissionResult>(HealthPermissionManager.BUNDLE_KEY)
            }
            if (list.isNullOrEmpty()) false
            else list.all { it.isAcquired }
        } catch (_: Exception) {
            false
        }
    }

    // ── Service binding (IHealth) ─────────────────────────────────────────────

    private inner class SamsungHealthIHealthConnection {
        private var conn: ServiceConnection? = null
        private val deferred = CompletableDeferred<IHealth?>()

        suspend fun connect(): IHealth? {
            conn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    try {
                        deferred.complete(IHealth.Stub.asInterface(service))
                    } catch (e: Exception) {
                        deferred.complete(null)
                    }
                }
                override fun onServiceDisconnected(name: ComponentName) {}
            }
            val intent = Intent().apply {
                component = ComponentName(SHEALTH_PACKAGE, SERVICE_CLASS)
            }
            val bound = withContext(Dispatchers.Main) {
                context.bindService(intent, conn!!, Context.BIND_AUTO_CREATE)
            }
            if (!bound) return null
            return withTimeoutOrNull(15_000L) { deferred.await() }
        }

        fun disconnect() {
            conn?.let { c ->
                try { context.unbindService(c) } catch (_: Exception) {}
                conn = null
            }
        }
    }

    // ── Service binding (IDataResolver) ───────────────────────────────────────

    private inner class SamsungHealthConnection {
        private var conn: ServiceConnection? = null
        private val deferred = CompletableDeferred<IDataResolver?>()

        suspend fun connect(): IDataResolver? {
            conn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    try {
                        val resolver = IHealth.Stub.asInterface(service).getIDataResolver()
                        deferred.complete(resolver)
                    } catch (e: Exception) {
                        deferred.complete(null)
                    }
                }
                override fun onServiceDisconnected(name: ComponentName) {}
            }
            val intent = Intent().apply {
                component = ComponentName(SHEALTH_PACKAGE, SERVICE_CLASS)
            }
            val bound = withContext(Dispatchers.Main) {
                context.bindService(intent, conn!!, Context.BIND_AUTO_CREATE)
            }
            if (!bound) return null
            return withTimeoutOrNull(15_000L) { deferred.await() }
        }

        fun disconnect() {
            conn?.let { c ->
                try { context.unbindService(c) } catch (_: Exception) {}
                conn = null
            }
        }
    }
}
