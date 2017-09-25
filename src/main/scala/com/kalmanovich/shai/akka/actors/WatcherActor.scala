package com.kalmanovich.shai.akka.actors

import akka.actor.{Actor, Props}


object WatcherActor {
  //def props(groupId: String, deviceId: String): Props = Props(new Device(groupId, deviceId))
  def props(): Props = Props(new WatcherActor)

}


class WatcherActor() extends Actor {


  override def receive = ???
}