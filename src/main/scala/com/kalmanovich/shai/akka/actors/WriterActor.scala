package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.kalmanovich.shai.akka.model.CarCatLogger

/**
  * Created by Shai Kalmanovich on 9/27/2017.
  */
object WriterActor {

  def props() : Props = Props(new WriterActor())

  case class Write(messagesList: Seq[Any])
}

class WriterActor() extends Actor with ActorLogging{

 // TODO put correct dispatcher for non blocking
  override def receive: Receive = {
    case Write(messagesList: CarCatLogger) =>
      messagesList.foreach(aMessage => println(s"Message is: $aMessage"))
  }


}
