# Add project specific ProGuard rules here.
# These rules are used when building a release APK.
# Keep Retrofit models from being obfuscated (Gson needs field names to match JSON)
-keep class com.salon.app.models.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
