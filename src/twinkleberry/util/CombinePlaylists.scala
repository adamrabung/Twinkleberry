package twinkleberry.util

import java.io.File
import collection.mutable.HashSet
import twinkleberry.Playlist

object CombinePlaylists {
	def main(args: Array[String]): Unit = {
		val root = new File("/Users/Shared/Dropbox")
		val musicRoot = new File(root, "music");
		val adam = new Playlist(musicRoot, new File(root, "playlists/adam.xml"))
		val jen = new Playlist(musicRoot, new File(root, "playlists/jen.xml"))
		val playlists = List(jen, adam)
		var union = new HashSet() ++ playlists.head.songs
		for (playlist <- playlists.tail) {
			for (song <- union) {
				if (!playlist.canPlay(song)) {
					union.remove(song)
				}
			}
		}
		println(union)
	}
}