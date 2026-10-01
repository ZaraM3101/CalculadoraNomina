package com.example.calculadoranomina

const val SMMLV = 1750905.0
const val AUXILIO_TRANSPORTE = 249095.0
const val HORAS_ORDINARIAS_MES = 210.0
const val APORTE_SALUD = 0.04
const val APORTE_PENSION = 0.04
const val FONDO_SOLIDARIDAD = 0.01

data class ResultadoNomina(
    val valorHora: Double,
    val totalHorasExtra: Double,
    val auxilioTransporte: Double,
    val totalDevengado: Double,
    val aporteSalud: Double,
    val aportePension: Double,
    val fondoSolidaridad: Double,
    val totalDeducciones: Double,
    val salarioNeto: Double
)

enum class RangoSalarial {
    RANGO_1,
    RANGO_2,
    RANGO_3
}

fun calcularNomina(
    salarioBasico: Double,
    horasDiurnas: Double,
    horasNocturnas: Double,
    esDominical: Boolean,
    transporteEmpresa: Boolean
): ResultadoNomina {

    val valorHora = salarioBasico / HORAS_ORDINARIAS_MES

    val factorDiurno = if (esDominical) 2.15 else 1.25
    val factorNocturno = if (esDominical) 2.65 else 1.75

    val pagoExtrasDiurnas =
        horasDiurnas * valorHora * factorDiurno

    val pagoExtrasNocturnas =
        horasNocturnas * valorHora * factorNocturno

    val totalHorasExtra =
        pagoExtrasDiurnas + pagoExtrasNocturnas

    val ibc = salarioBasico + totalHorasExtra

    val auxilioTransporte = if (
        salarioBasico <= SMMLV * 2 && !transporteEmpresa
    ) {
        AUXILIO_TRANSPORTE
    } else {
        0.0
    }

    val totalDevengado = ibc + auxilioTransporte

    val aporteSalud = ibc * APORTE_SALUD

    val aportePension = ibc * APORTE_PENSION

    val fondoSolidaridad = if (ibc >= SMMLV * 4) {
        ibc * FONDO_SOLIDARIDAD
    } else {
        0.0
    }

    val totalDeducciones =
        aporteSalud + aportePension + fondoSolidaridad

    val salarioNeto =
        totalDevengado - totalDeducciones

    return ResultadoNomina(
        valorHora = valorHora,
        totalHorasExtra = totalHorasExtra,
        auxilioTransporte = auxilioTransporte,
        totalDevengado = totalDevengado,
        aporteSalud = aporteSalud,
        aportePension = aportePension,
        fondoSolidaridad = fondoSolidaridad,
        totalDeducciones = totalDeducciones,
        salarioNeto = salarioNeto
    )
}

fun clasificarRango(salarioBasico: Double): RangoSalarial {
    return when {
        salarioBasico <= SMMLV * 2 -> RangoSalarial.RANGO_1
        salarioBasico < SMMLV * 4 -> RangoSalarial.RANGO_2
        else -> RangoSalarial.RANGO_3
    }
}