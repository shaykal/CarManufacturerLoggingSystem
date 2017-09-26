package com.kalmanovich.shai.akka.actors

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.kalmanovich.shai.akka.actors.CarCategoryActor.Message
import com.kalmanovich.shai.akka.model.CarCatLogger
import org.mockito.Mockito
//import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class CarCategoryActorSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {


  def this() = this(ActorSystem("CarCategoryActorSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }


  "CarCatLoggingActor" should "accumulate messages" in {
      val probe: TestProbe = TestProbe()
      val carCatLoggerMock = Mockito.spy(new CarCatLogger)
      val carCatLoggingActor = system.actorOf(CarCategoryActor.props(carCatLoggerMock))

      println("sending first1");
      carCatLoggingActor.tell(Message("first1"), probe.ref)
      println("sending second2");
      carCatLoggingActor.tell(Message("second2"), probe.ref)
      //Thread.sleep(15000)
      //println("sending StoreMessages");
      //carCatLoggingActor.tell(StoreMessages, probe.ref)

      println("sending Idle");
      carCatLoggingActor.tell(Empty, probe.ref)
      Thread.sleep(5000)

      val response = probe.expectMsgType[String]
      response should ===("Done")

      assert(carCatLoggerMock.messagesList.length == 2)
      assert(carCatLoggerMock.messagesList.head == "first1")
      assert(carCatLoggerMock.messagesList.tail.head == "second2")

      system.stop(carCatLoggingActor)
    }
/*
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
*/
}