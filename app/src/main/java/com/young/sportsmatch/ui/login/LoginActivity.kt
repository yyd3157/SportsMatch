package com.young.sportsmatch.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.young.sportsmatch.BuildConfig
import com.young.sportsmatch.R
import com.young.sportsmatch.databinding.ActivityLoginBinding
import com.young.sportsmatch.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        checkUserLoginState()
    }

    private fun checkUserLoginState() {
        val user = auth.currentUser
        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            binding.btnLogin.setOnClickListener {
                setSignInRequest()
            }
        }
    }

    private fun setSignInRequest() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()
        startGoogleSignIn()
    }

    private fun startGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    getResultLauncher.launch(
                        IntentSenderRequest
                            .Builder(result.pendingIntent.intentSender)
                            .build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    showToast(getString(R.string.login_error))
                }
            }
            .addOnFailureListener(this) {
                startGoogleSignUp()
            }
    }

    private fun startGoogleSignUp() {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
            .build()

        Identity.getSignInClient(this)
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
                getResultLauncher.launch(
                    IntentSenderRequest
                        .Builder(result.intentSender)
                        .build()
                )
            }
            .addOnFailureListener { e ->
                showToast(getString(R.string.login_login_failed_error))
            }
    }

    private val getResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val idToken = oneTapClient.getSignInCredentialFromIntent(result.data).googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithCredential:success")
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
                            }
                        }
                    moveToHome()
                } else {
                    showToast(getString(R.string.login_token_error))
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        showToast(getString(R.string.login_cancel))
                    }

                    CommonStatusCodes.NETWORK_ERROR -> {
                        showToast(getString(R.string.login_network_error))
                    }

                    else -> {
                        showToast(getString(R.string.login_unknown_error))
                    }
                }
            }
        }
    }

    private fun moveToHome() {
        showToast(getString(R.string.login_succeed))
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}