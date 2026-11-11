# ==============================================================================
# AncientKeys ProGuard / R8 Configuration Rules
# ==============================================================================

# --- Android Components & Services ---
-keep public class com.ancientkeys.keyboard.MainActivity extends androidx.activity.ComponentActivity {
    public <init>();
}

-keep public class com.ancientkeys.keyboard.ime.AncientKeysIME extends android.inputmethodservice.InputMethodService {
    public <init>();
}

# --- Epigraphic Models & Serialization ---
# Keep data model properties so JSON serialization and reflection maintain exact schema names
-keep class com.ancientkeys.keyboard.model.** {
    <fields>;
    <methods>;
}

-keep class com.ancientkeys.keyboard.learn.** {
    <fields>;
    <methods>;
}

-keep class com.ancientkeys.keyboard.storage.** {
    <fields>;
    <methods>;
}

# --- Jetpack Compose & Material 3 Optimization Safety ---
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature

-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
    @androidx.compose.runtime.ReadOnlyComposable *;
}

-dontwarn androidx.compose.**
-dontwarn androidx.lifecycle.**
-dontwarn androidx.savedstate.**

# --- Bundled Assets & Fonts ---
# Keep font provider asset path references
-keep class com.ancientkeys.keyboard.util.HistoricalFontProvider {
    public *;
}
