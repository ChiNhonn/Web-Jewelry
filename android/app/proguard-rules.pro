# AURA Fine Jewelry Proguard & R8 Rules

# Kotlinx Serialization Rules
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit & OkHttp
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepattributes Signature
-keepattributes Exceptions

# Room SQLite
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Data Models (Keep domain & DTO fields from being obfuscated/stripped)
-keep class com.aura.jewelry.data.remote.dto.** { *; }
-keep class com.aura.jewelry.domain.model.** { *; }
