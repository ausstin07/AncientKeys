package com.ancientkeys.keyboard.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors

/**
 * AncientKeys v1.1 Setup & Onboarding Guide.
 *
 * Implements a clear 5-step guided walkthrough:
 * 1. Welcome & 100% Offline Privacy Guarantee
 * 2. Enable AncientKeys in Android Keyboard Settings
 * 3. Select AncientKeys as Active Keyboard
 * 4. Choose a Historical Writing System
 * 5. Start Typing & Seamless English Toggling
 */
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
        containerColor = MaterialTheme.ancientKeysColors.cardBackground,
        shape = RoundedCornerShape(18.dp),
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (step) {
                            1 -> "Welcome to AncientKeys"
                            2 -> "1. Enable Keyboard"
                            3 -> "2. Select Active Keyboard"
                            4 -> "3. Choose a Script"
                            else -> "4. Fast English Switching"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )

                    // Step Indicator Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.ancientKeysColors.chipSelectedBackground
                    ) {
                        Text(
                            text = "$step of 5",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.chipSelectedText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (step) {
                    1 -> {
                        Text(
                            text = "AncientKeys brings six authentic historical writing systems to your Android device with total offline privacy.",
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            lineHeight = 18.sp
                        )

                        AncientKeysCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                            borderColor = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.7f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🔒 100% Offline & Private", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• Zero network permissions (INTERNET = false)", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                                Text("• Zero keystroke logging or cloud storage", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                                Text("• Automatic shield in PIN and password fields", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                            }
                        }
                    }
                    2 -> {
                        Text(
                            text = "To type ancient scripts in WhatsApp, Telegram, browsers, and notes, enable AncientKeys in your system settings:",
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                            )
                        ) {
                            Text("⚙️ Open Android Keyboard Settings", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            text = "Turn ON the switch next to 'AncientKeys'. Android will show a standard disclaimer reminding you that all input methods can read typed text; AncientKeys has zero internet access so your text never leaves your device.",
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.chipText,
                            lineHeight = 16.sp
                        )
                    }
                    3 -> {
                        Text(
                            text = "Next, switch to AncientKeys as your current active input method:",
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                            )
                        ) {
                            Text("⌨️ Choose Keyboard (IME Picker)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            text = "Select 'AncientKeys' in the list. You can switch back to your device keyboard anytime using the 🌐 key.",
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.chipText,
                            lineHeight = 16.sp
                        )
                    }
                    4 -> {
                        Text(
                            text = "Tap the top-left script button [🏛️ Script ▾] on the keyboard to select from six authentic writing systems:",
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            lineHeight = 18.sp
                        )

                        AncientKeysCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                            borderColor = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.7f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text("• Ancient Greek (Ἑλληνικά)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• Egyptian Hieroglyphs (Classical Middle Kingdom)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• Ashokan Brahmi (3rd Century BCE)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• Kharosthi (Gandhara, RTL)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• Phoenician (Levant, RTL)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• Elder Futhark (Early Germanic Runes)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Seamlessly switch between ancient scripts and English at any time:",
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            lineHeight = 18.sp
                        )

                        AncientKeysCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                            borderColor = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.7f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("• [ABC] Pill: Quick English QWERTY keyboard", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• [🏛️] Pill: Returns directly to your active ancient script", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• [?123] Pill: Modern digits and symbols", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.keyText)
                                Text("• [🌐] Globe Key: Switches back to Gboard or Samsung Keyboard", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.keyText)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (step < 5) {
                Button(
                    onClick = { step += 1 },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                ) {
                    Text("Next ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        userPrefs.setOnboardingCompleted(true)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                ) {
                    Text("Start Typing!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step -= 1 },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                ) {
                    Text("Back", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.keyText)
                }
            } else {
                TextButton(
                    onClick = {
                        userPrefs.setOnboardingCompleted(true)
                        onDismiss()
                    }
                ) {
                    Text("Skip", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                }
            }
        }
    )
}
