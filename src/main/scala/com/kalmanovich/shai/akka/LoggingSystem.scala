package com.kalmanovich.shai.akka

import akka.actor.{ActorSystem, Props}
import com.kalmanovich.shai.akka.actors.SupervisorActor
import com.kalmanovich.shai.akka.actors.SupervisorActor.Queue
import com.kalmanovich.shai.akka.actors.SupervisorActor.Flush
import com.kalmanovich.shai.akka.utils.Consts._

import scala.io.StdIn

object LoggingSystem {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("logging-system")

    val supervisorActor = system.actorOf(SupervisorActor.props(), "supervisor")

    Thread.sleep(2000)

    supervisorActor ! Queue(LOGGING_ACTOR_FILE_ID, "test123")
    supervisorActor ! Flush(LOGGING_ACTOR_FILE_ID)


    //supervisorActor ! Queue(LOGGING_ACTOR_DB_ID, "test345")
    // Exit the system after ENTER is pressed
    StdIn.readLine()
  }
}
