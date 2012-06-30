package com.redis

object CreateRedisCommand {
  def apply(command: String, entries: String*) =
    entries.foldLeft(command) {
      (acc, entry) => acc + " %s".format(entry)
    } + "\r\n"
}
