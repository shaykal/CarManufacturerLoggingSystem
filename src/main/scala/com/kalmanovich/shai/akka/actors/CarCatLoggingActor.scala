package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, Props, Stash}
import com.kalmanovich.shai.akka.model.CarCatLogger

object CarCatLoggingActor {
  def props(logger: CarCatLogger) : Props = Props(new CarCatLoggingActor(logger));

  final case class Message(msg: String)
  case object Flush

}

class CarCatLoggingActor(val logger: CarCatLogger) extends Actor with Stash {
  import CarCatLoggingActor._

  override def receive = {
    case Message(msg) => logger.addMessage(msg)
    case Flush =>
      logger.write()
      sender() ! "Done"
  }
}
