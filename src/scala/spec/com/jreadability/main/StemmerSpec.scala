package com.jreadability.main

import scala.io.Source
import org.specs._
import org.specs.runner.JUnit
import com.getvouched.keywords._
import java.lang.String

class StemmerSpec extends SpecificationWithJUnit {
  "Stemmer" should {
    "Stem the word" in {
      for (testCase <- Source.fromFile("./test_data/vocabulary").getLines)
      {
        val stem = Stemmer
        val testMatch  = """^(.*),(.*)$""".r
        val testMatch (input,output) = testCase
        if (input.length > 3) {
          stem.stem(input) must beEqual(output)
        }
      }
    }
  }
}
