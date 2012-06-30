package com.redis

import org.specs2.mutable.Specification

object OperationsSepc extends Specification {

  "Getting and setting a key/value pair" should {
    "work as described" in new CommonRedisSetup {
      client.set("someKey", "awesomeValue")
      client.get("someKey").get must beEqualTo("awesomeValue").eventually(10, 500)
    }
  }

}
