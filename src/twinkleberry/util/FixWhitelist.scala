package twinkleberry.util

import java.io.File
import java.io.FileWriter
import scala.xml.XML
import scala.xml.PrettyPrinter

object FixWhitelist {
	def main(args: Array[String]) {
		try {
			//<exclude artist="Keith Jarrett"></exclude>
			//<exclude song="Dave Matthews Band - Mother Father.mp3"></exclude>
			//<exclude type="song" item="Frou Frou - Let Go.mp3">
			val root = new File("/Users/Shared/Dropbox/playlists/adam.xml")
			val pl = XML.loadFile(root)
			val updated = pl \\ "exclude" map (_ match {
				case x @ <exclude/> if (x.attribute("type").get == "song") => <exclude type="song" item={ "/" + x.attribute("item").get }/>
				case x @ _ => x
			})
			val newDoc = <playlist>
				{ updated }
			</playlist>
			val prettyXML = new PrettyPrinter(width = 500, step = 6).format(newDoc)
			println(prettyXML)
			val fw = new FileWriter(new File("/Users/Shared/Dropbox/playlists/adam.xml"))
			try { fw.write(prettyXML) }
			finally { fw.close }
		} catch {
			case e => e.printStackTrace()
		}
	}
}
