# ─── Namma-Shaale Inventory ProGuard Rules ────────────────────────────────────

# Keep Room entities and DAOs
-keep class com.nammashale.inventory.data.local.entity.** { *; }
-keep interface com.nammashale.inventory.data.local.dao.** { *; }

# Keep Gson serialized DTO classes
-keep class com.nammashale.inventory.data.remote.dto.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Gson TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Retrofit
-keepattributes RuntimeVisibleAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Compose
-keep class androidx.compose.** { *; }
