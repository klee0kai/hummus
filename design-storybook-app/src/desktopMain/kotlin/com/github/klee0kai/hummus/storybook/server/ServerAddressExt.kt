package com.github.klee0kai.hummus.storybook.server

import java.net.Inet4Address
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

object AppAddress {

    var jmDNS: JmDNS? = null

    /**
     * check with `avahi-browse -rt _http._tcp`
     * force clean up `sudo systemctl restart avahi-daemon`
     */
    fun registerDNSInLocal(
        name: String,
        port: Int,
        description: String,
    ): String {
        val realLocalAddress = realLocalAddress()
        jmDNS?.unregisterAllServices()
        jmDNS = JmDNS.create(realLocalAddress, name)
        val serviceName = name
        val serviceType = "_http._tcp.local."
        val serviceInfo = ServiceInfo.create(
            serviceType,
            serviceName,
            port,
            description,
        )
        jmDNS?.registerService(serviceInfo)
        return "http://$serviceName.local:$port"
    }

    fun unregisterDNSInLocal() {
        jmDNS?.unregisterAllServices()
    }

    fun realLocalAddress(): Inet4Address? {
        return NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { it.isUp && !it.isLoopback && !it.isVirtual }
            .flatMap { it.inetAddresses.asSequence() }
            .filterIsInstance<Inet4Address>()
            .firstOrNull { !it.isLoopbackAddress }
    }

}

