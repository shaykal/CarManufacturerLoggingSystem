package com.kalmanovich.shai.akka.model

import scala.collection.mutable.ListBuffer

class CarCatLogger() {

  val messagesList : ListBuffer[String] = ListBuffer.empty

  def addMessage(msg: String) : Unit =
    messagesList += msg

  def write() = {
    messagesList.map(msg => println(s"msg is: $msg"))
    messagesList.clear //= ListBuffer.empty
  }

}

