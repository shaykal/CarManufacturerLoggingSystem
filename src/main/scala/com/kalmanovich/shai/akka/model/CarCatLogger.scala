package com.kalmanovich.shai.akka.model


trait CarCatLogger {

  /**
    * <i>writeMessages</i> - This method writes the messages to the correct handler. (File, DB, etc.)
    * @param messagesList - .
    */
  def writeMessages(messagesList: Seq[Any]): Unit

}