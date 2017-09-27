package com.kalmanovich.shai.akka.model

import scala.collection.mutable.ListBuffer

trait CarCatLogger {

  val messagesList: ListBuffer[Any] = ListBuffer.empty

  /**
    * <i>writeMessages</i> - This method writes the messages to the correct output handler.
    */
  def writeMessages(): Unit

  def addMessage(msg: Any) = messagesList += msg

}

