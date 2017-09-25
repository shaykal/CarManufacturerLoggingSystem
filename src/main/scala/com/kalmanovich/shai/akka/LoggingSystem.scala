package com.kalmanovich.shai.akka

import akka.actor.{ActorSystem, Props}
import com.kalmanovich.shai.akka.actors.WatcherActor

import scala.io.StdIn

object LoggingSystem {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("logging-system")

    val watcherActor = system.actorOf(WatcherActor.props(), "watcher")
    // Exit the system after ENTER is pressed
    StdIn.readLine()
  }
}
