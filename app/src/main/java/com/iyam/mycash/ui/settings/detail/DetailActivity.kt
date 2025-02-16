package com.iyam.mycash.ui.settings.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityDetailBinding
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.model.User
import com.iyam.mycash.ui.settings.outlet.OutletDataUpdateActivity
import com.iyam.mycash.ui.settings.user.UserUpdateActivity

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(
            layoutInflater,
            window.decorView.findViewById(android.R.id.content),
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.detail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeDataFromSettings()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.tvUpdateOutlet.setOnClickListener {
            OutletDataUpdateActivity.startActivity(this, intent.getParcelableExtra(EXTRA_OUTLET))
            finish()
        }
        binding.tvUpdateUser.setOnClickListener {
            UserUpdateActivity.startActivity(this, intent.getParcelableExtra(EXTRA_USER))
            finish()
        }
    }

    private fun observeDataFromSettings() {
        val user = intent.getParcelableExtra<User>(EXTRA_USER)
        val outlet = intent.getParcelableExtra<Outlet>(EXTRA_OUTLET)

        binding.tvUserName.text = user?.name
        binding.tvUserEmail.text = user?.email
        if (user?.phoneNumber.isNullOrBlank()) {
            binding.tvUserPhoneNumber.text = getString(R.string.unknown)
        } else {
            binding.tvUserPhoneNumber.text = user?.phoneNumber
        }

        binding.tvOutletName.text = outlet?.name
        binding.tvOutletType.text = outlet?.type
        binding.tvOutletPhoneNumber.text = outlet?.phoneNumber
        binding.tvOutletAddress.text = outlet?.address
        binding.tvOutletDistrict.text = outlet?.district
        binding.tvOutletCity.text = outlet?.city
        binding.tvOutletProvince.text = outlet?.province

        if (user?.image.isNullOrBlank()) {
            binding.ivProfilePhoto.load(R.drawable.img_placeholder_profile)
        } else {
            binding.ivProfilePhoto.load(user?.image)
        }

        if (outlet?.image.isNullOrBlank()) {
            binding.ivOutletLogo.load(R.drawable.img_placeholder_general)
        } else {
            binding.ivOutletLogo.load(outlet?.image)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val EXTRA_OUTLET = "EXTRA_OUTLET"
        private const val EXTRA_USER = "EXTRA_USER"
        fun startActivity(context: Context, user: User?, outlet: Outlet?) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            intent.putExtra(EXTRA_OUTLET, outlet)
            context.startActivity(intent)
        }
    }
}
