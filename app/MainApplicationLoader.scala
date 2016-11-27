
import com.softwaremill.macwire._
import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import router.Routes

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class MainApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = new MainComponents(context).application
}

class MainComponents(context: Context) extends BuiltInComponentsFromContext(context) with MainModule {
  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment)
  }

  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}
