# KeyNova ProGuard & R8 optimization rules

# Keep Room entities and DAOs
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Keep DTO models for Gson serialization
-keep class com.harshguruji.keynova.data.model.** { *; }

# Jetpack Compose rules are bundled with Compose libraries
