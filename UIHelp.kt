package com.example.mymentorbuddy

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun issmallsize(): Boolean{
    val d = LocalConfiguration.current
    return d.screenHeightDp <= 786
}