package twinkleberry

import java.io.File
import org.cmc.music.myid3.MyID3;

object SongMetadata {
  def getArtist(song: File) = {
    val f = new MyID3().read(song)
    if (f!=null)
      Option(f.getSimplified.getArtist)
    else
      None
  }
}
