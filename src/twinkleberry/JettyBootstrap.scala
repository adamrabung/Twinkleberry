package twinkleberry

import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context
import org.mortbay.jetty.servlet.ServletHolder


import javax.servlet.http.HttpServlet
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class JettyBootstrap(val songManager: SongManager) {
  val webServer = new Server(6969)

  def applicationStarting() {
    try {
      val context = new Context(webServer, "/", Context.SESSIONS)
      val servlet = new HttpServlet() {
        override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
          println("url => " + req.getRequestURL)
          println("param => " + req.getParameterMap)
          req.getParameter("action") match {
            case "next" => songManager.playNextSong()
            case "togglePause" => songManager.togglePause()
            case "banSong" => songManager.banSong()
            case "banArtist" => songManager.banArtist()
            case "playSongs" => songManager.playSongs(parse(Option(req.getParameter("songs"))))
            case cmd => throw new RuntimeException("Don't understand command " + cmd);
          }

        }
      }
      context.addServlet(new ServletHolder(servlet), "/music")
      webServer.start()
    }
    catch {
      case e => e.printStackTrace(); throw e;
    }
  }

  def parse(songsList:Option[String]) = songsList.getOrElse("").split(",").map(new File(_))

  def applicationStopping() {
    try {
      webServer.setGracefulShutdown(1000)
      webServer.stop()
    }
    catch {
      case e => e.printStackTrace()
    }
  }
}
