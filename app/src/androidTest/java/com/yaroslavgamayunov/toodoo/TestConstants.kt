package com.yaroslavgamayunov.toodoo

import java.net.InetAddress
import java.net.URL

object TestConstants {
    val MOCKSERVER_ADDRESS: InetAddress = InetAddress.getByName("127.0.0.1")
    const val MOCKSERVER_PORT = 8080

    val SERVER_URL = URL("http", MOCKSERVER_ADDRESS.hostName, MOCKSERVER_PORT, "")
}