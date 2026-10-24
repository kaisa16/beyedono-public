# file: app/proguard-rules.pro
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions
-dontwarn kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keepclassmembers class ru.beyedono.app.data.remote.dto.** { *; }
-keepattributes RuntimeVisibleAnnotations

# Retrofit
-keepattributes Signature, Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * { @retrofit2.http.* <methods>; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper

# Room
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**

# Yandex MapKit
-keep class com.yandex.mapkit.** { *; }
-keep class com.yandex.runtime.** { *; }
-dontwarn com.yandex.**

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Kotlin
-keep class kotlin.Metadata { *; }