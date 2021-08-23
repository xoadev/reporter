package dev.xoa.reporter

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        install(MicrometerMetrics) {
            registry = appMicrometerRegistry
        }

        val reporter = ApiReporterImpl(appMicrometerRegistry)

        routing {
            get("/metrics") {
                call.respond(appMicrometerRegistry.scrape())
            }

            get("/hello/{name}") {
                val name = call.parameters["name"] ?: return@get call.respondText(
                    "Missing or malformed name",
                    status = HttpStatusCode.BadRequest
                )

                reporter.infoHello_Requested(name)
                reporter.incrementHelloRequestedWithName_(name)

                call.respondText("Hello $name!")
            }
        }
    }.start(wait = true)
}
