package fr.fteychene.teaching.cloud.sample

import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.format.Jackson.auto
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import org.slf4j.LoggerFactory
import java.time.Instant

val logger = LoggerFactory.getLogger("main");

data class ApplicationInfo(
    val instanceId: String = System.getenv("INSTANCE_ID") ?: "DEFAULT_INSTANCE_ID",
    val appId: String = System.getenv("APP_ID") ?: "DEFAULT_APP_ID"
)

sealed class Health;
object Ok : Health()
data class Failure(val errors: List<String>) : Health()

data class HealthStatus(
    val time: Instant,
    val health: Health
)

val ApplicationInfoLens = Body.auto<ApplicationInfo>().toLens()
val HealthStatusLens = Body.auto<HealthStatus>().toLens()

data class Convertion(val time: String, val browser: String) : ViewModel

fun main() {
    logger.info("Start server")
    Thread {
        while (true) {
            logger.info("Marco !")
            Thread.sleep(3000)
            logger.info(("Polo !"))
            Thread.sleep(3000)
        }
    }.start();
    val applicationInfo = ApplicationInfo()
    val templateRenderer = HandlebarsTemplates().CachingClasspath()
    val templates = Body.viewModel(templateRenderer, TEXT_HTML).toLens()
    routes(
        static(ResourceLoader.Classpath("public")),
        "/" bind Method.GET to {
            Response(Status.PERMANENT_REDIRECT).header("Location", "index.html")
        },
        "/convertion" bind Method.GET to temperature(templates),
        database(templates),
        "/actuator" bind routes(
            "/info" bind Method.GET to {
                Response(Status.OK).with(ApplicationInfoLens of applicationInfo)
            },
            "/health" bind Method.GET to {
                Response(Status.OK).with(HealthStatusLens of HealthStatus(Instant.now(), Ok))
            }
        ),
        whoami(templates)
    ).asServer(Netty(8080)).start()
}
