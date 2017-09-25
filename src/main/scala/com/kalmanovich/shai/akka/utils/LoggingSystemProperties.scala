package com.kalmanovich.shai.akka.utils

import com.typesafe.config.ConfigFactory

object LoggingSystemProperties {

  lazy val storeMessageTimeInterval = ConfigFactory.load().getString(Consts.STORE_MESSAGES_TIME_INTERVAL_KEY)

}
