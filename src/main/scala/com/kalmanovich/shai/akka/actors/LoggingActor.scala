package com.kalmanovich.shai.akka.actors

import akka.actor.{ActorRef, LoggingFSM}
import com.kalmanovich.shai.akka.actors.LoggingActor.{Data, State}
import com.kalmanovich.shai.akka.utils.LoggingSystemProperties

import scala.concurrent.duration._

object LoggingActor {

  // Messages (Events)
  // received events
  final case class SetTarget(ref: ActorRef)
  final case class Queue(msg: Any)
  case object Flush

  // sent events
  final case class Write(messages: Seq[Any])

  // states
  sealed trait State
  case object Empty extends State
  case object Accumulate extends State

  // data
  sealed trait Data
  case object Uninitialized extends Data
  final case class Todo(target: ActorRef, messages: Seq[Any]) extends Data

}
class LoggingActor extends LoggingFSM[State, Data] {

  import LoggingActor._

  lazy val loggingActorExpireTime: Int = LoggingSystemProperties.loggingActorExpireTime.toInt
  lazy val loggingActorNumOfMessageToFlush: Int = LoggingSystemProperties.loggingActorNumOfMessageToFlush.toInt

  override def logDepth = 12

  startWith(Empty, Uninitialized)

  when(Empty) {
    case Event(SetTarget(ref), Uninitialized) =>
      log.info("inside when Empty first")
      stay using Todo(ref, Vector.empty)
  }


  when(Accumulate, stateTimeout = loggingActorExpireTime second) {
    case Event(Flush | StateTimeout, t: Todo) =>
      log.info("inside when Accumulate second")
      goto(Empty) using t.copy(messages = Vector.empty)
  }


  onTransition {
    case Accumulate -> Empty => // This case could happen on timeout, before we have 5 messages
      log.info("inside onTransition Accumulate -> Empty")
      stateData match {
        case Todo(ref, messages) => ref ! Write(messages)
        case _                   => // nothing to do
      }
  }


  whenUnhandled {
    // common code for both states
    case Event(Queue(msg), t@Todo(_, msgList)) =>
      log.info("inside whenUnhandled first")
      if (msgList.size == loggingActorNumOfMessageToFlush-1) {
        self ! Flush
      }
      goto(Accumulate) using t.copy(messages = msgList :+ msg)

    case Event(e, s) =>
      log.info("inside whenUnhandled second")
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }


  initialize()
}