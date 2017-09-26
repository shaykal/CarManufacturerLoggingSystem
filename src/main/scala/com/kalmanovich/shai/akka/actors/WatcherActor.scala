package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, Props}

/**
  * Created by Shai Kalmanovich on 9/26/2017.
  */
object WatcherActor {
  //def props(groupId: String, deviceId: String): Props = Props(new Device(groupId, deviceId))
  def props(): Props = Props(new WatcherActor)

}


class WatcherActor() extends Actor {


  override def receive = ???
}