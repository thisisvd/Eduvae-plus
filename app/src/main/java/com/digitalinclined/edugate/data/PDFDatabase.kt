package com.digitalinclined.edugate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.digitalinclined.edugate.constants.Constants.PDF_DATABASE_NAME
import com.digitalinclined.edugate.data.model.PDFDataRoom

@Database(entities = [PDFDataRoom::class], version = 1, exportSchema = false)
abstract class PDFDatabase : RoomDatabase() {

    abstract fun pdfDataDao(): PDFDataDao

    // same as public static final class in java
    companion object {

        // writes to this field are immediately made visible to other threads
        @Volatile
        private var INSTANCE: PDFDatabase? = null

        fun getDatabase(context: Context): PDFDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            // synchronized -> When a thread calls synchronized, it acquires the lock of that synchronized block.
            // Other threads don't have permission to call that same synchronized block as long as previous thread
            // which had acquired the lock does not release the lock.
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PDFDatabase::class.java,
                    PDF_DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}