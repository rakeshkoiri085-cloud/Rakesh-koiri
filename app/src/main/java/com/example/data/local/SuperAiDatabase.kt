package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        ProjectEntity::class,
        ProjectTaskEntity::class,
        UsageEntity::class,
        LessonProgressEntity::class,
        SubscriptionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SuperAiDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun projectDao(): ProjectDao
    abstract fun projectTaskDao(): ProjectTaskDao
    abstract fun usageDao(): UsageDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: SuperAiDatabase? = null

        fun getDatabase(context: Context): SuperAiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SuperAiDatabase::class.java,
                    "super_ai_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
