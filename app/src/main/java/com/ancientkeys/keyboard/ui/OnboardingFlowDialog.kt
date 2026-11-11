package com.ancientkeys.keyboard.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.ime.phonetic.BrahmiEasyPhoneticEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.util.HistoricalFontProvider

@Composable
fun OnboardingFlowDialog(
    onDismiss: () -> Unit,
    userPrefs: UserPreferencesRepository
) {
    var step by remember { mutableIntStateOf(1) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            userPrefs.setOnboardingCompleted(true)
            onDismiss()
        },
        confirmButton = {
            if (step < 4) {
                Button(
                    onClick = { step += 1 },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Next ➔")
                }
            } else {
                Button(
                    onClick = {
                        userPrefs.setOnboardingCompleted(true)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Get Started!")
                }
            }
        },
        dismissButton = {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step -= 1 },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Back")
                }
            } else {
                TextButton(
                    onClick = {
                        userPrefs.setOnboardingCompleted(true)
                        onDismiss()
                    }
                ) {
                    Text("Skip")
                }
            }
        },
        title = {
            Text(
                text = when (step) {
                    1 -> "Welcome to AncientKeys"
                    2 -> "1. Enable the System Keyboard"
                    3 -> "2. Modes & Script Switching"
                    else -> "3. Try Easy Phonetic Typing!"
                },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (step) {
                    1 -> {
                        Text(
                            text = "AncientKeys brings six authentic historical writing systems to your Android device with 100% offline privacy.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🔒 100% Offline & Private", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("• Zero network permissions", fontSize = 11.sp)
                                Text("• Zero keystroke logging or word storage", fontSize = 11.sp)
                                Text("• Automatic shield in password & PIN fields", fontSize = 11.sp)
                            }
                        }
                    }
                    2 -> {
                        Text(
                            text = "To type ancient scripts anywhere on your phone (WhatsApp, Docs, Notes), enable AncientKeys in Android Settings:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                } catch (_: Exception) {
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("⚙️ Open Keyboard Settings")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Turn ON 'AncientKeys', then select it using the keyboard icon at the bottom of your screen when typing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    3 -> {
                        Text(
                            text = "The keyboard utility bar gives you instant, 1-tap control:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("[Brahmi ▾] [Easy] [ABC] [Native] [?123] [🌐]", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• [Easy]: Type natural English (e.g. 'ka' ➔ 𑀓)", fontSize = 11.sp)
                                Text("• [ABC]: Regular English QWERTY keyboard", fontSize = 11.sp)
                                Text("• [Native]: Direct ancient character grid", fontSize = 11.sp)
                                Text("• [?123]: Modern numbers & symbols", fontSize = 11.sp)
                                Text("• [🌐]: Instantly switch back to Gboard", fontSize = 11.sp)
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Try typing ordinary English letters below to see live Brahmi conversion:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        var testInput by remember { mutableStateOf("asoka") }
                        val converted = remember(testInput) { BrahmiEasyPhoneticEngine.convert(testInput) }
                        val brahmiFont = remember { HistoricalFontProvider.getFontFamily(context, "brahmi") }

                        OutlinedTextField(
                            value = testInput,
                            onValueChange = { testInput = it },
                            label = { Text("Type English letters (e.g., 'namaste', 'ka', 'asoka')") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Live Historical Output:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = converted.ifEmpty { "𑀅𑀰𑁄𑀓" },
                                    fontFamily = brahmiFont,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

