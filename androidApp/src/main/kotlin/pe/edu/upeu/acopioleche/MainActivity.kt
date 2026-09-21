package pe.edu.upeu.acopioleche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pe.edu.upeu.acopioleche.ui.AcopioLecheApp
import pe.edu.upeu.acopioleche.ui.theme.AcopioLecheTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcopioLecheTheme {
                AcopioLecheApp()
            }
        }
    }
}
