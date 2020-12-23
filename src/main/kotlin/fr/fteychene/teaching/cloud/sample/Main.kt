package fr.fteychene.teaching.cloud.sample

import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer

data class ApplicationInfo(
    val instanceId: String = System.getenv("INSTANCE_ID") ?: "DEFAULT_INSTANCE_ID",
    val appId: String = System.getenv("APP_ID") ?: "DEFAULT_APP_ID"
)

val ApplicationInfoLens = Body.auto<ApplicationInfo>().toLens()


fun main() {
    val applicationInfo = ApplicationInfo()
    routes(
        "/" bind Method.GET to {
            Response(Status.OK).with(ApplicationInfoLens of applicationInfo)
        }
    ).asServer(Netty(8080)).start()
}