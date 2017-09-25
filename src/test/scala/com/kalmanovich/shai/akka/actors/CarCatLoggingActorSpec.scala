package com.kalmanovich.shai.akka.actors

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.kalmanovich.shai.akka.actors.CarCatLoggingActor.{Flush, Message}
import com.kalmanovich.shai.akka.model.CarCatLogger
import org.mockito.Mockito
//import org.scalamock.scalatest.MockFactory
import org.scalatest.Assertions._
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class CarCatLoggingActorSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {


  def this() = this(ActorSystem("CarCatLoggingActorSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }


  "CarCatLoggingActor" should "accumalate messages" in {
      val probe: TestProbe = TestProbe()
      val carCatLoggerMock = Mockito.spy(new CarCatLogger)
      val carCatLoggingActor = system.actorOf(CarCatLoggingActor.props(carCatLoggerMock))

      carCatLoggingActor.tell(Message("first1"), probe.ref)
      carCatLoggingActor.tell(Message("second2"), probe.ref)

      //val response = probe.expectMsgType[Message]
      //response.msg should ===("first")
      assert(carCatLoggerMock.messagesList.length == 2)
      assert(carCatLoggerMock.messagesList.head == "first1")
      assert(carCatLoggerMock.messagesList.tail.head == "second2")

      system.stop(carCatLoggingActor)
    }

  "CarCatLoggingActor" should "accumalate messages and flush" in {
    val probe: TestProbe = TestProbe()
    val carCatLoggerMock = Mockito.spy(new CarCatLogger)
    val carCatLoggingActor = system.actorOf(CarCatLoggingActor.props(carCatLoggerMock))

    carCatLoggingActor.tell(Message("first3"), probe.ref)
    carCatLoggingActor.tell(Message("second4"), probe.ref)
    assert(carCatLoggerMock.messagesList.length == 2)
    assert(carCatLoggerMock.messagesList.head == "first3" )
    assert(carCatLoggerMock.messagesList.tail.head == "second4" )

    carCatLoggingActor.tell(Flush, probe.ref)
    val response = probe.expectMsgType[String]
    response should ===("Done")
    assert(carCatLoggerMock.messagesList.size == 0)
    system.stop(carCatLoggingActor)
  }

}