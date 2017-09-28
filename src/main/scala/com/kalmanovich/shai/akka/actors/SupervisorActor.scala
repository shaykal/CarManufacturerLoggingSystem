package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Stash, Timers}
import com.kalmanovich.shai.akka.model.Impl.{CarCatLoggerDBImpl, CarCatLoggerFileImpl}
import com.kalmanovich.shai.akka.utils.Consts._
import com.kalmanovich.shai.akka.utils.LoggingSystemProperties

import scala.concurrent.duration._

/**
  * Created by shai kalmanovich on 9/28/2017.
  */

object SupervisorActor {
  def props(): Props = Props(new SupervisorActor)

  private case object hearBeatTimerKey
  private case object responseTimerKey

  case object StartHeartBeat
  case class HeartBeat(actorId: ActorId)
  //case object HeartBeat
  //case object Alive
  case class Alive(actorId: ActorId)
  case object TimeIsUp


  case class Queue(actorId: ActorId, msg: Any)
  case class Flush(actorId: ActorId)

  //case class CreateCarCat(logger: CarCatLogger)

}


class SupervisorActor extends Actor with Stash with Timers with ActorLogging {

  import SupervisorActor._

  // properties
  lazy val watchTimeInterval = LoggingSystemProperties.watchTimeInterval.toInt
  lazy val responseTimeInterval = LoggingSystemProperties.responseTimeInterval.toInt

  // maps to control which actor is still alive and which should be sent PoisonPill and re-created.
  var writerActors = Map.empty[ActorRef, ActorId]
  var actorIdToRefMap = Map.empty[ActorId, ActorRef]
  var actorsReplied = Map.empty[ActorRef, ActorId]
  var actorsStillNeedToReply = Map.empty[ActorRef, ActorId]

  // creation of the actors
  override def preStart(): Unit = {
    log.info("inside preStart")
    val dbWriterActor: ActorRef = context.actorOf(WriterActor.props(new CarCatLoggerDBImpl), DB_WRITER_ACTOR_ID)
    writerActors += (dbWriterActor -> DB_WRITER_ACTOR_ID)
    actorIdToRefMap += (DB_WRITER_ACTOR_ID -> dbWriterActor)
    actorsStillNeedToReply += (dbWriterActor -> DB_WRITER_ACTOR_ID)
    val fileWriterActor: ActorRef = context.actorOf(WriterActor.props(new CarCatLoggerFileImpl), FILE_WRITER_ACTOR_ID)
    writerActors += (fileWriterActor -> FILE_WRITER_ACTOR_ID)
    actorIdToRefMap += (FILE_WRITER_ACTOR_ID -> fileWriterActor)
    actorsStillNeedToReply += (fileWriterActor -> FILE_WRITER_ACTOR_ID)

    // creating FSM and setting the correct actor referneces
    val loggingActorFile = context.actorOf(Props[LoggingActor], LOGGING_ACTOR_FILE_ID)
    loggingActorFile ! LoggingActor.SetTarget(fileWriterActor)
    val loggingActorDb = context.actorOf(Props[LoggingActor], LOGGING_ACTOR_DB_ID)
    loggingActorDb ! LoggingActor.SetTarget(dbWriterActor)
  }

  // timer to send HeartBeat
  timers.startPeriodicTimer(hearBeatTimerKey, StartHeartBeat, watchTimeInterval seconds)

  override def receive = {
    case StartHeartBeat =>
      log.info("inside StartHeartBeat")
      writerActors.foreach(entry => entry._1 ! HeartBeat(entry._2))
      unstashAll()
      timers.startPeriodicTimer(responseTimerKey, TimeIsUp, responseTimeInterval seconds)
      context.become(waitForResults, discardOld = false)

    case Queue(actorId, msg) => {
      log.info(s"inside Queue1(actorId, msg) : actorId is: $actorId msg is: $msg")
      actorIdToRefMap.get(actorId) match {
        case Some(ref) => ref ! LoggingActor.Queue(msg)
        case None => throw new Exception("error")
      }
    }

    case Flush(actorId) => {
      log.info(s"inside Flush1(actorId, msg) : actorId is: $actorId")
      actorIdToRefMap.get(actorId) match {
        case Some(ref) => ref ! LoggingActor.Flush
        case None => throw new Exception("error")
      }
    }

    case msg => stash()
  }


  def waitForResults : Receive = {
    case Alive(id) =>
      log.info(s"Alive(id) $id")
      actorsReplied += (sender() -> id)
      actorsStillNeedToReply -= sender()
      if(actorsStillNeedToReply.isEmpty){
        // we are done, all actors are alive
        timers.cancel(responseTimerKey)
        actorsReplied = Map.empty[ActorRef, ActorId]
        actorsStillNeedToReply = copyMap(writerActors)
        context.unbecome()
      }

    case TimeIsUp =>
      log.info(s"TimeIsUp")
      timers.cancel(responseTimerKey)
      actorsStillNeedToReply.foreach(entry => {
        val oldActorRef = entry._1
        val actorId = entry._2
        log.info(s"oldActorRef is: $oldActorRef, actorId is: $actorId")
        oldActorRef ! PoisonPill
        actorId match {
          case DB_WRITER_ACTOR_ID =>
            writerActors -= oldActorRef
            actorIdToRefMap -= actorId
            val newActorRef = context.actorOf(WriterActor.props(new CarCatLoggerDBImpl), DB_WRITER_ACTOR_ID)
            writerActors += (newActorRef -> actorId)
            actorIdToRefMap += (actorId -> newActorRef)
          case FILE_WRITER_ACTOR_ID =>
            writerActors -= oldActorRef
            actorIdToRefMap -= actorId
            val newActorRef = context.actorOf(WriterActor.props(new CarCatLoggerFileImpl), FILE_WRITER_ACTOR_ID)
            writerActors += (newActorRef -> actorId)
            actorIdToRefMap += (actorId -> newActorRef)
        }
      })
      context.unbecome()

    case Queue(actorId, msg) => {
      log.info(s"Queue2(actorId, msg) actorId is: $actorId, msg is: $msg")
      actorIdToRefMap.get(actorId).map(entry => entry ! LoggingActor.Queue(msg))
    }

    case Flush(actorId) => {
      log.info(s"inside Flush2(actorId, msg) : actorId is: $actorId")
      actorIdToRefMap.get(actorId).map(entry => entry ! LoggingActor.Flush)
    }

  }


  private def copyMap(writerActors: Map[ActorRef, ActorId]): Map[ActorRef, ActorId] = {
    var answer = Map.empty[ActorRef, ActorId]
    writerActors.foreach(entry =>
      answer += (entry._1 -> entry._2)
    )
    answer
  }

}