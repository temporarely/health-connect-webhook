# Add project specific ProGuard rules here.

# Samsung Health SDK IPC classes — class names are looked up at runtime via
# Class.forName() from Bundle.getParcelable(), so they must not be renamed.
-keep class com.samsung.android.sdk.healthdata.HealthDataResolver { *; }
-keep class com.samsung.android.sdk.healthdata.HealthDataResolver$ReadResult { *; }
-keep class com.samsung.android.sdk.internal.healthdata.HealthResultReceiver { *; }
-keep class com.samsung.android.sdk.internal.healthdata.ReadRequestImpl { *; }

# Keep the CREATOR fields required by Android's Parcelable reflection.
-keepclassmembers class com.samsung.android.sdk.** {
    public static final android.os.Parcelable$Creator CREATOR;
}
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
