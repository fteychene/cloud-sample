package fr.fteychene.teaching.cloud.sample

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel

data class Temperature(
    val celsius: Double,
    val fahrenheit: Double,
    val kelvin: Double,
): ViewModel

fun temperature(template: BiDiBodyLens<ViewModel>): HttpHandler = {
            val base = (System.getenv("TEMPERATURE") ?: "28").toDouble()
            Response(Status.OK).with(template of Temperature(
                celsius = base,
                fahrenheit = (base  * ( 9 / 5)) + 32,
                kelvin = base + 273.15
            ))
}
