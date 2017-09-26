package com.kalmanovich.shai.akka.utils

import com.typesafe.config.ConfigFactory

/**
  * Created by Shai Kalmanovich on 9/26/2017.
  * This singleton will hold variables that contain properties from the application.conf file.
  */
object LoggingSystemProperties {

  lazy val storeMessageTimeInterval = ConfigFactory.load().getString(Consts.STORE_MESSAGES_TIME_INTERVAL_KEY)
  lazy val loggingActorExpireTime = ConfigFactory.load().getString(Consts.LOGGING_ACTOR_EXPIRE_TIME)
  lazy val loggingActorNumOfMessageToFlush = ConfigFactory.load().getString(Consts.LOGGING_ACTOR_NUM_OF_MESSAGE_TO_FLUSH)

}
