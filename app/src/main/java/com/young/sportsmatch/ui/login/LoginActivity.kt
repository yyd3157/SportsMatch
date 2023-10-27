package com.young.sportsmatch.ui.login

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.young.sportsmatch.BuildConfig
import com.young.sportsmatch.ui.setting.SettingActivity
import com.young.sportsmatch.R
import com.young.sportsmatch.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        autoLogin()

        binding.btnLogin.setOnClickListener {
            showOneTapUI()
        }

        oneTapClient = Identity.getSignInClient(this)
    }
    private val oneTapSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        handleOneTapSignInResult(result)
    }

    private val legacySignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleLegacySignInResult(result)
    }

    private fun createdSignClient() {
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    private fun handleOneTapSignInResult(result: ActivityResult) {
        try {
            val googleCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = googleCredential.googleIdToken
            when {
                idToken != null -> {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                moveToHome()
                            } else {
                                Log.w("sign", "signInWithCredential:failure", task.exception)
                            }
                        }
                }
                else -> {
                    Log.d("sign", "No ID token!")
                }
            }
        } catch (e: ApiException) {
            displayLegacySignIn()
        }
    }

    private fun handleLegacySignInResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { signInTask ->
                            if (signInTask.isSuccessful) {
                                moveToHome()
                            } else {
                                Log.w("sign", "signInWithCredential:failure", signInTask.exception)
                            }
                        }
                } else {
                    Log.d("sign", "No ID token!")
                }
            } catch (e: ApiException) {
                Log.w("sign", "Google sign in failed", e)
            }
        }
    }

    private fun showOneTapUI() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    oneTapSignInLauncher.launch(
                        IntentSenderRequest
                            .Builder(result.pendingIntent.intentSender)
                            .build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("SignInActivity", "Couldn't start One Tap UI: " + e.localizedMessage)
                }
            }
            .addOnFailureListener { e ->
                Log.e("SignInActivity", "One Tap sign-in failed: ${e.message}", e)
                if (e is ApiException && e.statusCode == CommonStatusCodes.CANCELED) {
                    displayLegacySignIn()
                }
            }
    }

    private fun displayLegacySignIn() {
        val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val signInIntent = googleSignInClient.signInIntent
        legacySignInLauncher.launch(signInIntent)
    }

    private fun moveToHome() {
        showToast(getString(R.string.login_succeed))
        startActivity(Intent(this, SettingActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun autoLogin() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            moveToHome()
        } else {
            createdSignClient()

        }
    }
}