package twinkleberry.util

import twinkleberry.Util._
import twinkleberry.Config

object FindDupes {
	def main(args: Array[String]) {
		val pathsForSize = Config.musicRoot.findFiles().filter(_.getName.endsWith(".mp3")).groupBy(mp3File => mp3File.length)
		for ((size, files) <- pathsForSize.elements) {
			if (files.size > 1) {
				println(size)
				for (file <- files) {
					println("\t" + file)
				}
				println
			}
		}
	}
}