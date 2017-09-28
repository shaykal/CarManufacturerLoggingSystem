package com.kalmanovich.shai.akka.utils

object Consts {

  val STORE_MESSAGES_TIME_INTERVAL_KEY = "akka.actors.storeMessagesTimeInterval"
  val LOGGING_ACTOR_EXPIRE_TIME = "akka.actors.loggingActor.expireTime"
  val LOGGING_ACTOR_NUM_OF_MESSAGE_TO_FLUSH = "akka.actors.loggingActor.numOfMessageToFlush"
  val WATCH_TIME_INTERVAL_KEY = "akka.actors.wacthTimeInterval"
  val RESPONSE_INTERVAL_KEY = "akka.actors.responseTimeInterval"

  val LOGGING_ACTOR_FILE_ID = "loggingActorFileId"
  val LOGGING_ACTOR_DB_ID = "loggingActorDbId"
  val DB_WRITER_ACTOR_ID = "dbWriterActorId"
  val FILE_WRITER_ACTOR_ID = "fileWriterActorId"

  val ALIVE = "alive"

  type ActorId = String
}
