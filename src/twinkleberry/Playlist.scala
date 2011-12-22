package twinkleberry

import java.io.{FileWriter, File}

import scala.collection.mutable.MultiMap
import scala.collection.mutable.HashMap
import scala.collection.mutable.Set
import Util._
import xml.{PrettyPrinter, XML}
import java.lang.String

object PlaylistType extends Enumeration { type PLType = Value; val include, exclude = Value }


class Playlist(val musicRoot: File, val playList: File, launchSongs:Seq[File] = List()) {
  var playlistElement = XML.loadFile(playList)
  val itemForType = new HashMap[String, Set[String]] with MultiMap[String, String]
  val playlistType = playlistElement.getString("type") match { 
	case "include" => PlaylistType.include
	case "exclude" => PlaylistType.exclude
}
  //val playlistType = playlistElement.getEnum[PlaylistType.type]("type", PlaylistType)
  playlistElement \\ "item" foreach {  node =>
      itemForType.addBinding(
      node.getString("type"),
      node.getString("path"))
  }

  var songs = launchSongs.iterator ++ (musicRoot.findFiles
          .filter(_.getName.endsWith(".mp3"))
          .shuffle
          .iterator
          .filter(canPlay(_)))


  def canPlay(f: File) = {
    val exclusive = playlistType == PlaylistType.exclude
    
    if (!f.getName().endsWith("mp3")) {
      println("Skipping weird song " + f)
      false
    }
    else if (itemForType.getOrElseUpdate("song", Set()).contains(f.removeRoot(musicRoot))) {
      println("Skipping banned song " + f.removeRoot(musicRoot))
      !exclusive
    }
    else if (itemForType.getOrElseUpdate("artist", Set()).contains(SongMetadata.getArtist(f).getOrElse(""))) {
      println("Skipping banned artist " + SongMetadata.getArtist(f).getOrElse("No Artist"))
      !exclusive
    }
    else {
      println("Play " + f.removeRoot(musicRoot))
      exclusive
    }
  }

  def mojo(pt:PlaylistType.Value) {
    println(pt)
  }

  def getNextSong = {
    if (songs.hasNext) {
      songs.next
    }
    else {
      throw new RuntimeException("Out of songs!");
    }
  }

  def banSong(song: File) = {
    ban("song", song.removeRoot(musicRoot))
  }

  def banArtist(song: File) = {
    SongMetadata.getArtist(song)  match {
      case Some(artist) => ban("artist", artist)
      case _ =>
    }                                               
  }

  def enqueue(newSongs: Seq[File]) {
    songs = newSongs.filter(_.exists).iterator.append(songs) 
  }

  private def ban(banType:String, banItem:String) {
    println("Banning " + banType + " " + banItem)
    playlistElement = playlistElement match {
      case <playlist>{ bans @ _* }</playlist> => <playlist type="exclude">{ bans } <item type={banType} path={banItem} /></playlist>
    }
    println("Ban " + banType + " " + banItem)
    val fw = new FileWriter(playList)
    try {fw.write(new PrettyPrinter(width = 500, step = 6).format(playlistElement))}
    finally {fw.close}
  }
}
