package twinkleberry.util

import java.io.File
import twinkleberry.Config._
import twinkleberry.Util._
import scala.xml.PrettyPrinter
import twinkleberry.SongMetadata

object CreateArtistWhiteListFromPlaylist {
	def substringAfter(s: String, pattern: String) = s.substring(s.indexOf(pattern) + 1)

	def main(args: Array[String]) {
		val file = new File(musicRoot, "jen.m3u")
		val filesInPlaylist = file.getLines.map(new File(musicRoot, _)).toList
		println("filesInPlaylist " + filesInPlaylist.size)

		val whitelist = Set("The Avett Brothers", "Diane Birch", "Elvis Perkins") ++ filesInPlaylist
			.map(SongMetadata.getArtist(_))
			.collect { case Some(artist) => artist }
			.toSet

		val nicePlayList =
			<playlist type="include">
				{ whitelist.map { artist: String => <item path={ artist } type="artist"/> } }
			</playlist>

		new File(playlistRoot, "jen.xml").write(new PrettyPrinter(200, 1).format(nicePlayList))
		//    val millisInADay:Long = 1000 * 60 * 60 * 24
		//    val blacklistedArtists = musicRoot.findFiles
		//            .filter{f:File =>  f.getName.endsWith("mp3") && !filesInPlaylist.contains(f) }
		//            .filter(whitelist.contains(SongMetadata.getArtist(_)))
		//            .map(<item path="{_}" type="song"})
		//
		//    println("artists " + whitelist.size)
		//    var i = 0
		//    val blacklistedArtists = musicRoot.findFiles
		//            .filter{f:File =>  f.getName.endsWith("mp3") && !filesInPlaylist.contains(f) }
		//            .filter(_.lastModified > System.currentTimeMillis() - (31 * 6 * millisInADay))
		//            .map(SongMetadata.getArtist(_))
		//            .partialMap { case Some(artist) => artist }
		//            .toSet
		//    println(blacklistedArtists.mkString("\n"))
		//    println("Consider adding: " + blacklistedArtists.diff(whitelist).mkString("\n"))
		//    val also = Set("The Avett Brothers","Diane Birch","Elvis Perkins")
	}

}