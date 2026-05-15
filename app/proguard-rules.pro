# FEATURE: Project Scaffolding
-keepattributes *Annotation*
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
