package twinkleberry

import java.io.File
class SongManager(val playlist: Playlist) {
  private class PauseInfo(val song: File, val frame: Int)

  private var currPlayer: AudioPlayer = _
  private var pauseInfo: Option[PauseInfo] = None

  def playNextSong() {
    play(playlist.getNextSong)
  }

  def togglePause() {
    if (pauseInfo.isEmpty) {
      pauseInfo = Some(new PauseInfo(currPlayer.song, currPlayer.lastPosition))
      currPlayer.close()
    }
    else {
      play(pauseInfo.get.song, pauseInfo.get.frame)
      pauseInfo = None
    }
  }

  private def play(song: File, startFrame: Int = 0) {
    //printf("SongManager.play: song = %s, start = %s\n", song, startFrame)
    if (currPlayer != null) {
      //printf("\t closing\n")
      currPlayer.close()
    }
    currPlayer = AudioPlayer(song)
    pauseInfo = None
    new Thread(new PlaySong(this, currPlayer, startFrame)).start()
  }

  def banSong() {
    val banned = currPlayer.song
    playNextSong()
    playlist.banSong(banned)
  }

  def banArtist() {
    val bannedSong = currPlayer.song
    playNextSong()
    playlist.banArtist(bannedSong)
  }

  def playSongs(songs:Seq[File]) = {
    playlist.enqueue(songs);
    playNextSong
  }
}

class PlaySong(val sm: SongManager, val currPlayer: AudioPlayer, startFrame: Int = 0) extends Runnable {
  def run() {
    try {
      val finished = currPlayer.play(start = startFrame, end = Integer.MAX_VALUE)
      if (finished)
        sm.playNextSong() 
    }
    catch {
      case e => e.printStackTrace()
      sm.playNextSong();
    }
    finally {
      currPlayer.close
    }
  }
}
