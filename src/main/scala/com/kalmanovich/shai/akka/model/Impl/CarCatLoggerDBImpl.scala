package com.kalmanovich.shai.akka.model.Impl

import com.kalmanovich.shai.akka.model.CarCatLogger

import scala.collection.mutable.ListBuffer

/**
  * Created by Shai Kalmanovich on 9/27/2017.
  */
class CarCatLoggerDBImpl extends CarCatLogger {


  override def writeMessages(messagesList: Seq[Any]): Unit = {
    // implement DB write method
    println("saved message in DB")
  }
}
