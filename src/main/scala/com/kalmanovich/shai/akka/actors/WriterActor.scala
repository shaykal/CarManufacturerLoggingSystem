package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.kalmanovich.shai.akka.model.CarCatLogger
import com.kalmanovich.shai.akka.utils.Consts.ActorId
//import com.kalmanovich.shai.akka.utils.Consts

/**
  * Created by Shai Kalmanovich on 9/27/2017.
  */
object WriterActor {

  def props(logger: CarCatLogger) : Props = Props(new WriterActor(logger))

  case class Write(messagesList: Seq[Any])
  case class HeartBeat(actorId: ActorId)
  case class Alive(actorId: ActorId)
  //case object HeartBeat
  //case object Alive
}

class WriterActor(logger: CarCatLogger) extends Actor with ActorLogging{

  import WriterActor._

    // TODO put correct dispatcher for non blocking
  override def receive: Receive = {
    case Write(messagesList: Seq[Any]) => {
      log.info(s"inside Write(id) messagesList is: $messagesList")
      logger.writeMessages(messagesList)
    }


    case HeartBeat(id) => {
      log.info(s"inside HeartBeat(id) id is: $id")
      sender() ! Alive(id)
    }
  }


}
