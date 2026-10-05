package com.pratikbharad.shoplite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pratikbharad.shoplite.ui.ShopLiteApp
import com.pratikbharad.shoplite.ui.theme.ShopLiteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ShopLiteTheme {
                ShopLiteApp()
            }
        }
    }
}
