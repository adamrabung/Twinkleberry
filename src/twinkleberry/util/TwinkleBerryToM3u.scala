package twinkleberry.util

import java.io.File

import scala.xml.XML

import twinkleberry.Config.musicRoot
import twinkleberry.Config.playlistRoot
import twinkleberry.Util.elemToRichElem
import twinkleberry.Util.fileToRichFile
import twinkleberry.SongMetadata

object TwinkleBerryToM3u extends App {
	def updateJenM3u {
		val goodArtists = XML.load(new File(playlistRoot, "jen.xml").toURL) \ "item" map (_.getString("path"))
		val start = System.currentTimeMillis()
		val goodSongs = musicRoot.findFiles
			.filter(f => f.getName.endsWith("mp3") && goodArtists.contains(SongMetadata.getArtist(f).getOrElse(1)))
			.map("../music/" + _.removeRoot(musicRoot, removeLeadingSlash = true))
		println("wrote " + goodSongs.size + " to jen m3u")
		new File(playlistRoot, "jen.m3u").write(goodSongs.mkString("\n"))
	}

	updateJenM3u
}