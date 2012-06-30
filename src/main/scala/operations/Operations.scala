package com.redis.operations

import com.redis.Connection

object CreateRedisCommand {
  def apply(command: String, entries: String*) =
    entries.foldLeft(command) {
      (acc, entry) => acc + " %s".format(entry)
    } + "\r\n"
}

trait Operations {

  // This sucks...
  def getConnection(key: String): Connection

  def set(key: String, value: String): Unit =
    getConnection(key).write(CreateRedisCommand("SET", key, value))

  def set(key: String, value: String, expiry: Int): Unit = {
    set(key, value.toString)
    getConnection(key).write(CreateRedisCommand("EXPIRE", key, expiry.toString))
  }

  def set(keyValues: Map[String, String]): Boolean = {
    val connection = getConnection(keyValues.toArray.apply(0)._1)
    connection.writeMultiBulk(keyValues.size * 2, "MSET", keyValues)
    connection.readBoolean
  }

  def get(key: String): Option[String] = {
    getConnection(key).write(CreateRedisCommand("GET", key))
    getConnection(key).readResponse.map{_.toString}
  }

  def get(key: String, value: String): Option[String] = {
    getConnection(key).write(CreateRedisCommand("GETSET", key, value))
    getConnection(key).readResponse.map{_.toString}
  }

  def setUnlessExists(key: String, value: String): Unit =
    getConnection(key).write(CreateRedisCommand("SETNX", key, value))

  def setUnlessExists(keyValues: Map[String, String]): Boolean = {
    val connection = getConnection(keyValues.toArray.apply(0)._1)
    connection.writeMultiBulk(keyValues.size * 2, "MSETNX", keyValues)
    connection.readBoolean
  }

  def delete(key: String): Unit =
    getConnection(key).write(CreateRedisCommand("DEL", key))

  def increment(key: String, offset: Int = 1): Option[Int] = {
    getConnection(key).write(CreateRedisCommand("INCBY", key, offset.toString))
    getConnection(key).readInt
  }

  def decrement(key: String, offset: Int = -1): Option[Int] = {
    getConnection(key).write(CreateRedisCommand("DECBY", key, offset.toString))
    getConnection(key).readInt
  }

  def exists(key: String): Boolean = {
    getConnection(key).write(CreateRedisCommand("EXISTS", key))
    getConnection(key).readBoolean
  }

  def getType(key: String): Any = {
    getConnection(key).write(CreateRedisCommand("TYPE", key))
    getConnection(key).readResponse
  }

  def ttl(key: String): Option[Int] = {
    getConnection(key).write(CreateRedisCommand("TTL", key))
    getConnection(key).readInt
  }
}