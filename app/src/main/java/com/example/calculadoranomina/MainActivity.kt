package com.example.calculadoranomina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoranomina.ui.theme.CalculadoraNominaTheme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CalculadoraNominaTheme {
                NominaApp()
            }
        }
    }
}

@Composable
fun NominaApp() {

    var salarioInput by remember { mutableStateOf("") }
    var horasDiurnasInput by remember { mutableStateOf("") }
    var horasNocturnasInput by remember { mutableStateOf("") }

    var esDominical by remember { mutableStateOf(false) }
    var transporteEmpresa by remember { mutableStateOf(false) }

    var resultado by remember { mutableStateOf<ResultadoNomina?>(null) }

    var errorSalario by remember { mutableStateOf(false) }
    var errorSalarioMinimo by remember { mutableStateOf(false) }
    var errorHoras by remember { mutableStateOf(false) }
    var errorHorasMaximas by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.titulo_nomina),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampoNumerico(
            label = stringResource(R.string.salario_basico),
            value = salarioInput,
            onValueChange = {
                salarioInput = it
                errorSalario = false
                errorSalarioMinimo = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = errorSalario || errorSalarioMinimo
        )

        if (errorSalario) {
            Text(stringResource(R.string.error_salario))
        }

        if (errorSalarioMinimo) {
            Text(stringResource(R.string.error_salario_minimo))
        }

        Spacer(modifier = Modifier.height(12.dp))

        CampoNumerico(
            label = stringResource(R.string.horas_diurnas),
            value = horasDiurnasInput,
            onValueChange = {
                horasDiurnasInput = it
                errorHoras = false
                errorHorasMaximas = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = errorHoras || errorHorasMaximas
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoNumerico(
            label = stringResource(R.string.horas_nocturnas),
            value = horasNocturnasInput,
            onValueChange = {
                horasNocturnasInput = it
                errorHoras = false
                errorHorasMaximas = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = errorHoras || errorHorasMaximas
        )

        if (errorHoras) {
            Text(stringResource(R.string.error_horas))
        }

        if (errorHorasMaximas) {
            Text(stringResource(R.string.error_horas_maximas))
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilaInterruptor(
            texto = stringResource(R.string.domingo_festivo),
            checked = esDominical,
            onCheckedChange = {
                esDominical = it
            }
        )

        FilaInterruptor(
            texto = stringResource(R.string.transporte_empresa),
            checked = transporteEmpresa,
            onCheckedChange = {
                transporteEmpresa = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {

                    val salario = salarioInput.toDoubleOrNull()

                    val horasDiurnas = horasDiurnasInput
                        .ifBlank { "0" }
                        .toDoubleOrNull()

                    val horasNocturnas = horasNocturnasInput
                        .ifBlank { "0" }
                        .toDoubleOrNull()

                    errorSalario = salario == null

                    errorSalarioMinimo =
                        salario != null && salario < SMMLV

                    errorHoras =
                        horasDiurnas == null ||
                                horasNocturnas == null ||
                                horasDiurnas < 0 ||
                                horasNocturnas < 0

                    val totalHoras =
                        (horasDiurnas ?: 0.0) +
                                (horasNocturnas ?: 0.0)

                    errorHorasMaximas = totalHoras > 48

                    if (
                        !errorSalario &&
                        !errorSalarioMinimo &&
                        !errorHoras &&
                        !errorHorasMaximas
                    ) {

                        resultado = calcularNomina(
                            salarioBasico = salario!!,
                            horasDiurnas = horasDiurnas ?: 0.0,
                            horasNocturnas = horasNocturnas ?: 0.0,
                            esDominical = esDominical,
                            transporteEmpresa = transporteEmpresa
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.calcular))
            }

            Button(
                onClick = {

                    salarioInput = ""
                    horasDiurnasInput = ""
                    horasNocturnasInput = ""

                    esDominical = false
                    transporteEmpresa = false

                    resultado = null

                    errorSalario = false
                    errorSalarioMinimo = false
                    errorHoras = false
                    errorHorasMaximas = false
                }
            ) {
                Text(stringResource(R.string.limpiar))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        resultado?.let { nomina ->

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.resultado_nomina),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                stringResource(
                    R.string.valor_hora,
                    formatearPesos(nomina.valorHora)
                )
            )

            Text(
                stringResource(
                    R.string.total_horas_extra,
                    formatearPesos(nomina.totalHorasExtra)
                )
            )

            Text(
                stringResource(
                    R.string.auxilio_transporte,
                    formatearPesos(nomina.auxilioTransporte)
                )
            )

            Text(
                stringResource(
                    R.string.total_devengado,
                    formatearPesos(nomina.totalDevengado)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(
                    R.string.aporte_salud,
                    formatearPesos(nomina.aporteSalud)
                )
            )

            Text(
                stringResource(
                    R.string.aporte_pension,
                    formatearPesos(nomina.aportePension)
                )
            )

            Text(
                stringResource(
                    R.string.fondo_solidaridad,
                    formatearPesos(nomina.fondoSolidaridad)
                )
            )

            Text(
                stringResource(
                    R.string.total_deducciones,
                    formatearPesos(nomina.totalDeducciones)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(
                    R.string.salario_neto,
                    formatearPesos(nomina.salarioNeto)
                ),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            val salario = salarioInput.toDoubleOrNull() ?: 0.0

            val rango = clasificarRango(salario)

            when (rango) {

                RangoSalarial.RANGO_1 -> {

                    Image(
                        painter = painterResource(
                            id = R.drawable.rango_1
                        ),
                        contentDescription = stringResource(
                            id = R.string.imagen_rango_1
                        )
                    )

                    Text(
                        text = stringResource(
                            R.string.rango_1_descripcion
                        )
                    )
                }

                RangoSalarial.RANGO_2 -> {

                    Image(
                        painter = painterResource(
                            id = R.drawable.rango_2
                        ),
                        contentDescription = stringResource(
                            id = R.string.imagen_rango_2
                        )
                    )

                    Text(
                        text = stringResource(
                            R.string.rango_2_descripcion
                        )
                    )
                }

                RangoSalarial.RANGO_3 -> {

                    Image(
                        painter = painterResource(
                            id = R.drawable.rango_3
                        ),
                        contentDescription = stringResource(
                            id = R.string.imagen_rango_3
                        )
                    )

                    Text(
                        text = stringResource(
                            R.string.rango_3_descripcion
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CampoNumerico(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        isError = isError
    )
}

@Composable
fun FilaInterruptor(
    texto: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = texto,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

fun formatearPesos(valor: Double): String {

    val formato = NumberFormat.getCurrencyInstance(
        Locale("es", "CO")
    )

    formato.maximumFractionDigits = 0

    return formato.format(valor)
}

@Preview(showBackground = true)
@Composable
fun NominaAppPreview() {
    CalculadoraNominaTheme {
        NominaApp()
    }
}