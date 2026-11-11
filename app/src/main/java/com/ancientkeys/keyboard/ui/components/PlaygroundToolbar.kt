package com.ancientkeys.keyboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlaygroundToolbar(
    onSpaceClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Space button
        Button(
            onClick = onSpaceClick,
            modifier = Modifier
                .weight(2.5f)
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Space", fontSize = 14.sp)
        }

        // Backspace button
        Button(
            onClick = onBackspaceClick,
            modifier = Modifier
                .weight(1.8f)
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("⌫ Del", fontSize = 14.sp)
        }

        // Copy button
        OutlinedButton(
            onClick = onCopyClick,
            modifier = Modifier
                .weight(1.5f)
                .height(44.dp)
        ) {
            Text("Copy", fontSize = 13.sp)
        }

        // Clear button
        OutlinedButton(
            onClick = onClearClick,
            modifier = Modifier
                .weight(1.5f)
                .height(44.dp)
        ) {
            Text("Clear", fontSize = 13.sp)
        }
    }
}

