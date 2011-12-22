package twinkleberry

import java.util.Properties
import java.io.{FileInputStream, File}
import Util._

object Config {
  val config = new Properties()
  
  val twinkleHome = List(System.getProperty("twinkleberry.home"), System.getProperty("user.dir")).getFirst(_ != null)
  config.load(new FileInputStream(twinkleHome + "/conf/twinkleberry.conf"))
  val musicRoot = new File(config.get("musicRoot").asInstanceOf[String])
  val playlistRoot = new File(config.get("playlistRoot").asInstanceOf[String])
  val playList = new File(playlistRoot, config.get("initialPlaylist").asInstanceOf[String])
  val indexRoot = new File("/Users/arabung/temp/indexes")
}