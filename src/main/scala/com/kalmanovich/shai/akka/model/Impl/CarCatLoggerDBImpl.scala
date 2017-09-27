package com.kalmanovich.shai.akka.model.Impl

import com.kalmanovich.shai.akka.model.CarCatLogger

/**
  * Created by Shai Kalmanovich on 9/27/2017.
  */
class CarCatLoggerDBImpl extends CarCatLogger {

  /**
    * <i>writeMessages</i> - This method writes the messages to the correct output handler.
    */
  override def writeMessages(): Unit = {
    // implement DB write method
    println("saved message in DB")
  }
}
