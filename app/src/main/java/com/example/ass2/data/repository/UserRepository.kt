import com.example.ass2.data.local.User
import com.example.ass2.data.local.UserDao
import com.example.ass2.viewmodel.UserViewModel
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun signup(
        email: String,
        password: String,
        weight: Float,
        height: Float,
        age: Int,
        gender: String
    ): Boolean {
        val existing = userDao.findUserByEmail(email)
        if (existing != null) return false

        userDao.insertUser(
            User(
                email = email,
                password = password,
                weight = weight,
                height = height,
                age = age,
                gender = gender
            )
        )
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
        return UserViewModel.LoginResult.Success(user)
    }

    suspend fun findUserByEmail(email: String): User? {
        return userDao.findUserByEmail(email)
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

    suspend fun resetPassword(email: String, newPassword: String): Boolean {
        val user = userDao.findUserByEmail(email) ?: return false
        userDao.updateUser(user.copy(password = newPassword))
        return true
    }
}