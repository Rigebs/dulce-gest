package com.rige.dulcegest.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.commit
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var homeNavHost: NavHostFragment
    private lateinit var productsNavHost: NavHostFragment
    private lateinit var financesNavHost: NavHostFragment
    private lateinit var moreNavHost: NavHostFragment

    private var activeNavHost: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            val currentNavController = activeNavHost?.navController
            if (currentNavController?.popBackStack() == false) {
                finish()
            }
        }

        if (savedInstanceState == null) {

            homeNavHost = createNavHost(R.navigation.nav_home)
            productsNavHost = createNavHost(R.navigation.nav_products)
            financesNavHost = createNavHost(R.navigation.nav_finances)
            moreNavHost = createNavHost(R.navigation.nav_more)

            supportFragmentManager.commit {
                add(R.id.nav_host_container, moreNavHost, "more").hide(moreNavHost)
                add(R.id.nav_host_container, financesNavHost, "finances").hide(financesNavHost)
                add(R.id.nav_host_container, productsNavHost, "products").hide(productsNavHost)
                add(R.id.nav_host_container, homeNavHost, "home")
            }
            activeNavHost = homeNavHost
        } else {
            homeNavHost =
                supportFragmentManager.findFragmentByTag("home") as NavHostFragment
            productsNavHost =
                supportFragmentManager.findFragmentByTag("products") as NavHostFragment
            financesNavHost =
                supportFragmentManager.findFragmentByTag("finances") as NavHostFragment
            moreNavHost =
                supportFragmentManager.findFragmentByTag("more") as NavHostFragment

            activeNavHost = listOf(homeNavHost, productsNavHost, financesNavHost, moreNavHost)
                .firstOrNull { !it.isHidden }
        }

        setupBottomNav()
    }

    fun navigateToInFinancesGraph(destinationId: Int, args: Bundle? = null) {
        switchNavHost(financesNavHost)
        binding.bottomNav.selectedItemId = R.id.financeMenuFragment

        val navController = financesNavHost.navController
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        try {
            navController.navigate(destinationId, args, navOptions)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Destino no encontrado en nav_finances.xml", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun navigateToInProductsGraph(destinationId: Int, args: Bundle? = null) {
        switchNavHost(productsNavHost)
        binding.bottomNav.selectedItemId = R.id.productMenuFragment

        val navController = productsNavHost.navController
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        try {
            navController.navigate(destinationId, args, navOptions)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Destino no encontrado en nav_products.xml", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun createNavHost(navGraphId: Int): NavHostFragment {
        return NavHostFragment.create(navGraphId)
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> switchNavHost(homeNavHost)
                R.id.productMenuFragment -> switchNavHost(productsNavHost)
                R.id.financeMenuFragment -> switchNavHost(financesNavHost)
                R.id.reportListFragment -> switchNavHost(moreNavHost)
            }
            true
        }
    }

    private fun switchNavHost(target: NavHostFragment) {
        if (activeNavHost == target) return

        supportFragmentManager.commit {
            activeNavHost?.let { hide(it) }
            show(target)
        }
        activeNavHost = target
    }
}