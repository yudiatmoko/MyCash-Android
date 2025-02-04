package com.iyam.mycash.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.outlet.OutletActivity
import com.iyam.mycash.ui.signin.SignInActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private var isTokenChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !isTokenChecked }
        observeUserLoggedIn()
    }

    private fun observeUserLoggedIn() {
        mainViewModel.userTokenLiveData.observe(this) {
            if (it.isNotEmpty()) {
                navigateToOutlet()
            } else {
                navigateToLogin()
            }
            isTokenChecked = true
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToOutlet() {
        val intent = Intent(this, OutletActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
