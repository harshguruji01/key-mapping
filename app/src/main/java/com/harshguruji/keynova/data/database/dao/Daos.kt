package com.harshguruji.keynova.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.harshguruji.keynova.data.database.entity.MappingEntity
import com.harshguruji.keynova.data.database.entity.ProfileEntity
import com.harshguruji.keynova.data.database.entity.VirtualControlEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :profileId LIMIT 1")
    suspend fun getProfileById(profileId: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE targetPackage = :packageName LIMIT 1")
    suspend fun getProfileForPackage(packageName: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :profileId")
    suspend fun deleteProfileById(profileId: String)
}

@Dao
interface MappingDao {
    @Query("SELECT * FROM mappings WHERE profileId = :profileId")
    fun getMappingsForProfileFlow(profileId: String): Flow<List<MappingEntity>>

    @Query("SELECT * FROM mappings WHERE profileId = :profileId")
    suspend fun getMappingsForProfile(profileId: String): List<MappingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: MappingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMappings(mappings: List<MappingEntity>)

    @Update
    suspend fun updateMapping(mapping: MappingEntity)

    @Delete
    suspend fun deleteMapping(mapping: MappingEntity)

    @Query("DELETE FROM mappings WHERE id = :mappingId")
    suspend fun deleteMappingById(mappingId: String)

    @Query("DELETE FROM mappings WHERE profileId = :profileId")
    suspend fun deleteMappingsForProfile(profileId: String)
}

@Dao
interface VirtualControlDao {
    @Query("SELECT * FROM virtual_controls WHERE profileId = :profileId")
    fun getControlsForProfileFlow(profileId: String): Flow<List<VirtualControlEntity>>

    @Query("SELECT * FROM virtual_controls WHERE profileId = :profileId")
    suspend fun getControlsForProfile(profileId: String): List<VirtualControlEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControl(control: VirtualControlEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllControls(controls: List<VirtualControlEntity>)

    @Update
    suspend fun updateControl(control: VirtualControlEntity)

    @Delete
    suspend fun deleteControl(control: VirtualControlEntity)

    @Query("DELETE FROM virtual_controls WHERE id = :controlId")
    suspend fun deleteControlById(controlId: String)

    @Query("DELETE FROM virtual_controls WHERE profileId = :profileId")
    suspend fun deleteControlsForProfile(profileId: String)
}
