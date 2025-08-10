import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.Room
import android.content.Context

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}

