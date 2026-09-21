package pe.edu.upeu.acopioleche.ui.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.service.CicloSemanal
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeBorde
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

private val DIAS = listOf("J", "V", "S", "D", "L", "M", "M")

/**
 * [valores] está ordenado según el ciclo de acopio jueves→miércoles (índice 0 = jueves de inicio
 * del ciclo, ver [CicloSemanal]), igual que `EntregaRepository.observarVolumenUltimaSemana()`. El
 * índice de "hoy" se calcula con `CicloSemanal.indiceEnCiclo()` para que el resaltado coincida
 * con el día real dentro del ciclo, no un índice fijo.
 */
@Composable
fun VolumenSemanalChart(valores: List<Double>) {
    val maximo = (valores.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    val indiceHoy = CicloSemanal.indiceEnCiclo(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    Row(
        modifier = Modifier.fillMaxWidth().height(130.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        valores.forEachIndexed { index, valor ->
            val alturaFraccion = (valor / maximo).toFloat().coerceIn(0f, 1f)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(text = valor.toInt().toString(), style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((90 * alturaFraccion).dp.coerceAtLeast(2.dp))
                        .background(
                            color = if (index == indiceHoy) VerdeOscuro else VerdeBorde,
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                        ),
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DIAS.getOrElse(index) { "" },
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario,
                )
            }
        }
    }
}
