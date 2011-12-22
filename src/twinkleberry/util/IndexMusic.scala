package twinkleberry.util

import java.io.File

import scala.Array.canBuildFrom

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.cmc.music.myid3.MyID3

import twinkleberry.Util.arrayToRichArray
import twinkleberry.Util.fileToRichFile
import twinkleberry.Config

object IndexMusic {
	def createIndex(musicRoot: File, indexDirectory: File) {
		indexDirectory.mkdirs
		val dir = FSDirectory.open(indexDirectory)
		//val searcher = new IndexSearcher(dir)

		//
		//    for (f:File <- musicRoot.findFiles.filter(_.getName.endsWith(".mp3"))) {
		//      println(searcher.search(new TermQuery(new Term("path", f.removeRoot(musicRoot))), 10))
		//      val matched  = searcher.search(new TermQuery(new Term("path", f.removeRoot(musicRoot))), 10).totalHits == 0
		//      println(f + "=>" + matched)
		//    }
		val unindexedFiles = musicRoot.findFiles().filter(_.getName.endsWith(".mp3")) //.filter{ f:File => searcher.search(new TermQuery(new Term("path", f.removeRoot(musicRoot))), 10).totalHits == 0 }
		//  is.close

		val analyzer = new StandardAnalyzer(Version.LUCENE_30)
		val writer = new IndexWriter(dir, analyzer, IndexWriter.MaxFieldLength.LIMITED)
		var i = 0
		for (f <- unindexedFiles) {
			val path = f.removeRoot(musicRoot)

			val id3 = new MyID3().read(f).getSimplified
			val document = new Document()
			document.add(new Field("path", path, Field.Store.YES, Field.Index.NOT_ANALYZED))
			if (id3.getSongTitle != null)
				document.add(new Field("title", id3.getSongTitle, Field.Store.YES, Field.Index.ANALYZED))
			if (id3.getArtist != null)
				document.add(new Field("artist", id3.getArtist, Field.Store.YES, Field.Index.ANALYZED))
			if (id3.getAlbum != null)
				document.add(new Field("album", id3.getAlbum, Field.Store.YES, Field.Index.ANALYZED))

			writer.addDocument(document)
			i = i + 1
			if (i % 100 == 0) { println(i) }
		}
		writer.optimize();
		writer.close();
	}

	def getU2Albums(indexDirectory: File) {
		val dir = FSDirectory.open(indexDirectory)
		val analyzer = new StandardAnalyzer(Version.LUCENE_30)
		val searcher = new IndexSearcher(dir)
		val parser = new QueryParser(Version.LUCENE_30, "artist", analyzer)
		val albums = searcher.search(parser.parse("artist = 'U2'"), 1000)
			.scoreDocs
			.map(hit => (searcher.doc(hit.doc).get("album"), searcher.doc(hit.doc).get("title")))
			.toMultiMap
		println(albums.mkString("\n"))
	}
	def searchDemo(indexDirectory: File) {
		val dir = FSDirectory.open(indexDirectory)
		val analyzer = new StandardAnalyzer(Version.LUCENE_30)
		val searcher = new IndexSearcher(dir)
		for (hit <- searcher.search(new TermQuery(new Term("path", "/u2 - A Sort Of Homecoming.mp3")), 10).scoreDocs) {
			val doc = searcher.doc(hit.doc);
			println("artists for song => " + doc.get("artist"))
		}

		val parser = new QueryParser(Version.LUCENE_30, "artist", analyzer);
		for (hit <- searcher.search(parser.parse("artist = 'Broken Bells'"), 1000).scoreDocs) {
			val doc = searcher.doc(hit.doc);
			println("u2 song => " + doc.get("title"))
		}

		for (field <- List("artist", "title")) {
			val queryString = "elvis~"
			println("searching for: " + queryString);
			val parser = new QueryParser(Version.LUCENE_30, field, analyzer);
			val query = parser.parse(queryString);
			val results = searcher.search(query, 200);
			println("total hits: " + results.totalHits);
			val hits = results.scoreDocs;
			for (hit <- hits) {
				val doc = searcher.doc(hit.doc);
				printf("\t%5.3f %s\n", hit.score, doc.get(field));
			}
		}
		searcher.close();
	}

	def main(args: Array[String]) {
		try {
			//IndexMusic.createIndex(Config.musicRoot, Config.indexRoot)
			IndexMusic.getU2Albums(Config.indexRoot)
		} catch {
			case e => e.printStackTrace
		}
	}
}
