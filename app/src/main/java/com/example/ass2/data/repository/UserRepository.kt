import com.example.ass2.data.local.User
import com.example.ass2.data.local.UserDao
import com.example.ass2.viewmodel.UserViewModel
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun signup(email: String, password: String): Boolean {
        val existing = userDao.findUserByEmail(email)
        if (existing != null) return false

        userDao.insertUser(User(email = email, password = password))
        return true
    }
    suspend fun login(email: String, password: String): UserViewModel.LoginResult {
        val user = userDao.findUserByEmail(email)
        if (user == null) {
            return UserViewModel.LoginResult.UserNotFound
        }
        if (user.password != password) {
            return UserViewModel.LoginResult.WrongPassword
        }
        return UserViewModel.LoginResult.Success
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}