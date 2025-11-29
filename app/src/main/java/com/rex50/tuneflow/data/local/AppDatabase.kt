package com.rex50.tuneflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rex50.tuneflow.data.local.dao.ProfileDao
import com.rex50.tuneflow.data.local.entity.ProfileEntity
import com.rex50.tuneflow.domain.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for TuneFlow app
 */
@Database(
    entities = [ProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tuneflow_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default profiles on first creation
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDefaultProfiles(database.profileDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultProfiles(profileDao: ProfileDao) {
            val cityProfile = Profile.createCityProfile()
            val highwayProfile = Profile.createHighwayProfile()
            val runningProfile = Profile.createRunningProfile()
            val cyclingProfile = Profile.createCyclingProfile()

            // Insert city profile as selected by default
            profileDao.insertProfile(
                ProfileEntity.fromDomain(cityProfile, isSelected = true)
            )

            // Insert highway profile
            profileDao.insertProfile(
                ProfileEntity.fromDomain(highwayProfile, isSelected = false)
            )

            profileDao.insertProfile(
                ProfileEntity.fromDomain(cyclingProfile, isSelected = false)
            )

            profileDao.insertProfile(
                ProfileEntity.fromDomain(runningProfile, isSelected = false)
            )
        }

        /**
         * Re-seed default profiles if database is empty
         */
        suspend fun ensureDefaultProfiles(profileDao: ProfileDao) {
            val count = profileDao.getProfileCount()
            if (count == 0) {
                seedDefaultProfiles(profileDao)
            }
        }
    }
}

