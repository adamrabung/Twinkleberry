package twinkleberry

import java.io.{File,Closeable}

object AudioPlayer {
  def apply(song:File) = new JavaZoomAudioPlayer(song)
}

//just wanted to make a change directly from the web :)
trait AudioPlayer extends Closeable {
  val song:File
  var lastPosition:Int
  def play(start: Int, end: Int): Boolean
}