import org.scalatestplus.play._
import play.api.ApplicationLoader.Context

class SeleniumSpec
  extends PlaySpec
  with OneServerPerTestWithComponents[MainComponents]
  with OneBrowserPerTest
  with HtmlUnitFactory {

  override def createComponents(context: Context) = new MainComponents(context)

  "SeleniumSpec" should {

    "work from within a browser" in {

      go to ("http://localhost:" + port)

      pageSource must include ("Your new application is ready.")
    }
  }
}
