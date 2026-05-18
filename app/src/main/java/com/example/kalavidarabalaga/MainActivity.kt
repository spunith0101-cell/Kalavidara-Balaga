package com.example.kalavidarabalaga

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kalavidarabalaga.ui.RegisterTroupeScreen
import com.example.kalavidarabalaga.ui.TroupeDetailScreen
import com.example.kalavidarabalaga.ui.TroupeListScreen
import com.example.kalavidarabalaga.ui.TroupeViewModel
import com.example.kalavidarabalaga.ui.theme.KalavidaraBalagaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalavidaraBalagaTheme {
                MainNavigation(
                    onCallClick = { phoneNumber ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MainNavigation(onCallClick: (String) -> Unit) {
    val navController = rememberNavController()
    val viewModel: TroupeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TroupeListScreen(
                viewModel = viewModel,
                onTroupeClick = { troupeId ->
                    navController.navigate("detail/$troupeId")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterTroupeScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable(
            route = "detail/{troupeId}",
            arguments = listOf(navArgument("troupeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val troupeId = backStackEntry.arguments?.getString("troupeId")
            TroupeDetailScreen(
                troupeId = troupeId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCallClick = onCallClick
            )
        }
    }
}
