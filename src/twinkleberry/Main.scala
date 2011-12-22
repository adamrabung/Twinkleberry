package twinkleberry

import java.io.File
import io.Source
import javax.net.ServerSocketFactory
import java.net.{URLEncoder, URL}
import Config._

object Main {
  def main(args: Array[String]) {
    try {
      val launchSongs = args.map(new File(_))
      //still doesnt work:Salvatore Accardo-Caprice No. 24 in a Minor, Op. 1.mp3
      if (isAppRunning) {
        Source.fromURL(new URL("http://localhost:6969/music?action=playSongs&songs="+ URLEncoder.encode(launchSongs.mkString(","))))
      }
      else {
        val sm = new SongManager(new Playlist(musicRoot, playList, launchSongs))
        sm.playNextSong()

        val webServer = new JettyBootstrap(sm)
        webServer.applicationStarting();
      }
    }
    catch {
      case e => e.printStackTrace()
    }
  }

  private def isAppRunning = wasExceptionThrown { ServerSocketFactory.getDefault().createServerSocket(6969)  }

  private def wasExceptionThrown(f: => Unit) = {
    try {
      f
      false
    }
    catch {
      case _ => true
    }
  }
}
