package com.kalmanovich.shai.akka.actors

import akka.actor.{ActorSystem, FSM, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
  * Created by Shai Kalmanovich on 9/26/2017.
  */
class LoggingActorSpec (_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("LoggingActorSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }


  "LoggingActor FSM" should "demonstrate NullFunction" in {
    class A extends FSM[Int, Null] {
      val SomeState = 0
      when(SomeState)(FSM.NullFunction)
    }
  }

  "LoggingActor FSM" should "batch correctly" in {
    val loggingActor = system.actorOf(Props(classOf[LoggingActor]))
    loggingActor ! SetTarget(testActor)
    loggingActor ! Queue(42)
    loggingActor ! Queue(43)
    loggingActor ! Flush
    expectMsg(Write(Seq(42, 43)))
    loggingActor ! Queue(44)
    loggingActor ! Flush
    loggingActor ! Queue(45)
    expectMsg(Write(Seq(44)))
    loggingActor ! Flush
    expectMsg(Write(Seq(45)))
    loggingActor ! Queue(46)
    loggingActor ! Queue(47)
    loggingActor ! Queue(48)
    loggingActor ! Queue(49)
    loggingActor ! Queue(50)
    expectMsg(Write(Seq(46, 47, 48, 49, 50)))
    loggingActor ! Queue(51)
    loggingActor ! Flush
    expectMsg(Write(Seq(51)))
  }

  "LoggingActor FSM" should "not batch if uninitialized" in {
    val loggingActor = system.actorOf(Props(classOf[LoggingActor]))
    loggingActor ! Queue(42)
    loggingActor ! Flush
    expectNoMsg
  }

}