package com.quick.liveswitcher.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.db.DbConfig.DB_VERSION
import com.quick.liveswitcher.db.DbConfig.DATABASE_NAME
import com.quick.liveswitcher.db.converters.DateConverters
import com.quick.liveswitcher.db.dao.PreViewLayerDao
import com.quick.liveswitcher.db.dao.SceneDao
import com.quick.liveswitcher.db.entity.LayerEntity
import com.quick.liveswitcher.db.entity.PreViewLayerEntity

@Database(entities = arrayOf(
        PreViewLayerEntity::class, SceneBean::class),
        version = DB_VERSION,
        autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun preViewLayerDao(): PreViewLayerDao
    abstract fun sceneDao(): SceneDao

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
//                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
//                            WorkManager.getInstance(context).enqueue(request)
                        }
                    })
                .fallbackToDestructiveMigrationOnDowngrade()//数据库降版本时删除数据重新创建
                .build()
        }
    }
}