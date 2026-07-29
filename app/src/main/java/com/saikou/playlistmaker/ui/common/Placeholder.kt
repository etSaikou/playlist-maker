package com.saikou.playlistmaker.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.saikou.playlistmaker.ui.theme.YsDisplay
import com.saikou.playlistmaker.util.ThemeState


@Composable
fun Placeholder(
    imageRes: Int,
    imageResAlt: Int,
    text: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    val isDark by ThemeState.isDark.collectAsState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(102.dp))

        Image(
            painter = painterResource(id = if (isDark) imageResAlt else imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 19.sp,
                textAlign = TextAlign.Center,
                fontFamily = YsDisplay
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )
        if (buttonText != null && onButtonClick != null) {
            Button(
                onClick = onButtonClick,
                modifier = Modifier.padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(text = buttonText, fontFamily = YsDisplay)
            }
        }
    }
}
