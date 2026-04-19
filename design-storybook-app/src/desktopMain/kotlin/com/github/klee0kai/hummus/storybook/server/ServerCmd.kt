package com.github.klee0kai.hummus.storybook.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import picocli.CommandLine

@CommandLine.Command(
    name = "server",
    description = ["Launch design components browser as HTTP server"],
    mixinStandardHelpOptions = true
)
class ServerCmd : Runnable {

    @CommandLine.Option(
        names = ["-n", "--name"],
        description = ["Server local host name"],
        defaultValue = "storybook"
    )
    var hostName: String = "storybook"

    @CommandLine.Option(
        names = ["-p", "--port"],
        description = ["Server port"],
        defaultValue = "8080"
    )
    var port: Int = 8080


    override fun run() {
        launchServer(port)
    }

    private fun launchServer(port: Int) = runBlocking {
        val server = embeddedServer(Netty, port = port, host = "0.0.0.0") {
            configureRouting()
        }

        try {
            val mDnsAddress = AppAddress.registerDNSInLocal(
                name = hostName,
                port = port,
                description = "Hummus storybook website",
            )
            println("Story Book Service $mDnsAddress or http://${AppAddress.realLocalAddress()}:$port")

            server.start(wait = true)
        } catch (ex: Exception) {
            println("Error starting server: ${ex.message}")
            ex.printStackTrace()
        }

        AppAddress.unregisterDNSInLocal()
        server.stop(gracePeriodMillis = 5000, timeoutMillis = 10000)
    }


    private fun Application.configureRouting() {
        routing {
            get("/") {
                val composeHtml = findResourceFromWasmArtefacts("index-storybook-compose.html")
                call.respondBytes(
                    bytes = composeHtml!!.openStream().readBytes(),
                )
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