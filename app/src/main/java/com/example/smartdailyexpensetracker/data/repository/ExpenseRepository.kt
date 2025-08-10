import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    suspend fun addExpense(e: Expense) = dao.insert(e)
    fun getAll(): Flow<List<Expense>> = dao.getAll()
    fun getForDay(day: Long): Flow<List<Expense>> = dao.getForDay(day)
    suspend fun countSimilar(title: String, amount: Double, since: Long) =
        dao.countSimilar(title, amount, since)
}