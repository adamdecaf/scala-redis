package com.redis

import org.specs2.specification.Scope

trait CommonRedisSetup extends Scope {
  type Duration = org.specs2.time.Duration

  implicit def intToDuration(i: Int): Duration = new Duration(i)

  val client = new Redis()
}

