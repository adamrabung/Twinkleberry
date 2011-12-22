package twinkleberry.util

import java.io.File
import twinkleberry.Util._
import twinkleberry.Config._
import scala.collection.immutable.TreeSet
import collection.mutable.HashMap
import twinkleberry.Config
import twinkleberry.SongMetadata
import java.util.Date
import twinkleberry.Playlist
import scala.xml.XML

/**
 * <item type="artist" path="Chris Lennertz" notes=""></item>
 * <item type="artist" path="The Hiders" notes=""></item>
 * <item type="artist" path="The Alexandria Quartet" notes=""></item>
 * <item type="artist" path="Miranda Lee Richards" notes=""></item>
 *
 * <item type="artist" path="Adele" notes=""></item>
 * <item type="artist" path="Brandi Carlile" notes=""></item>
 * <item type="artist" path="Edward Sharpe &amp; The Magnetic Zeros" notes=""></item>
 * <item type="artist" path="Los Campesinos!" notes=""></item>
 * <item type="artist" path="Mumford &amp; Sons" notes=""></item>
 *
 * <item type="artist" path="Glen Hansard And Marketa Irglova" notes=""></item>
 * <item type="artist" path="Marketa Irglova And Glen Hansard" notes=""></item>
 * <item type="artist" path="Glen Hansard" notes=""></item>
 *
 * <item type="artist" path="Interference" notes=""></item>
 * <item type="artist" path="Marketa Irglova" notes=""></item>
 * <item type="artist" path="Sara Bareilles" notes=""></item>
 * <item type="artist" path="Yael Na•m" notes=""></item>
 *
 * <item type="artist" path="Christina Perri" notes=""></item>
 * <item type="artist" path="Coldplay &amp; Rihanna" notes=""></item>
 */
object CreateAllArtistPlaylist extends App {
	val alreadyArtists = XML.load(new File(playlistRoot, "jen.xml").toURL) \ "item" map (_.getString("path"))
	//val artistsAlreadyInList = 
	val twoYearsAgo = System.currentTimeMillis() - (1000l * 60 * 60 * 24 * 365 * 2)
	println(new Date(twoYearsAgo))
	val newArtists = Config.musicRoot
		.findFiles()
		.filter(f => f.getName().endsWith("mp3") && (f.lastModified > twoYearsAgo))
		.map(songFile => SongMetadata.getArtist(songFile).getOrElse(songFile.getName()))
		.toSeq
		.distinct
		.filterNot(alreadyArtists.contains(_))
		.foreach(artist => println(<item type="artist" path={ artist } notes=""/>))

	//	val sampleSongForArtist = new HashMap[String, String]
	//	for (f <- Config.musicRoot.findFiles() if f.getName.endsWith("mp3")) {
	//		val artist = SongMetadata.getArtist(f)
	//		sampleSongForArtist += (artist.getOrElse("No Artist") -> f.removeRoot(Config.musicRoot))
	//	}
	//
	//	for ((artist, sampleSong) <- sampleSongForArtist) {
	//		println(<item type="artist" value={ artist } notes={ sampleSong }/>)
	//	}
}
