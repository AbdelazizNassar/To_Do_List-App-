package com.example.todolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class RoomDbHelper : RoomDatabase() {
    abstract val dao: TaskDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDbHelper? = null

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Task ADD COLUMN timeInMillis INTEGER DEFAULT 0 NOT NULL")
                database.execSQL("ALTER TABLE Task ADD COLUMN isReminded INTEGER DEFAULT 0 NOT NULL")
            }
        }

        fun getInstance(context: Context): RoomDbHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RoomDbHelper::class.java,
                    "Tasks_db"
                )
                    .addMigrations(MIGRATION_1_2) // Add this line for migration
                    .allowMainThreadQueries() // Be cautious with this in production
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
