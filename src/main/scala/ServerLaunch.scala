import launcher.WSServer

/**
  * Main File for Launching Server
  * This file needss to be run only once for
  * the entire run of instrumenter
  *
  * In case the server does not start, make sure your port 8080 is not currently
  * in use
  */
object ServerLaunch extends App{
    println(
        """
          |***************************************************************************************************************
          |                                         Java Instrumentation Server
          |***************************************************************************************************************
          |Keep this server running until your use of Instrumentation is done
          |Server runs at 127.0.0.1:8080
          |
          |***************************************************************************************************************
          |""".stripMargin)
    new WSServer
}
