package fr.fteychene.teaching.cloud.sample

import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.Header
import org.http4k.lens.Header.CONTENT_TYPE
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import java.net.InetAddress
import java.time.Instant

data class WhoAmI(
    val time: Instant,
    val method: Method,
    val source: RequestSource?,
    val userAgent: String,
    val path: String,
    val serverIp: String
): ViewModel

val WhoAmIJsonLens = Body.auto<WhoAmI>().toLens()

fun whoami(template: BiDiBodyLens<ViewModel>): RoutingHttpHandler =
    "/whoami" bind routes(
        "{.*}" bind Method.GET to {
            val result = WhoAmI(
                time = Instant.now(),
                method = it.method,
                source = null,
                userAgent = it.header("User-Agent") ?: "unknown",
                path = it.uri.path,
                serverIp = InetAddress.getLocalHost().toString()
            )
            when(CONTENT_TYPE.get(it)) {
                ContentType.APPLICATION_JSON ->  Response(Status.OK).with(WhoAmIJsonLens of result)
                    else -> Response(Status.OK).with(template of result)
            }
        }
    )