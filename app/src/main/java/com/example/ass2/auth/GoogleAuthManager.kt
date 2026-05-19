package com.example.ass2.auth

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ass2.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthManager(activity: ComponentActivity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    private var pendingCallback: ((GoogleSignInResult) -> Unit)? = null

    private val signInLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val callback = pendingCallback
        pendingCallback = null

        if (result.resultCode != Activity.RESULT_OK) {
            callback?.invoke(GoogleSignInResult.Cancelled)
            return@registerForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken == null) {
                callback?.invoke(GoogleSignInResult.Failure("Missing Google ID token"))
                return@registerForActivityResult
            }
            firebaseAuthWithGoogle(idToken, callback)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            callback?.invoke(GoogleSignInResult.Failure(e.localizedMessage ?: "Google sign in failed"))
        }
    }

    init {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, options)
    }

    fun signIn(onResult: (GoogleSignInResult) -> Unit) {
        pendingCallback = onResult
        signInLauncher.launch(googleSignInClient.signInIntent)
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        callback: ((GoogleSignInResult) -> Unit)?
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    callback?.invoke(
                        GoogleSignInResult.Failure(
                            task.exception?.localizedMessage ?: "Firebase authentication failed"
                        )
                    )
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                val email = user?.email
                if (email.isNullOrBlank()) {
                    callback?.invoke(GoogleSignInResult.Failure("Google account has no email"))
                    return@addOnCompleteListener
                }

                callback?.invoke(
                    GoogleSignInResult.Success(
                        email = email,
                        displayName = user.displayName
                    )
                )
            }
    }

    sealed class GoogleSignInResult {
        data class Success(val email: String, val displayName: String?) : GoogleSignInResult()
        data class Failure(val message: String) : GoogleSignInResult()
        data object Cancelled : GoogleSignInResult()
    }

    companion object {
        private const val TAG = "GoogleAuthManager"
    }
}
