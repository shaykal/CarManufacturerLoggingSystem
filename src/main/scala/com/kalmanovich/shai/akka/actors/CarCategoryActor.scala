package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, ActorLogging, Props, Stash, Timers}
import com.kalmanovich.shai.akka.model.CarCatLogger
import com.kalmanovich.shai.akka.utils.LoggingSystemProperties

import scala.concurrent.duration._

/**
  * Created by Shai Kalmanovich on 9/26/2017.
  */
object CarCategoryActor {

  private case object TimerKey
  case object StoreMessages
  case object Idle
  lazy val storeMessageTimeInterval: String = LoggingSystemProperties.storeMessageTimeInterval

  def props(logger: CarCatLogger) : Props = Props(new CarCategoryActor(logger));

  final case class Message(msg: String)
  case object Flush

}

class CarCategoryActor(val logger: CarCatLogger) extends Actor with Stash with Timers with ActorLogging {

  import CarCategoryActor._

  timers.startPeriodicTimer(TimerKey, StoreMessages, storeMessageTimeInterval.toInt seconds)


  override def preStart(): Unit = {
    log.info("Inside preStart of CarCatLoggingActor")
  }

  override def receive = {

    case StoreMessages =>
      log.info("unstash all")
      unstashAll()
      context.become({
        case Message(msg) =>
          log.info(s"got message $msg")
          logger.addMessage(msg)
        case Empty => // TODO think about this case, how to change state
          sender() ! "Done"
          log.info("unbecoming")
          context.unbecome()
      }, discardOld = false)

    case msg => stash()
  }
}
