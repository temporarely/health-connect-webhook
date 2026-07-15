package com.hcwebhook.app

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import kotlinx.coroutines.CancellationException
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import com.hcwebhook.app.dashboard.DashboardFormatter
import com.hcwebhook.app.dashboard.DashboardMetric
import com.hcwebhook.app.dashboard.DashboardSnapshot
import kotlin.reflect.KClass

enum class HealthDataType(val nameResId: Int, val recordClass: KClass<out Record>, val rationaleResId: Int) {
    STEPS(
        R.string.dt_steps_name, StepsRecord::class, R.string.dt_steps_rationale
    ),
    SLEEP(
        R.string.dt_sleep_name, SleepSessionRecord::class, R.string.dt_sleep_rationale
    ),
    HEART_RATE(
        R.string.dt_hr_name, HeartRateRecord::class, R.string.dt_hr_rationale
    ),
    HEART_RATE_VARIABILITY(
        R.string.dt_hrv_name, HeartRateVariabilityRmssdRecord::class, R.string.dt_hrv_rationale
    ),
    DISTANCE(
        R.string.dt_dist_name, DistanceRecord::class, R.string.dt_dist_rationale
    ),
    ACTIVE_CALORIES(
        R.string.dt_act_cal_name, ActiveCaloriesBurnedRecord::class, R.string.dt_act_cal_rationale
    ),
    TOTAL_CALORIES(
        R.string.dt_tot_cal_name, TotalCaloriesBurnedRecord::class, R.string.dt_tot_cal_rationale
    ),
    WEIGHT(
        R.string.dt_weight_name, WeightRecord::class, R.string.dt_weight_rationale
    ),
    HEIGHT(
        R.string.dt_height_name, HeightRecord::class, R.string.dt_height_rationale
    ),
    BLOOD_PRESSURE(
        R.string.dt_bp_name, BloodPressureRecord::class, R.string.dt_bp_rationale
    ),
    BLOOD_GLUCOSE(
        R.string.dt_bg_name, BloodGlucoseRecord::class, R.string.dt_bg_rationale
    ),
    OXYGEN_SATURATION(
        R.string.dt_oxy_name, OxygenSaturationRecord::class, R.string.dt_oxy_rationale
    ),
    BODY_TEMPERATURE(
        R.string.dt_temp_name, BodyTemperatureRecord::class, R.string.dt_temp_rationale
    ),
    SKIN_TEMPERATURE(
        R.string.dt_skin_temp_name, SkinTemperatureRecord::class, R.string.dt_skin_temp_rationale
    ),
    RESPIRATORY_RATE(
        R.string.dt_resp_name, RespiratoryRateRecord::class, R.string.dt_resp_rationale
    ),
    RESTING_HEART_RATE(
        R.string.dt_rhr_name, RestingHeartRateRecord::class, R.string.dt_rhr_rationale
    ),
    EXERCISE(
        R.string.dt_exer_name, ExerciseSessionRecord::class, R.string.dt_exer_rationale
    ),
    HYDRATION(
        R.string.dt_hydr_name, HydrationRecord::class, R.string.dt_hydr_rationale
    ),
    NUTRITION(
        R.string.dt_nutr_name, NutritionRecord::class, R.string.dt_nutr_rationale
    ),
    BASAL_METABOLIC_RATE(
        R.string.dt_bmr_name, BasalMetabolicRateRecord::class, R.string.dt_bmr_rationale
    ),
    BODY_FAT(
        R.string.dt_bf_name, BodyFatRecord::class, R.string.dt_bf_rationale
    ),
    LEAN_BODY_MASS(
        R.string.dt_lbm_name, LeanBodyMassRecord::class, R.string.dt_lbm_rationale
    ),
    VO2_MAX(
        R.string.dt_vo2_name, Vo2MaxRecord::class, R.string.dt_vo2_rationale
    ),
    BONE_MASS(
        R.string.dt_bone_name, BoneMassRecord::class, R.string.dt_bone_rationale
    )
}

data class HealthData(
    val steps: List<StepsData>,
    val sleep: List<SleepData>,
    val heartRate: List<HeartRateData>,
    val heartRateVariability: List<HeartRateVariabilityData>,
    val distance: List<DistanceData>,
    val activeCalories: List<ActiveCaloriesData>,
    val totalCalories: List<TotalCaloriesData>,
    val weight: List<WeightData>,
    val height: List<HeightData>,
    val bloodPressure: List<BloodPressureData>,
    val bloodGlucose: List<BloodGlucoseData>,
    val oxygenSaturation: List<OxygenSaturationData>,
    val bodyTemperature: List<BodyTemperatureData>,
    val skinTemperature: List<SkinTemperatureData>,
    val respiratoryRate: List<RespiratoryRateData>,
    val restingHeartRate: List<RestingHeartRateData>,
    val exercise: List<ExerciseData>,
    val hydration: List<HydrationData>,
    val nutrition: List<NutritionData>,
    val basalMetabolicRate: List<BasalMetabolicRateData>,
    val bodyFat: List<BodyFatData>,
    val leanBodyMass: List<LeanBodyMassData>,
    val vo2Max: List<Vo2MaxData>,
    val boneMass: List<BoneMassData>
)

data class StepsData(
    val count: Long,
    val startTime: Instant,
    val endTime: Instant
)

data class SleepData(
    val sessionEndTime: Instant,
    val duration: Duration,
    val stages: List<SleepStage>
)

data class SleepStage(
    val stage: String,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration
)

data class HeartRateData(
    val bpm: Long,
    val bpmMin: Long,
    val bpmMax: Long,
    val measurementsCount: Long,
    val time: Instant
)

data class HeartRateVariabilityData(
    val rmssdMillis: Double,
    val time: Instant
)

data class DistanceData(
    val meters: Double,
    val startTime: Instant,
    val endTime: Instant
)

data class ActiveCaloriesData(
    val calories: Double,
    val startTime: Instant,
    val endTime: Instant
)

data class TotalCaloriesData(
    val calories: Double,
    val startTime: Instant,
    val endTime: Instant
)

data class WeightData(
    val kilograms: Double,
    val time: Instant
)

data class HeightData(
    val meters: Double,
    val time: Instant
)

data class BloodPressureData(
    val systolic: Double,
    val diastolic: Double,
    val time: Instant
)

data class BloodGlucoseData(
    val mmolPerLiter: Double,
    val time: Instant
)

data class OxygenSaturationData(
    val percentage: Double,
    val time: Instant
)

data class BodyTemperatureData(
    val celsius: Double,
    val time: Instant
)

data class SkinTemperatureData(
    val time: Instant,
    val deltaCelsius: Double,
    val baselineCelsius: Double?,
    val measurementLocation: Int
)

data class RespiratoryRateData(
    val rate: Double,
    val time: Instant
)

data class RestingHeartRateData(
    val bpm: Long,
    val time: Instant
)

data class ExerciseData(
    val type: String,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val distanceMeters: Double? = null,
    val steps: Long? = null,
    val avgCadenceSpm: Double? = null,
    val maxCadenceSpm: Double? = null,
    val strideLengthMeters: Double? = null
)

data class HydrationData(
    val liters: Double,
    val startTime: Instant,
    val endTime: Instant
)

data class NutritionData(
    val calories: Double?,
    val protein: Double?,
    val carbs: Double?,
    val fat: Double?,
    val sugar: Double?,
    val sodium: Double?,
    val dietaryFiber: Double?,
    val name: String?,
    val startTime: Instant,
    val endTime: Instant
)

data class BasalMetabolicRateData(
    val kcalPerDay: Double,
    val time: Instant
)

data class BodyFatData(
    val percentage: Double,
    val time: Instant
)

data class LeanBodyMassData(
    val kilograms: Double,
    val time: Instant
)

data class Vo2MaxData(
    val mlPerKgPerMin: Double,
    val time: Instant
)

data class BoneMassData(
    val kilograms: Double,
    val time: Instant
)

class HealthConnectManager(private val context: Context) {

    private val healthConnectClient by lazy {
        try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            throw IllegalStateException("Health Connect is not available on this device: ${e.message}", e)
        }
    }

    suspend fun readHealthData(
        enabledTypes: Set<HealthDataType>,
        lastSyncTimestamps: Map<HealthDataType, Instant?>,
        timeRangeDays: Int? = null,
        start: Instant? = null,
        end: Instant? = null
    ): Result<HealthData> {
        return try {
            val endTime = end ?: Instant.now()
            val startTime = when {
                start != null -> start
                timeRangeDays != null -> endTime.minus(timeRangeDays.toLong(), ChronoUnit.DAYS)
                else -> endTime.minus(LOOKBACK_HOURS, ChronoUnit.HOURS)
            }

            if (startTime.isAfter(endTime)) {
                return Result.failure(IllegalArgumentException("start must be before or equal to end"))
            }

            val stepsData = if (HealthDataType.STEPS in enabledTypes)
                readSafe("STEPS") { readStepsData(startTime, endTime, lastSyncTimestamps[HealthDataType.STEPS]) } else emptyList()
            val sleepData = if (HealthDataType.SLEEP in enabledTypes)
                readSafe("SLEEP") { readSleepData(startTime, endTime, lastSyncTimestamps[HealthDataType.SLEEP]) } else emptyList()
            val heartRateData = if (HealthDataType.HEART_RATE in enabledTypes)
                readSafe("HEART_RATE") { readHeartRateData(startTime, endTime, lastSyncTimestamps[HealthDataType.HEART_RATE]) } else emptyList()
            val heartRateVariabilityData = if (HealthDataType.HEART_RATE_VARIABILITY in enabledTypes)
                readSafe("HRV") { readHeartRateVariabilityData(startTime, endTime, lastSyncTimestamps[HealthDataType.HEART_RATE_VARIABILITY]) } else emptyList()
            val distanceData = if (HealthDataType.DISTANCE in enabledTypes)
                readSafe("DISTANCE") { readDistanceData(startTime, endTime, lastSyncTimestamps[HealthDataType.DISTANCE]) } else emptyList()
            val activeCaloriesData = if (HealthDataType.ACTIVE_CALORIES in enabledTypes)
                readSafe("ACTIVE_CAL") { readActiveCaloriesData(startTime, endTime, lastSyncTimestamps[HealthDataType.ACTIVE_CALORIES]) } else emptyList()
            val totalCaloriesData = if (HealthDataType.TOTAL_CALORIES in enabledTypes)
                readSafe("TOTAL_CAL") { readTotalCaloriesData(startTime, endTime, lastSyncTimestamps[HealthDataType.TOTAL_CALORIES]) } else emptyList()
            val weightData = if (HealthDataType.WEIGHT in enabledTypes)
                readSafe("WEIGHT") { readWeightData(startTime, endTime, lastSyncTimestamps[HealthDataType.WEIGHT]) } else emptyList()
            val heightData = if (HealthDataType.HEIGHT in enabledTypes)
                readSafe("HEIGHT") { readHeightData(startTime, endTime, lastSyncTimestamps[HealthDataType.HEIGHT]) } else emptyList()
            val bloodPressureData = if (HealthDataType.BLOOD_PRESSURE in enabledTypes)
                readSafe("BLOOD_PRESSURE") { readBloodPressureData(startTime, endTime, lastSyncTimestamps[HealthDataType.BLOOD_PRESSURE]) } else emptyList()
            val bloodGlucoseData = if (HealthDataType.BLOOD_GLUCOSE in enabledTypes)
                readSafe("BLOOD_GLUCOSE") { readBloodGlucoseData(startTime, endTime, lastSyncTimestamps[HealthDataType.BLOOD_GLUCOSE]) } else emptyList()
            val oxygenSaturationData = if (HealthDataType.OXYGEN_SATURATION in enabledTypes)
                readSafe("OXYGEN_SAT") { readOxygenSaturationData(startTime, endTime, lastSyncTimestamps[HealthDataType.OXYGEN_SATURATION]) } else emptyList()
            val bodyTemperatureData = if (HealthDataType.BODY_TEMPERATURE in enabledTypes)
                readSafe("BODY_TEMP") { readBodyTemperatureData(startTime, endTime, lastSyncTimestamps[HealthDataType.BODY_TEMPERATURE]) } else emptyList()
            val skinTemperatureData = if (HealthDataType.SKIN_TEMPERATURE in enabledTypes)
                readSafe("SKIN_TEMP") { readSkinTemperatureData(startTime, endTime, lastSyncTimestamps[HealthDataType.SKIN_TEMPERATURE]) } else emptyList()
            val respiratoryRateData = if (HealthDataType.RESPIRATORY_RATE in enabledTypes)
                readSafe("RESP_RATE") { readRespiratoryRateData(startTime, endTime, lastSyncTimestamps[HealthDataType.RESPIRATORY_RATE]) } else emptyList()
            val restingHeartRateData = if (HealthDataType.RESTING_HEART_RATE in enabledTypes)
                readSafe("RESTING_HR") { readRestingHeartRateData(startTime, endTime, lastSyncTimestamps[HealthDataType.RESTING_HEART_RATE]) } else emptyList()
            val exerciseData = if (HealthDataType.EXERCISE in enabledTypes)
                readSafe("EXERCISE") { readExerciseData(
                    startTime,
                    endTime,
                    lastSyncTimestamps[HealthDataType.EXERCISE],
                    HealthDataType.DISTANCE in enabledTypes,
                    HealthDataType.STEPS in enabledTypes
                ) } else emptyList()
            val hydrationData = if (HealthDataType.HYDRATION in enabledTypes)
                readSafe("HYDRATION") { readHydrationData(startTime, endTime, lastSyncTimestamps[HealthDataType.HYDRATION]) } else emptyList()
            val nutritionData = if (HealthDataType.NUTRITION in enabledTypes)
                readSafe("NUTRITION") { readNutritionData(startTime, endTime, lastSyncTimestamps[HealthDataType.NUTRITION]) } else emptyList()
            val basalMetabolicRateData = if (HealthDataType.BASAL_METABOLIC_RATE in enabledTypes)
                readSafe("BMR") { readBasalMetabolicRateData(startTime, endTime, lastSyncTimestamps[HealthDataType.BASAL_METABOLIC_RATE]) } else emptyList()
            val bodyFatData = if (HealthDataType.BODY_FAT in enabledTypes)
                readSafe("BODY_FAT") { readBodyFatData(startTime, endTime, lastSyncTimestamps[HealthDataType.BODY_FAT]) } else emptyList()
            val leanBodyMassData = if (HealthDataType.LEAN_BODY_MASS in enabledTypes)
                readSafe("LEAN_MASS") { readLeanBodyMassData(startTime, endTime, lastSyncTimestamps[HealthDataType.LEAN_BODY_MASS]) } else emptyList()
            val vo2MaxData = if (HealthDataType.VO2_MAX in enabledTypes)
                readSafe("VO2_MAX") { readVo2MaxData(startTime, endTime, lastSyncTimestamps[HealthDataType.VO2_MAX]) } else emptyList()
            val boneMassData = if (HealthDataType.BONE_MASS in enabledTypes)
                readSafe("BONE_MASS") { readBoneMassData(startTime, endTime, lastSyncTimestamps[HealthDataType.BONE_MASS]) } else emptyList()

            Result.success(HealthData(
                steps = stepsData,
                sleep = sleepData,
                heartRate = heartRateData,
                heartRateVariability = heartRateVariabilityData,
                distance = distanceData,
                activeCalories = activeCaloriesData,
                totalCalories = totalCaloriesData,
                weight = weightData,
                height = heightData,
                bloodPressure = bloodPressureData,
                bloodGlucose = bloodGlucoseData,
                oxygenSaturation = oxygenSaturationData,
                bodyTemperature = bodyTemperatureData,
                skinTemperature = skinTemperatureData,
                respiratoryRate = respiratoryRateData,
                restingHeartRate = restingHeartRateData,
                exercise = exerciseData,
                hydration = hydrationData,
                nutrition = nutritionData,
                basalMetabolicRate = basalMetabolicRateData,
                bodyFat = bodyFatData,
                leanBodyMass = leanBodyMassData,
                vo2Max = vo2MaxData,
                boneMass = boneMassData
            ))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readTodayDashboardStats(grantedPermissions: Set<String>): Result<DashboardSnapshot> {
        return try {
            val prefs = PreferencesManager(context)
            val enabledTypes = prefs.getEnabledDataTypes()

            if (prefs.getDataSource() == DataSource.SAMSUNG_HEALTH) {
                if (enabledTypes.isEmpty()) return Result.success(DashboardSnapshot(emptyList()))
                val zone = ZoneId.systemDefault()
                val dayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant()
                val dayEnd = Instant.now()
                return SamsungHealthManager(context).readHealthData(
                    enabledTypes = enabledTypes,
                    lastSyncTimestamps = emptyMap(),
                    start = dayStart,
                    end = dayEnd
                ).map { healthDataToDashboardSnapshot(it, enabledTypes) }
            }

            val typesToLoad = grantedDataTypes(grantedPermissions).filter { it in enabledTypes }
            if (typesToLoad.isEmpty()) {
                return Result.success(DashboardSnapshot(emptyList()))
            }

            val zone = ZoneId.systemDefault()
            val dayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant()
            val dayEnd = Instant.now()

            val metrics = typesToLoad.map { type ->
                try {
                    readDashboardMetric(type, dayStart, dayEnd)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    DashboardMetric(type, DashboardFormatter.NO_DATA, R.string.dashboard_sub_no_data)
                }
            }

            Result.success(DashboardSnapshot(metrics, Instant.now()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun readDashboardMetric(
        type: HealthDataType,
        dayStart: Instant,
        dayEnd: Instant,
    ): DashboardMetric = when (type) {
        HealthDataType.STEPS -> DashboardMetric(
            type,
            DashboardFormatter.formatSteps(aggregateSteps(dayStart, dayEnd)),
            R.string.dashboard_sub_today,
        )
        HealthDataType.DISTANCE -> DashboardMetric(
            type,
            DashboardFormatter.formatDistanceKm(aggregateDistanceMeters(dayStart, dayEnd)),
            R.string.dashboard_sub_km_today,
        )
        HealthDataType.ACTIVE_CALORIES -> DashboardMetric(
            type,
            DashboardFormatter.formatCalories(aggregateActiveCalories(dayStart, dayEnd)),
            R.string.dashboard_sub_kcal_today,
        )
        HealthDataType.TOTAL_CALORIES -> DashboardMetric(
            type,
            DashboardFormatter.formatCalories(aggregateTotalCalories(dayStart, dayEnd)),
            R.string.dashboard_sub_kcal_today,
        )
        HealthDataType.SLEEP -> {
            val duration = aggregateSleepDuration(dayStart, dayEnd)
            DashboardMetric(
                type,
                DashboardFormatter.formatDurationMinutes(
                    DashboardFormatter.sumDurationsMinutes(listOf(duration)),
                ),
                R.string.dashboard_sub_today,
            )
        }
        HealthDataType.HEART_RATE -> {
            val avg = averageHeartRateBpm(dayStart, dayEnd)
            DashboardMetric(
                type,
                DashboardFormatter.formatOptional(avg, DashboardFormatter::formatAverageBpm),
                if (avg != null) R.string.dashboard_sub_avg_bpm else R.string.dashboard_sub_no_data,
            )
        }
        HealthDataType.HEART_RATE_VARIABILITY -> {
            val avg = averageHeartRateVariabilityMs(dayStart, dayEnd)
            DashboardMetric(
                type,
                DashboardFormatter.formatOptional(avg, DashboardFormatter::formatHeartRateVariabilityMs),
                if (avg != null) R.string.dashboard_sub_ms_avg else R.string.dashboard_sub_no_data,
            )
        }
        HealthDataType.WEIGHT -> latestMetric(
            type,
            readWeightData(dayStart, dayEnd, null).maxByOrNull { it.time }?.kilograms,
            DashboardFormatter::formatWeightKg,
            R.string.dashboard_sub_kg_latest,
        )
        HealthDataType.HEIGHT -> latestMetric(
            type,
            readHeightData(dayStart, dayEnd, null).maxByOrNull { it.time }?.meters,
            DashboardFormatter::formatHeightCm,
            R.string.dashboard_sub_cm_latest,
        )
        HealthDataType.BLOOD_PRESSURE -> {
            val latest = readBloodPressureData(dayStart, dayEnd, null).maxByOrNull { it.time }
            DashboardMetric(
                type,
                if (latest != null) {
                    DashboardFormatter.formatBloodPressure(latest.systolic, latest.diastolic)
                } else {
                    DashboardFormatter.NO_DATA
                },
                if (latest != null) R.string.dashboard_sub_mmhg_latest else R.string.dashboard_sub_no_data,
            )
        }
        HealthDataType.BLOOD_GLUCOSE -> latestMetric(
            type,
            readBloodGlucoseData(dayStart, dayEnd, null).maxByOrNull { it.time }?.mmolPerLiter,
            DashboardFormatter::formatBloodGlucose,
            R.string.dashboard_sub_mmol_latest,
        )
        HealthDataType.OXYGEN_SATURATION -> latestMetric(
            type,
            readOxygenSaturationData(dayStart, dayEnd, null).maxByOrNull { it.time }?.percentage,
            DashboardFormatter::formatPercentage,
            R.string.dashboard_sub_percent_latest,
        )
        HealthDataType.BODY_TEMPERATURE -> latestMetric(
            type,
            readBodyTemperatureData(dayStart, dayEnd, null).maxByOrNull { it.time }?.celsius,
            DashboardFormatter::formatCelsius,
            R.string.dashboard_sub_celsius_latest,
        )
        HealthDataType.SKIN_TEMPERATURE -> {
            val latest = readSkinTemperatureData(dayStart, dayEnd, null).maxByOrNull { it.time }
            DashboardMetric(
                type,
                if (latest != null) {
                    DashboardFormatter.formatSignedCelsius(latest.deltaCelsius)
                } else {
                    DashboardFormatter.NO_DATA
                },
                if (latest != null) R.string.dashboard_sub_delta_celsius else R.string.dashboard_sub_no_data,
            )
        }
        HealthDataType.RESPIRATORY_RATE -> {
            val avg = averageRespiratoryRate(dayStart, dayEnd)
            DashboardMetric(
                type,
                DashboardFormatter.formatOptional(avg, DashboardFormatter::formatRespiratoryRate),
                if (avg != null) R.string.dashboard_sub_per_min_avg else R.string.dashboard_sub_no_data,
            )
        }
        HealthDataType.RESTING_HEART_RATE -> latestMetric(
            type,
            readRestingHeartRateData(dayStart, dayEnd, null).maxByOrNull { it.time }?.bpm?.toDouble(),
            DashboardFormatter::formatAverageBpm,
            R.string.dashboard_sub_bpm_latest,
        )
        HealthDataType.EXERCISE -> {
            val sessions = readExerciseData(dayStart, dayEnd, null, includeDistance = false, includeSteps = false)
            val totalMinutes = DashboardFormatter.sumDurationsMinutes(sessions.map { it.duration })
            DashboardMetric(
                type,
                if (sessions.isEmpty()) {
                    DashboardFormatter.formatDurationMinutes(0)
                } else {
                    DashboardFormatter.formatDurationMinutes(totalMinutes)
                },
                R.string.dashboard_sub_sessions_today,
            )
        }
        HealthDataType.HYDRATION -> DashboardMetric(
            type,
            DashboardFormatter.formatLiters(
                readHydrationData(dayStart, dayEnd, null).sumOf { it.liters },
            ),
            R.string.dashboard_sub_liters_today,
        )
        HealthDataType.NUTRITION -> DashboardMetric(
            type,
            DashboardFormatter.formatCalories(
                readNutritionData(dayStart, dayEnd, null).mapNotNull { it.calories }.sum(),
            ),
            R.string.dashboard_sub_kcal_today,
        )
        HealthDataType.BASAL_METABOLIC_RATE -> {
            val kcal = readBasalMetabolicRateData(dayStart, dayEnd, null).sumOf { it.kcalPerDay }.takeIf { it > 0.0 }
            latestMetric(type, kcal, DashboardFormatter::formatCalories, R.string.dashboard_sub_kcal_today)
        }
        HealthDataType.BODY_FAT -> latestMetric(
            type,
            readBodyFatData(dayStart, dayEnd, null).maxByOrNull { it.time }?.percentage,
            DashboardFormatter::formatPercentage,
            R.string.dashboard_sub_percent_latest,
        )
        HealthDataType.LEAN_BODY_MASS -> latestMetric(
            type,
            readLeanBodyMassData(dayStart, dayEnd, null).maxByOrNull { it.time }?.kilograms,
            DashboardFormatter::formatWeightKg,
            R.string.dashboard_sub_kg_latest,
        )
        HealthDataType.VO2_MAX -> latestMetric(
            type,
            readVo2MaxData(dayStart, dayEnd, null).maxByOrNull { it.time }?.mlPerKgPerMin,
            DashboardFormatter::formatVo2Max,
            R.string.dashboard_sub_vo2_latest,
        )
        HealthDataType.BONE_MASS -> latestMetric(
            type,
            readBoneMassData(dayStart, dayEnd, null).maxByOrNull { it.time }?.kilograms,
            DashboardFormatter::formatWeightKg,
            R.string.dashboard_sub_kg_latest,
        )
    }

    private fun healthDataToDashboardSnapshot(data: HealthData, enabledTypes: Set<HealthDataType>): DashboardSnapshot {
        val metrics = enabledTypes.mapNotNull { type ->
            try {
                when (type) {
                    HealthDataType.STEPS -> DashboardMetric(
                        type, DashboardFormatter.formatSteps(data.steps.sumOf { it.count }), R.string.dashboard_sub_today)
                    HealthDataType.SLEEP -> DashboardMetric(
                        type, DashboardFormatter.formatDurationMinutes(DashboardFormatter.sumDurationsMinutes(data.sleep.map { it.duration })), R.string.dashboard_sub_today)
                    HealthDataType.HEART_RATE -> {
                        val avg = data.heartRate.map { it.bpm.toDouble() }.average().takeIf { data.heartRate.isNotEmpty() }
                        DashboardMetric(type, DashboardFormatter.formatOptional(avg, DashboardFormatter::formatAverageBpm), if (avg != null) R.string.dashboard_sub_avg_bpm else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.HEART_RATE_VARIABILITY -> {
                        val avg = data.heartRateVariability.map { it.rmssdMillis }.average().takeIf { data.heartRateVariability.isNotEmpty() }
                        DashboardMetric(type, DashboardFormatter.formatOptional(avg, DashboardFormatter::formatHeartRateVariabilityMs), if (avg != null) R.string.dashboard_sub_ms_avg else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.DISTANCE -> DashboardMetric(
                        type, DashboardFormatter.formatDistanceKm(data.distance.sumOf { it.meters }), R.string.dashboard_sub_km_today)
                    HealthDataType.ACTIVE_CALORIES -> DashboardMetric(
                        type, DashboardFormatter.formatCalories(data.activeCalories.sumOf { it.calories }), R.string.dashboard_sub_kcal_today)
                    HealthDataType.TOTAL_CALORIES -> DashboardMetric(
                        type, DashboardFormatter.formatCalories(data.totalCalories.sumOf { it.calories }), R.string.dashboard_sub_kcal_today)
                    HealthDataType.WEIGHT -> {
                        val v = data.weight.maxByOrNull { it.time }?.kilograms
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatWeightKg), if (v != null) R.string.dashboard_sub_kg_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.HEIGHT -> {
                        val v = data.height.maxByOrNull { it.time }?.meters
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatHeightCm), if (v != null) R.string.dashboard_sub_cm_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.BLOOD_PRESSURE -> {
                        val v = data.bloodPressure.maxByOrNull { it.time }
                        DashboardMetric(type, if (v != null) DashboardFormatter.formatBloodPressure(v.systolic, v.diastolic) else DashboardFormatter.NO_DATA, if (v != null) R.string.dashboard_sub_mmhg_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.BLOOD_GLUCOSE -> {
                        val v = data.bloodGlucose.maxByOrNull { it.time }?.mmolPerLiter
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatBloodGlucose), if (v != null) R.string.dashboard_sub_mmol_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.OXYGEN_SATURATION -> {
                        val v = data.oxygenSaturation.maxByOrNull { it.time }?.percentage
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatPercentage), if (v != null) R.string.dashboard_sub_percent_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.BODY_TEMPERATURE -> {
                        val v = data.bodyTemperature.maxByOrNull { it.time }?.celsius
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatCelsius), if (v != null) R.string.dashboard_sub_celsius_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.SKIN_TEMPERATURE -> {
                        val v = data.skinTemperature.maxByOrNull { it.time }
                        DashboardMetric(type, if (v != null) DashboardFormatter.formatSignedCelsius(v.deltaCelsius) else DashboardFormatter.NO_DATA, if (v != null) R.string.dashboard_sub_delta_celsius else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.RESPIRATORY_RATE -> {
                        val avg = data.respiratoryRate.map { it.rate }.average().takeIf { data.respiratoryRate.isNotEmpty() }
                        DashboardMetric(type, DashboardFormatter.formatOptional(avg, DashboardFormatter::formatRespiratoryRate), if (avg != null) R.string.dashboard_sub_per_min_avg else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.RESTING_HEART_RATE -> {
                        val v = data.restingHeartRate.maxByOrNull { it.time }?.bpm?.toDouble()
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatAverageBpm), if (v != null) R.string.dashboard_sub_bpm_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.EXERCISE -> DashboardMetric(
                        type, DashboardFormatter.formatDurationMinutes(DashboardFormatter.sumDurationsMinutes(data.exercise.map { it.duration })), R.string.dashboard_sub_sessions_today)
                    HealthDataType.HYDRATION -> DashboardMetric(
                        type, DashboardFormatter.formatLiters(data.hydration.sumOf { it.liters }), R.string.dashboard_sub_liters_today)
                    HealthDataType.NUTRITION -> DashboardMetric(
                        type, DashboardFormatter.formatCalories(data.nutrition.mapNotNull { it.calories }.sum()), R.string.dashboard_sub_kcal_today)
                    HealthDataType.BASAL_METABOLIC_RATE -> {
                        val v = data.basalMetabolicRate.sumOf { it.kcalPerDay }.takeIf { it > 0.0 }
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatCalories), if (v != null) R.string.dashboard_sub_kcal_today else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.BODY_FAT -> {
                        val v = data.bodyFat.maxByOrNull { it.time }?.percentage
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatPercentage), if (v != null) R.string.dashboard_sub_percent_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.LEAN_BODY_MASS -> {
                        val v = data.leanBodyMass.maxByOrNull { it.time }?.kilograms
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatWeightKg), if (v != null) R.string.dashboard_sub_kg_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.VO2_MAX -> {
                        val v = data.vo2Max.maxByOrNull { it.time }?.mlPerKgPerMin
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatVo2Max), if (v != null) R.string.dashboard_sub_vo2_latest else R.string.dashboard_sub_no_data)
                    }
                    HealthDataType.BONE_MASS -> {
                        val v = data.boneMass.maxByOrNull { it.time }?.kilograms
                        DashboardMetric(type, DashboardFormatter.formatOptional(v, DashboardFormatter::formatWeightKg), if (v != null) R.string.dashboard_sub_kg_latest else R.string.dashboard_sub_no_data)
                    }
                }
            } catch (_: Exception) {
                DashboardMetric(type, DashboardFormatter.NO_DATA, R.string.dashboard_sub_no_data)
            }
        }
        return DashboardSnapshot(metrics, Instant.now())
    }

    private fun latestMetric(
        type: HealthDataType,
        value: Double?,
        formatter: (Double) -> String,
        subtitleResId: Int,
    ): DashboardMetric = DashboardMetric(
        type,
        DashboardFormatter.formatOptional(value, formatter),
        if (value != null) subtitleResId else R.string.dashboard_sub_no_data,
    )

    private suspend fun aggregateSleepDuration(startTime: Instant, endTime: Instant): Duration {
        val request = AggregateRequest(
            metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        return aggregateRL(request)[SleepSessionRecord.SLEEP_DURATION_TOTAL] ?: Duration.ZERO
    }

    private suspend fun averageHeartRateBpm(startTime: Instant, endTime: Instant): Double? {
        val request = AggregateRequest(
            metrics = setOf(HeartRateRecord.BPM_AVG),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        return aggregateRL(request)[HeartRateRecord.BPM_AVG]?.toDouble()
    }

    private suspend fun averageHeartRateVariabilityMs(startTime: Instant, endTime: Instant): Double? {
        val request = ReadRecordsRequest(
            recordType = HeartRateVariabilityRmssdRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val records = readAllRecords(request)
        if (records.isEmpty()) return null
        return records.map { it.heartRateVariabilityMillis }.average()
    }

    private suspend fun averageRespiratoryRate(startTime: Instant, endTime: Instant): Double? {
        val request = ReadRecordsRequest(
            recordType = RespiratoryRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val records = readAllRecords(request)
        if (records.isEmpty()) return null
        return records.map { it.rate }.average()
    }

    private suspend fun aggregateTotalCalories(startTime: Instant, endTime: Instant): Double {
        return readTotalCaloriesData(startTime, endTime, null).sumOf { it.calories }
    }

    private suspend fun readStepsData(
        startTime: Instant,
        endTime: Instant,
        lastSync: Instant?
    ): List<StepsData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { StepsData(it.count, it.startTime, it.endTime) }
        }
        val zone = java.time.ZoneId.systemDefault()
        val result = mutableListOf<StepsData>()

        val startLocalDate = startTime.atZone(zone).toLocalDate()
        val endLocalDate = endTime.atZone(zone).toLocalDate()

        var currentDate = startLocalDate
        while (!currentDate.isAfter(endLocalDate)) {
            val dayStart = currentDate.atStartOfDay(zone).toInstant()
            val dayEnd = currentDate.plusDays(1).atStartOfDay(zone).toInstant()

            // Clamp to the actual lookback window
            val queryStart = if (dayStart.isBefore(startTime)) startTime else dayStart
            val queryEnd = if (dayEnd.isAfter(endTime)) endTime else dayEnd

            // Skip days entirely before lastSync
            if (lastSync != null && queryEnd.isBefore(lastSync)) {
                currentDate = currentDate.plusDays(1)
                continue
            }

            val aggregateRequest = AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val daySteps = aggregateRL(aggregateRequest)[StepsRecord.COUNT_TOTAL] ?: 0L

            if (daySteps > 0L) {
                result.add(StepsData(
                    count = daySteps,
                    startTime = dayStart,
                    endTime = queryEnd
                ))
            }

            currentDate = currentDate.plusDays(1)
        }

        return result
    }

    private suspend fun readSleepData(
        startTime: Instant,
        endTime: Instant,
        lastSync: Instant?
    ): List<SleepData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { record ->
                    SleepData(
                        sessionEndTime = record.endTime,
                        duration = Duration.between(record.startTime, record.endTime),
                        stages = record.stages.map { stage ->
                            SleepStage(
                                stage = stage.stage.toString(),
                                startTime = stage.startTime,
                                endTime = stage.endTime,
                                duration = Duration.between(stage.startTime, stage.endTime)
                            )
                        }
                    )
                }
        }
        val result = mutableListOf<SleepData>()
        forEachDay(startTime, endTime, lastSync) { _, queryStart, queryEnd ->
            val duration = aggregateSleepDuration(queryStart, queryEnd)
            if (!duration.isZero) {
                result.add(SleepData(sessionEndTime = queryEnd, duration = duration, stages = emptyList()))
            }
        }
        return result
    }

    private suspend fun readHeartRateData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<HeartRateData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request).flatMap { record ->
                record.samples
                    .filter { lastSync == null || it.time >= lastSync }
                    .map { sample ->
                        HeartRateData(
                            bpm = sample.beatsPerMinute,
                            bpmMin = sample.beatsPerMinute,
                            bpmMax = sample.beatsPerMinute,
                            measurementsCount = 1L,
                            time = sample.time
                        )
                    }
            }
        }
        val result = mutableListOf<HeartRateData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(
                    HeartRateRecord.BPM_AVG,
                    HeartRateRecord.BPM_MIN,
                    HeartRateRecord.BPM_MAX,
                    HeartRateRecord.MEASUREMENTS_COUNT,
                ),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val response = aggregateRL(request)
            val bpm = response[HeartRateRecord.BPM_AVG]
            if (bpm != null && bpm > 0L) result.add(HeartRateData(
                bpm = bpm,
                bpmMin = response[HeartRateRecord.BPM_MIN] ?: bpm,
                bpmMax = response[HeartRateRecord.BPM_MAX] ?: bpm,
                measurementsCount = response[HeartRateRecord.MEASUREMENTS_COUNT] ?: 1L,
                time = dayStart,
            ))
        }
        return result
    }

    private suspend fun readHeartRateVariabilityData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<HeartRateVariabilityData> {
        val request = ReadRecordsRequest(recordType = HeartRateVariabilityRmssdRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { HeartRateVariabilityData(it.heartRateVariabilityMillis, it.time) }
    }

    private suspend fun readDistanceData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<DistanceData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = DistanceRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { DistanceData(it.distance.inMeters, it.startTime, it.endTime) }
        }
        val zone = java.time.ZoneId.systemDefault()
        val result = mutableListOf<DistanceData>()

        val startLocalDate = startTime.atZone(zone).toLocalDate()
        val endLocalDate = endTime.atZone(zone).toLocalDate()

        var currentDate = startLocalDate
        while (!currentDate.isAfter(endLocalDate)) {
            val dayStart = currentDate.atStartOfDay(zone).toInstant()
            val dayEnd = currentDate.plusDays(1).atStartOfDay(zone).toInstant()

            val queryStart = if (dayStart.isBefore(startTime)) startTime else dayStart
            val queryEnd = if (dayEnd.isAfter(endTime)) endTime else dayEnd

            if (lastSync != null && queryEnd.isBefore(lastSync)) {
                currentDate = currentDate.plusDays(1)
                continue
            }

            val aggregateRequest = AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val dayDistance = aggregateRL(aggregateRequest)[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0

            if (dayDistance > 0.0) {
                result.add(DistanceData(
                    meters = dayDistance,
                    startTime = dayStart,
                    endTime = queryEnd
                ))
            }

            currentDate = currentDate.plusDays(1)
        }

        return result
    }

    private suspend fun readActiveCaloriesData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<ActiveCaloriesData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { ActiveCaloriesData(it.energy.inKilocalories, it.startTime, it.endTime) }
        }
        val zone = java.time.ZoneId.systemDefault()
        val result = mutableListOf<ActiveCaloriesData>()

        val startLocalDate = startTime.atZone(zone).toLocalDate()
        val endLocalDate = endTime.atZone(zone).toLocalDate()

        var currentDate = startLocalDate
        while (!currentDate.isAfter(endLocalDate)) {
            val dayStart = currentDate.atStartOfDay(zone).toInstant()
            val dayEnd = currentDate.plusDays(1).atStartOfDay(zone).toInstant()

            val queryStart = if (dayStart.isBefore(startTime)) startTime else dayStart
            val queryEnd = if (dayEnd.isAfter(endTime)) endTime else dayEnd

            if (lastSync != null && queryEnd.isBefore(lastSync)) {
                currentDate = currentDate.plusDays(1)
                continue
            }

            val aggregateRequest = AggregateRequest(
                metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val dayCalories = aggregateRL(aggregateRequest)[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0

            if (dayCalories > 0.0) {
                result.add(ActiveCaloriesData(
                    calories = dayCalories,
                    startTime = dayStart,
                    endTime = queryEnd
                ))
            }

            currentDate = currentDate.plusDays(1)
        }

        return result
    }

    private suspend fun readTotalCaloriesData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<TotalCaloriesData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = TotalCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { TotalCaloriesData(it.energy.inKilocalories, it.startTime, it.endTime) }
        }
        val result = mutableListOf<TotalCaloriesData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val calories = aggregateRL(request)[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
            if (calories > 0.0) result.add(TotalCaloriesData(calories, dayStart, queryEnd))
        }
        return result
    }

    private suspend fun readWeightData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<WeightData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.time >= lastSync }
                .map { WeightData(it.weight.inKilograms, it.time) }
        }
        val result = mutableListOf<WeightData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(WeightRecord.WEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val kg = aggregateRL(request)[WeightRecord.WEIGHT_AVG]?.inKilograms
            if (kg != null && kg > 0.0) result.add(WeightData(kg, dayStart))
        }
        return result
    }

    private suspend fun readHeightData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<HeightData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = HeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.time >= lastSync }
                .map { HeightData(it.height.inMeters, it.time) }
        }
        val result = mutableListOf<HeightData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(HeightRecord.HEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val meters = aggregateRL(request)[HeightRecord.HEIGHT_AVG]?.inMeters
            if (meters != null && meters > 0.0) result.add(HeightData(meters, dayStart))
        }
        return result
    }

    private suspend fun readBloodPressureData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<BloodPressureData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = BloodPressureRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.time >= lastSync }
                .map { BloodPressureData(it.systolic.inMillimetersOfMercury, it.diastolic.inMillimetersOfMercury, it.time) }
        }
        val result = mutableListOf<BloodPressureData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(BloodPressureRecord.SYSTOLIC_AVG, BloodPressureRecord.DIASTOLIC_AVG),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val response = aggregateRL(request)
            val systolic = response[BloodPressureRecord.SYSTOLIC_AVG]?.inMillimetersOfMercury
            val diastolic = response[BloodPressureRecord.DIASTOLIC_AVG]?.inMillimetersOfMercury
            if (systolic != null && diastolic != null) result.add(BloodPressureData(systolic, diastolic, dayStart))
        }
        return result
    }

    private suspend fun readBloodGlucoseData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<BloodGlucoseData> {
        val request = ReadRecordsRequest(recordType = BloodGlucoseRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { BloodGlucoseData(it.level.inMillimolesPerLiter, it.time) }
    }

    private suspend fun readOxygenSaturationData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<OxygenSaturationData> {
        val request = ReadRecordsRequest(recordType = OxygenSaturationRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { OxygenSaturationData(it.percentage.value, it.time) }
    }

    private suspend fun readBodyTemperatureData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<BodyTemperatureData> {
        val request = ReadRecordsRequest(recordType = BodyTemperatureRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { BodyTemperatureData(it.temperature.inCelsius, it.time) }
    }

    private suspend fun readSkinTemperatureData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<SkinTemperatureData> {
        val request = ReadRecordsRequest(recordType = SkinTemperatureRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        val response = readAllRecords(request)
        return response.flatMap { record ->
            record.deltas
                .filter { lastSync == null || it.time >= lastSync }
                .map { delta ->
                    SkinTemperatureData(
                        time = delta.time,
                        deltaCelsius = delta.delta.inCelsius,
                        baselineCelsius = record.baseline?.inCelsius,
                        measurementLocation = record.measurementLocation
                    )
                }
        }
    }

    private suspend fun readRespiratoryRateData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<RespiratoryRateData> {
        val request = ReadRecordsRequest(recordType = RespiratoryRateRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { RespiratoryRateData(it.rate, it.time) }
    }

    private suspend fun readRestingHeartRateData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<RestingHeartRateData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = RestingHeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.time >= lastSync }
                .map { RestingHeartRateData(it.beatsPerMinute, it.time) }
        }
        val result = mutableListOf<RestingHeartRateData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(RestingHeartRateRecord.BPM_AVG),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val bpm = aggregateRL(request)[RestingHeartRateRecord.BPM_AVG]
            if (bpm != null && bpm > 0L) result.add(RestingHeartRateData(bpm, dayStart))
        }
        return result
    }

    private suspend fun readExerciseData(
        startTime: Instant,
        endTime: Instant,
        lastSync: Instant?,
        includeDistance: Boolean,
        includeSteps: Boolean
    ): List<ExerciseData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { session ->
                    val typeStr = ExerciseSessionRecord.EXERCISE_TYPE_STRING_TO_INT_MAP.entries
                        .firstOrNull { it.value == session.exerciseType }?.key
                        ?: "type_${session.exerciseType}"
                    ExerciseData(
                        type = typeStr,
                        startTime = session.startTime,
                        endTime = session.endTime,
                        duration = Duration.between(session.startTime, session.endTime),
                    )
                }
        }
        val result = mutableListOf<ExerciseData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(ExerciseSessionRecord.EXERCISE_DURATION_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val duration = aggregateRL(request)[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL]
            if (duration != null && !duration.isZero) {
                result.add(ExerciseData(
                    type = "DAILY_TOTAL",
                    startTime = dayStart,
                    endTime = queryEnd,
                    duration = duration
                ))
            }
        }
        return result
    }

    private suspend fun aggregateSteps(startTime: Instant, endTime: Instant): Long {
        val request = AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        return aggregateRL(request)[StepsRecord.COUNT_TOTAL] ?: 0L
    }

    private suspend fun aggregateDistanceMeters(startTime: Instant, endTime: Instant): Double {
        val request = AggregateRequest(
            metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        return aggregateRL(request)[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
    }

    private suspend fun aggregateActiveCalories(startTime: Instant, endTime: Instant): Double {
        val request = AggregateRequest(
            metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        return aggregateRL(request)[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]
            ?.inKilocalories ?: 0.0
    }

    private suspend fun readHydrationData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<HydrationData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { HydrationData(it.volume.inLiters, it.startTime, it.endTime) }
        }
        val result = mutableListOf<HydrationData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(HydrationRecord.VOLUME_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val liters = aggregateRL(request)[HydrationRecord.VOLUME_TOTAL]?.inLiters ?: 0.0
            if (liters > 0.0) result.add(HydrationData(liters, dayStart, queryEnd))
        }
        return result
    }

    private suspend fun readNutritionData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<NutritionData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = NutritionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.endTime >= lastSync }
                .map { record ->
                    NutritionData(
                        calories = record.energy?.inKilocalories,
                        protein = record.protein?.inGrams,
                        carbs = record.totalCarbohydrate?.inGrams,
                        fat = record.totalFat?.inGrams,
                        sugar = record.sugar?.inGrams,
                        sodium = record.sodium?.inGrams,
                        dietaryFiber = record.dietaryFiber?.inGrams,
                        name = record.name,
                        startTime = record.startTime,
                        endTime = record.endTime
                    )
                }
        }
        val result = mutableListOf<NutritionData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(
                    NutritionRecord.ENERGY_TOTAL,
                    NutritionRecord.PROTEIN_TOTAL,
                    NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL,
                    NutritionRecord.TOTAL_FAT_TOTAL,
                    NutritionRecord.SUGAR_TOTAL,
                    NutritionRecord.SODIUM_TOTAL,
                    NutritionRecord.DIETARY_FIBER_TOTAL,
                ),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val response = aggregateRL(request)
            val calories = response[NutritionRecord.ENERGY_TOTAL]?.inKilocalories
            val protein = response[NutritionRecord.PROTEIN_TOTAL]?.inGrams
            val carbs = response[NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL]?.inGrams
            val fat = response[NutritionRecord.TOTAL_FAT_TOTAL]?.inGrams
            val sugar = response[NutritionRecord.SUGAR_TOTAL]?.inGrams
            val sodium = response[NutritionRecord.SODIUM_TOTAL]?.inGrams
            val dietaryFiber = response[NutritionRecord.DIETARY_FIBER_TOTAL]?.inGrams
            if (calories != null || protein != null || carbs != null || fat != null) {
                result.add(NutritionData(
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    sugar = sugar,
                    sodium = sodium,
                    dietaryFiber = dietaryFiber,
                    name = null,
                    startTime = dayStart,
                    endTime = queryEnd
                ))
            }
        }
        return result
    }

    private suspend fun readBasalMetabolicRateData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<BasalMetabolicRateData> {
        if (useRawRecords()) {
            val request = ReadRecordsRequest(
                recordType = BasalMetabolicRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            return readAllRecords(request)
                .filter { lastSync == null || it.time >= lastSync }
                .map { BasalMetabolicRateData(it.basalMetabolicRate.inKilocaloriesPerDay, it.time) }
        }
        val result = mutableListOf<BasalMetabolicRateData>()
        forEachDay(startTime, endTime, lastSync) { dayStart, queryStart, queryEnd ->
            val request = AggregateRequest(
                metrics = setOf(BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(queryStart, queryEnd)
            )
            val kcal = aggregateRL(request)[BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL]?.inKilocalories ?: 0.0
            if (kcal > 0.0) result.add(BasalMetabolicRateData(kcal, dayStart))
        }
        return result
    }

    private suspend fun readBodyFatData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<BodyFatData> {
        val request = ReadRecordsRequest(recordType = BodyFatRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { BodyFatData(it.percentage.value, it.time) }
    }

    private suspend fun readLeanBodyMassData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<LeanBodyMassData> {
        val request = ReadRecordsRequest(recordType = LeanBodyMassRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { LeanBodyMassData(it.mass.inKilograms, it.time) }
    }

    private suspend fun readVo2MaxData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<Vo2MaxData> {
        val request = ReadRecordsRequest(recordType = Vo2MaxRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { Vo2MaxData(it.vo2MillilitersPerMinuteKilogram, it.time) }
    }

    private suspend fun readBoneMassData(startTime: Instant, endTime: Instant, lastSync: Instant?): List<BoneMassData> {
        val request = ReadRecordsRequest(recordType = BoneMassRecord::class, timeRangeFilter = TimeRangeFilter.between(startTime, endTime))
        return readAllRecords(request)
            .filter { lastSync == null || it.time >= lastSync }
            .map { BoneMassData(it.mass.inKilograms, it.time) }
    }

    /**
     * Retries a Health Connect call when the platform throws its rate-limit /
     * quota-exceeded error, backing off exponentially (1s, 2s, 4s, 8s). Any other
     * error propagates immediately. This mitigates the periodic (burst) quota; the
     * separate daily quota still needs real time to replenish, so after the last
     * attempt the original exception is rethrown for the UI to surface.
     */
    private suspend fun <T> withHealthConnectRetry(maxAttempts: Int = 4, block: suspend () -> T): T {
        var attempt = 0
        var delayMs = 1_000L
        while (true) {
            try {
                return kotlinx.coroutines.withTimeoutOrNull(10_000L) { block() }
                    ?: throw Exception("Health Connect IPC timed out after 10s")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = e.message?.lowercase().orEmpty()
                val isRateLimit = msg.contains("rate limit") || msg.contains("quota")
                attempt++
                if (!isRateLimit || attempt >= maxAttempts) throw e
                kotlinx.coroutines.delay(delayMs)
                delayMs = (delayMs * 2).coerceAtMost(8_000L)
            }
        }
    }

    private suspend fun aggregateRL(request: AggregateRequest) =
        withHealthConnectRetry { healthConnectClient.aggregate(request) }

    private suspend fun <T : Record> readAllRecords(request: ReadRecordsRequest<T>): List<T> {
        val all = mutableListOf<T>()
        var token: String? = null
        var firstPage = true
        do {
            // Gentle inter-page throttle so a multi-page read (e.g. heart rate over a
            // long window) doesn't trip Health Connect's burst quota in the first place.
            if (!firstPage) kotlinx.coroutines.delay(120)
            firstPage = false
            val current = if (token == null) request else ReadRecordsRequest(
                recordType = request.recordType,
                timeRangeFilter = request.timeRangeFilter,
                dataOriginFilter = request.dataOriginFilter,
                ascendingOrder = request.ascendingOrder,
                pageSize = request.pageSize,
                pageToken = token,
            )
            val page = withHealthConnectRetry { healthConnectClient.readRecords(current) }
            all.addAll(page.records)
            token = page.pageToken
        } while (token != null)
        return all
    }

    private fun useRawRecords() = PreferencesManager(context).isUseRawRecordsEnabled()

    private suspend fun <T> readSafe(label: String = "?", block: suspend () -> List<T>): List<T> =
        try {
            kotlinx.coroutines.withTimeoutOrNull(15_000L) { block() }
                ?: run { android.util.Log.w("HCSync", "timeout: $label"); emptyList() }
        }
        catch (e: CancellationException) { throw e }
        catch (e: Exception) { android.util.Log.w("HCSync", "error: $label — ${e.message}"); emptyList() }

    private suspend fun forEachDay(
        startTime: Instant,
        endTime: Instant,
        lastSync: Instant?,
        block: suspend (dayStart: Instant, queryStart: Instant, queryEnd: Instant) -> Unit
    ) {
        val zone = ZoneId.systemDefault()
        var currentDate = startTime.atZone(zone).toLocalDate()
        val endLocalDate = endTime.atZone(zone).toLocalDate()
        while (!currentDate.isAfter(endLocalDate)) {
            val dayStart = currentDate.atStartOfDay(zone).toInstant()
            val dayEnd = currentDate.plusDays(1).atStartOfDay(zone).toInstant()
            val queryStart = if (dayStart.isBefore(startTime)) startTime else dayStart
            val queryEnd = if (dayEnd.isAfter(endTime)) endTime else dayEnd
            if (lastSync == null || !queryEnd.isBefore(lastSync)) {
                block(dayStart, queryStart, queryEnd)
            }
            currentDate = currentDate.plusDays(1)
        }
    }

    fun isHealthConnectAvailable(): Boolean {
        return try {
            HealthConnectClient.getOrCreate(context)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun hasPermissions(requiredPermissions: Set<String> = ALL_PERMISSIONS): Boolean {
        if (!isHealthConnectAvailable()) return false
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return requiredPermissions.all { it in granted }
    }

    suspend fun getGrantedPermissions(): Set<String> {
        if (!isHealthConnectAvailable()) return emptySet()
        return healthConnectClient.permissionController.getGrantedPermissions()
    }

    suspend fun requestPermissions(permissions: Set<String>): android.content.Intent {
        if (!isHealthConnectAvailable()) {
            throw IllegalStateException("Health Connect is not available on this device")
        }
        val contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
        return contract.createIntent(context, permissions.toTypedArray())
    }

    companion object {
        private const val LOOKBACK_HOURS = 48L

        fun grantedDataTypes(grantedPermissions: Set<String>): List<HealthDataType> =
            HealthDataType.entries.filter { type ->
                HealthPermission.getReadPermission(type.recordClass) in grantedPermissions
            }

        fun getPermissionsForTypes(
            types: Set<HealthDataType>,
            includeBackgroundPermission: Boolean = true,
            includeHistoryPermission: Boolean = true,
            includeStepsCadence: Boolean = true
        ): Set<String> {
            val permissions = types.map { HealthPermission.getReadPermission(it.recordClass) }.toMutableSet()
            if (includeStepsCadence && HealthDataType.STEPS in types) {
                permissions.add(HealthPermission.getReadPermission(StepsCadenceRecord::class))
            }
            if (includeBackgroundPermission) {
                permissions.add(BACKGROUND_PERMISSION_STR)
            }
            if (includeHistoryPermission) {
                permissions.add("android.permission.health.READ_HEALTH_DATA_HISTORY")
            }
            return permissions
        }

        const val BACKGROUND_PERMISSION_STR = "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND"

        val INITIAL_PERMISSIONS: Set<String>
            get() = ALL_PERMISSIONS + BACKGROUND_PERMISSION_STR

        val ALL_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(StepsCadenceRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(HeightRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(BloodGlucoseRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getReadPermission(BodyTemperatureRecord::class),
            HealthPermission.getReadPermission(SkinTemperatureRecord::class),
            HealthPermission.getReadPermission(RespiratoryRateRecord::class),
            HealthPermission.getReadPermission(RestingHeartRateRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getReadPermission(BasalMetabolicRateRecord::class),
            HealthPermission.getReadPermission(BodyFatRecord::class),
            HealthPermission.getReadPermission(LeanBodyMassRecord::class),
            HealthPermission.getReadPermission(Vo2MaxRecord::class),
            HealthPermission.getReadPermission(BoneMassRecord::class),
            "android.permission.health.READ_HEALTH_DATA_HISTORY"
        )
    }
}
