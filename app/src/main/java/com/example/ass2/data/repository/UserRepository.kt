import com.example.ass2.data.local.User
import com.example.ass2.data.local.UserDao
import com.example.ass2.viewmodel.UserViewModel
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun getOrCreateGoogleUser(email: String): User {
        userDao.findUserByEmail(email)?.let { return it }
        userDao.insertUser(
            User(
                email = email,
                password = GOOGLE_AUTH_PASSWORD
            )
        )
        return requireNotNull(userDao.findUserByEmail(email))
    }

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
        if (user.password == GOOGLE_AUTH_PASSWORD) {
            return UserViewModel.LoginResult.UseGoogleSignIn
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
        if (user.password == GOOGLE_AUTH_PASSWORD) return false
        userDao.updateUser(user.copy(password = newPassword))
        return true
    }

    companion object {
        const val GOOGLE_AUTH_PASSWORD = "__google_auth__"
    }
}