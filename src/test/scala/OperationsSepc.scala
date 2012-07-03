package com.redis

import org.specs2.mutable.Specification

object OperationsSepc extends Specification {

  "Getting and setting a key/value pair" should {
    "work for a single key/value pair" in new CommonRedisSetup {
      client.set("someKey", "awesomeValue")
      client.get("someKey").get must beEqualTo("awesomeValue").eventually(10, 500)
    }

    "work for a map of inputs" in new CommonRedisSetup {
      client.set(Map("a" -> "BB", "b" -> "AA"))
      client.get("a").get must beEqualTo("BB").eventually(10, 500)
      client.get("b").get must beEqualTo("AA").eventually(10, 500)
    }

    "and work for multiple pairs, even if they don't exist" in new CommonRedisSetup {
      client.set(Map("g" -> "BB", "h" -> "AA"))
      client.get("g", "h", "NOTEXIST") must beEqualTo(List("BB", "AA")).eventually(10, 500)
    }
  }

  "Using SETNX to avoid overwriting values" should {
    "work for single key/value pairs" in new CommonRedisSetup {
      client.set("name", "charlie")
      client.setUnlessExists("name", "fred")
      client.get("name").get must beEqualTo("charlie").eventually(10, 500)
    }

    "work when you give a Map" in new CommonRedisSetup {
      client.set(Map("cc" -> "DD", "dd" -> "CC"))
      client.setUnlessExists(Map("cc" -> "EE", "dd" -> "EE"))
      client.get("cc").get must beEqualTo("DD").eventually(10, 500)
      client.get("dd").get must beEqualTo("CC").eventually(10, 500)
    }
  }

  "Deleting an entry" should {
    "be removed from the store." in new CommonRedisSetup {
      client.set("deleteMe", "someValue")
      client.delete("deleteMe")
      client.get("deleteMe") must beEqualTo(None).eventually(10, 500)
    }
  }

  "Incrementing and Decrementing values" should {
    "work on increments" in new CommonRedisSetup {
      client.set("inc", "1")
      client.increment("inc").get must beEqualTo(2).eventually(10, 500)
    }.pendingUntilFixed

    "work on decrements" in new CommonRedisSetup {
      client.set("dec", "1")
      client.decrement("dec").get must beEqualTo(0).eventually(10, 500)
    }.pendingUntilFixed
  }

  "The call to check if a value exists" should {
    "check when a pair doesn't exist" in new CommonRedisSetup {
      client.delete("existCheck")
      client.exists("existCheck") must beEqualTo(false).eventually(10, 500)
    }

    "and check when it does exist" in new CommonRedisSetup {
      client.set("existsYes", "someValue")
      client.exists("existsYes") must beEqualTo(true).eventually(10, 500)
    }
  }

  "Checking the type of a value" should {
    "be working, somewhat..." in new CommonRedisSetup {
      client.set("typeCheck", "someString")
      client.getType("typeCheck").toString must beEqualTo("Some(+string)").eventually(10, 500)
    }
  }
}
