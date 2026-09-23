package com.harshguruji.keynova.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.harshguruji.keynova.data.database.dao.MappingDao
import com.harshguruji.keynova.data.database.dao.ProfileDao
import com.harshguruji.keynova.data.database.dao.VirtualControlDao
import com.harshguruji.keynova.data.database.entity.MappingEntity
import com.harshguruji.keynova.data.database.entity.ProfileEntity
import com.harshguruji.keynova.data.database.entity.VirtualControlEntity
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.ControlType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType

class Converters {
    @TypeConverter fun fromDeviceType(v: DeviceType): String = v.name
    @TypeConverter fun toDeviceType(v: String): DeviceType = runCatching { DeviceType.valueOf(v) }.getOrDefault(DeviceType.KEYBOARD)

    @TypeConverter fun fromInputType(v: InputType): String = v.name
    @TypeConverter fun toInputType(v: String): InputType = runCatching { InputType.valueOf(v) }.getOrDefault(InputType.KEYBOARD_KEY)

    @TypeConverter fun fromActionType(v: ActionType): String = v.name
    @TypeConverter fun toActionType(v: String): ActionType = runCatching { ActionType.valueOf(v) }.getOrDefault(ActionType.TAP_ACTION)

    @TypeConverter fun fromActionMode(v: ActionMode): String = v.name
    @TypeConverter fun toActionMode(v: String): ActionMode = runCatching { ActionMode.valueOf(v) }.getOrDefault(ActionMode.TAP)

    @TypeConverter fun fromControlType(v: ControlType): String = v.name
    @TypeConverter fun toControlType(v: String): ControlType = runCatching { ControlType.valueOf(v) }.getOrDefault(ControlType.BUTTON)
}

@Database(
    entities = [ProfileEntity::class, MappingEntity::class, VirtualControlEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KeyNovaDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun mappingDao(): MappingDao
    abstract fun virtualControlDao(): VirtualControlDao

    companion object {
        @Volatile
        private var INSTANCE: KeyNovaDatabase? = null

        fun getInstance(context: Context): KeyNovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeyNovaDatabase::class.java,
                    "keynova_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
