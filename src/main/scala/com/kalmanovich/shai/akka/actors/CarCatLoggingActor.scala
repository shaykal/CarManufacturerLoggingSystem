package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, Props, Stash, Timers}
import com.kalmanovich.shai.akka.model.CarCatLogger
import com.kalmanovich.shai.akka.utils.LoggingSystemProperties
import scala.concurrent.duration._

object CarCatLoggingActor {

  private case object TimerKey
  private case object StoreMessages
  private case object Idle
  lazy val storeMessageTimeInterval: String = LoggingSystemProperties.storeMessageTimeInterval

  def props(logger: CarCatLogger) : Props = Props(new CarCatLoggingActor(logger));

  final case class Message(msg: String)
  case object Flush

}

class CarCatLoggingActor(val logger: CarCatLogger) extends Actor with Stash with Timers {
  import CarCatLoggingActor._

  timers.startPeriodicTimer(TimerKey, StoreMessages, storeMessageTimeInterval.toInt seconds)
  stash()

  override def receive = {

    case StoreMessages =>
      unstashAll()
      context.become({
        case Message(msg) =>
          logger.addMessage(msg)
        case Idle => // TODO think about this case, how to change state
          stash()
          context.unbecome()
      }, discardOld = false)

    case Flush =>
      logger.write()
      sender() ! "Done"

  }
}
