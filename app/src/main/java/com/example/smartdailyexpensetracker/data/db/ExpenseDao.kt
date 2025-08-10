import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date(timestamp/1000, 'unixepoch') = date(:day/1000, 'unixepoch') ORDER BY timestamp DESC")
    fun getForDay(day: Long): Flow<List<Expense>>

    @Query("SELECT COUNT(*) FROM expenses WHERE title = :title AND amount = :amount AND timestamp > :since")
    suspend fun countSimilar(title: String, amount: Double, since: Long): Int
}
