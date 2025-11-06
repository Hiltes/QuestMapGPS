package com.example.questmapgps.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import com.example.questmapgps.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun AppLogo(width: Int, height: Int, padding: Int, modifier: Modifier = Modifier,) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo aplikacji",
        modifier = modifier.width(width.dp).height(height.dp).padding(padding.dp),


    )
}

@Preview
@Composable
fun LogoPreview() {
    AppLogo(width = 100, height = 200, padding = 10)
}