package pe.edu.upeu.acopioleche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AcopioLecheApp()
        }
    }
}

@Composable
fun AcopioLecheApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize().safeContentPadding()) {
            Text(text = "Acopioleche")
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AcopioLecheApp()
}
