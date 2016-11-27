import play.sbt.PlayRunHook
import sbt._

object NpmTask {
  def apply(cmd: String): PlayRunHook = {

    object NpmProcess extends PlayRunHook {
      override def beforeStarted(): Unit = {
        Process(s"npm $cmd").run
      }
    }

    NpmProcess
  }
}