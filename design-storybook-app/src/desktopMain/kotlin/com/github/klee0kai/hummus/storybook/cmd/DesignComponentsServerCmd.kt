package com.github.klee0kai.hummus.storybook.cmd

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.http.ContentType
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import java.net.InetAddress
import java.net.NetworkInterface

@CommandLine.Command(
    name = "server",
    description = ["Launch design components browser as HTTP server"],
    mixinStandardHelpOptions = true
)
class DesignComponentsServerCmd : Runnable {

    @CommandLine.Option(
        names = ["-p", "--port"],
        description = ["Server port"],
        defaultValue = "8080"
    )
    var port: Int = 8080

    override fun run() {
        launchServer(port)
    }

    private fun launchServer(port: Int) {
        val server = embeddedServer(Netty, port = port, host = "0.0.0.0") {
            configureRouting()
        }

        try {
            server.start(wait = false)

            val hostName = try {
                InetAddress.getLocalHost().hostName
            } catch (_: Exception) {
                "design-storybook"
            }

            val networkAddresses = getNetworkAddresses()

            println()
            println("═══════════════════════════════════════════════════════════════")
            println("  Design Storybook Server Started")
            println("═══════════════════════════════════════════════════════════════")
            println("  Local access:     http://localhost:$port")

            if (networkAddresses.isNotEmpty()) {
                networkAddresses.forEach { ip ->
                    println("  Network access:   http://$ip:$port")
                }
            }

            println("  Hostname:         $hostName")
            println("  Port:             $port")
            println()
            println("  Server is listening on 0.0.0.0:$port")
            println("  Accessible from any device on your network")
            println("═══════════════════════════════════════════════════════════════")
            println()

            runBlocking {
                server.stop(gracePeriodMillis = 5000, timeoutMillis = 10000)
            }

        } catch (ex: Exception) {
            println("Error starting server: ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun getNetworkAddresses(): List<String> {
        return try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .flatMap { iface ->
                    iface.inetAddresses
                        .toList()
                        .filter { addr ->
                            !addr.isLoopbackAddress && addr.hostAddress.contains(".")
                        }
                        .map { it.hostAddress }
                }
                .distinct()
                .sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun Application.configureRouting() {
        routing {
            get("/") {
                val indexHtml = loadResourceFromWasm("index.html")
                if (indexHtml != null) {
                    call.respondText(String(indexHtml), ContentType.Text.Html)
                } else {
                    call.respondText("Design Components Browser", ContentType.Text.Plain)
                }
            }

            get("/{...}") {
                val path = call.parameters.getAll("")?.joinToString("/") ?: ""

                val resourceBytes = loadResourceFromWasm(path)
                if (resourceBytes != null) {
                    val contentType = when {
                        path.endsWith(".wasm") -> ContentType("application", "wasm")
                        path.endsWith(".js") -> ContentType.Application.JavaScript
                        path.endsWith(".css") -> ContentType.Text.CSS
                        path.endsWith(".html") -> ContentType.Text.Html
                        else -> ContentType.Application.OctetStream
                    }
                    call.respondBytes(resourceBytes, contentType)
                } else {
                    val indexHtml = loadResourceFromWasm("index.html")
                    if (indexHtml != null) {
                        call.respondText(String(indexHtml), ContentType.Text.Html)
                    } else {
                        call.respondText("Not Found", ContentType.Text.Plain)
                    }
                }
            }
        }
    }

    private fun loadResourceFromWasm(path: String): ByteArray? {
        return try {
            val classLoader = this::class.java.classLoader ?: ClassLoader.getSystemClassLoader()

            val resourcePaths = listOf(
                path,
                "wasm/$path",
                "wasm-artifacts/$path",
            )

            for (resourcePath in resourcePaths) {
                classLoader.getResourceAsStream(resourcePath)?.use {
                    return it.readBytes()
                }
            }

            null
        } catch (e: Exception) {
            null
        }
    }
}
