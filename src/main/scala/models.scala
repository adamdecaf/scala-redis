package com.redis

case class Connection(val host: String, val port: Int) extends SocketOperations
