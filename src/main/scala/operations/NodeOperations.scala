package com.redis

trait NodeOperations {
  val connection: Connection
  var db: Int

  def save: Boolean = {
    connection.write("SAVE\r\n")
    connection.readBoolean
  }

  def bgSave: Boolean = {
    connection.write("BGSAVE\r\n")
    connection.readBoolean
  }

  // Return the UNIX TIME of the last DB SAVE executed with success.
  def lastSave: Option[Int] = {
    connection.write("LASTSAVE\r\n")
    connection.readInt
  }

  // Stop all the clients, save the DB, then quit the server.
  def shutdown: Boolean = {
    connection.write("SHUTDOWN\r\n")
    connection.readBoolean
  }

  // Return different information and statistics about the server.
  def info = {
    connection.write("INFO\r\n")
    connection.readResponse
  }

  // A debugging command that outputs the whole sequence of commands received by the Redis server.
  def monitor: java.io.BufferedReader = {
    connection.write("MONITOR\r\n")
    connection.readBoolean
    connection.getInputStream
  }

  // SLAVEOF
  // The SLAVEOF command can change the replication settings of a slave on the fly.
  def slaveOf(options: Any): Boolean = options match {
    case (host: String, port: Int) => {
      connection.write("SLAVEOF "+host+" "+port.toString+"\r\n")
      connection.readBoolean
    }
    case _ => setAsMaster
  }

  def setAsMaster: Boolean = {
    connection.write("SLAVEOF NO ONE\r\n")
    connection.readBoolean
  }

  // SELECT (index)
  // selects the DB to connect, defaults to 0 (zero).
  def selectDb(index: Int): Boolean = {
    connection.write("SELECT "+index+"\r\n")
    connection.readBoolean match {
      case true => { db = index; true }
      case _ => false
    }
  }

  // FLUSHDB the DB
  // removes all the DB data.
  def flushDb: Boolean = {
    connection.write("FLUSHDB\r\n")
    connection.readBoolean
  }

  // FLUSHALL the DB's
  // removes data from all the DB's.
  def flushAll: Boolean = {
    connection.write("FLUSHALL\r\n")
    connection.readBoolean
  }

  // MOVE
  // Move the specified key from the currently selected DB to the specified destination DB.
  def move(key: String, db: Int) = {
    connection.write("MOVE "+key+" "+db.toString+"\r\n")
    connection.readBoolean
  }

  // QUIT
  // exits the server.
  def quit: Boolean = {
    connection.write("QUIT\r\n")
    connection.disconnect
  }

  // AUTH
  // auths with the server.
  def auth(secret: String): Boolean = {
    connection.write("AUTH "+secret+"\r\n")
    connection.readBoolean
  }

  // BGREWRITEAOF
  // Rewrite the append only file in background when it gets too big
  def bgRewriteAOF(): Option[String] = {
    connection.write("BGREWRITEAOF\r\n")
    connection.readString
  }

}
