package com.kalmanovich.shai.akka.actors

import akka.actor.{ActorRef, LoggingFSM}
import com.kalmanovich.shai.akka.utils.LoggingSystemProperties

import scala.concurrent.duration._

// Messages (Events)
// received events
final case class SetTarget(ref: ActorRef)
final case class Queue(msg: Any)
case object Flush

// sent events
final case class Batch(messages: Seq[Any])

// states
sealed trait State
case object Empty extends State
case object Accumulate extends State

// data
sealed trait Data
case object Uninitialized extends Data
final case class Todo(target: ActorRef, messages: Seq[Any]) extends Data

class LoggingActor extends LoggingFSM[State, Data] {

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
        case Todo(ref, messages) => ref ! Batch(messages)
        case _                   => // nothing to do
      }

    case Accumulate -> Accumulate =>
      log.info("inside onTransition Accumulate -> Accumulate")
      nextStateData match {
        case t @ Todo(ref, messages) =>
          if (messages.size == loggingActorNumOfMessageToFlush) { // we have 5 messages, so need to flush
            // need to "flush" and write the messages and go back to Empty state
            ref ! Batch(messages)
            //goto(Empty) using t.copy(messages = Vector.empty)
            self ! Flush
          }
        case _ => // nothing to do
      }
  }



  whenUnhandled {
    // common code for both states
    case Event(Queue(msg), t@Todo(_, msgList)) =>
      log.info("inside whenUnhandled first")
      if (msgList.size == loggingActorNumOfMessageToFlush) { // TODO I was here
        goto(Empty) using t.copy(messages = Vector.empty)
      } else {
        goto(Accumulate) using t.copy(messages = msgList :+ msg)
      }

    case Event(e, s) =>
      log.info("inside whenUnhandled second")
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  initialize()
}