package twinkleberry

import collection.mutable.ListBuffer
import scala.io.Source
import java.io.{FileWriter, File}
import Util._
import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap
import scala.collection.mutable.{Set => MutableSet}


object Util {
  implicit def elemToRichElem(e:scala.xml.Node) = new {
    def getString(name:String) = {
      e.attribute(name) match {
        case Some(Seq(onlyElem)) => onlyElem.text
        case x => throw new RuntimeException("No attribute found for name '" + name + "' on " + e)
      }
    }

    def getEnum[T <: Enumeration](name:String, enum:T):T#Value = {
      e.attribute(name) match {
        case Some(Seq(onlyElem)) => enum.withName(onlyElem.text)    //why cant i get implicits working here
        case x => throw new RuntimeException("No attribute found for name '" + name + "' on " + e)
      }
    }
  }

  implicit def optionToRichOption[T<:Any](o:Option[T]) = new {
    def check(msg:String) = o match {
      case Some(x) => x
      case None => throw new RuntimeException(msg)
    }
  }

/**
  implicit def enumToRichEnum(e:Enumeration) = new {
    def parse(name:String):Enumeration#Value = e.valueOf(name)  match {
        case Some(x) => x
        case _ => throw new RuntimeException("No enum named '" + name + "' found: legal values: " + e.values)
    }
  }
*/
  
  implicit def iterableToRichIterable[A](a: Iterable[A]) =
    new {
      def shuffle = scala.util.Random.shuffle(a)
    }

  implicit def seqToRichSeq[A](a: Seq[A]) =
    new {
      def toMultiMap[K, V](implicit ev: A <:< (K, V)): scala.collection.mutable.MultiMap[K, V] = {
        val retval = new scala.collection.mutable.HashMap[K, scala.collection.mutable.Set[V]]() with scala.collection.mutable.MultiMap[K,V]
        a.foreach(pair => retval.addBinding(pair._1, pair._2))
        retval
      }
      
      def getFirst(filter:A=>Boolean) = {
        var retval:Option[A] = None
        var index = 0
        while (! retval.isDefined && index < a.size) {
          if (filter(a(index))) {
            retval = Some(a(index))
          }
          index += 1
        }
        retval.check("Could not get first match for " + filter + ": nothing matched")
      }

    }

  class RichTraversable[A](trav:Traversable[A]) {
    def toMultiMap[K, V](implicit ev: A <:< (K, V)): scala.collection.mutable.MultiMap[K, V] = {
      val retval = new HashMap[K, MutableSet[V]]() with MultiMap[K,V]
      trav.foreach(pair => retval.addBinding(pair._1, pair._2))
      retval
    }
  }
  implicit def traversableToRichTraversable[A](a: Traversable[A]) = new RichTraversable(a)
  implicit def arrayToRichArray[A](a: Array[A]) = new RichTraversable(a)

  implicit def fileToRichFile(f: File) = new RichFile(f);

  class RichFile(file: File) {
    def removeRoot(rootDir: File, removeLeadingSlash:Boolean=false) = {
      if (file.getAbsolutePath.startsWith(rootDir.getAbsolutePath)) {
        val sub = file.getAbsolutePath.stripPrefix(rootDir.getAbsolutePath)
        if (removeLeadingSlash) {
          sub.substring(1)
        }
        else {
          sub
        }
      }
      else {
        throw new RuntimeException(file + " does not appear to be a under " + rootDir);
      }
    }

    def ensureDeleted() {
      if (file.exists()) {
        delete(file);
      }
    }

    private def delete(f:File) {
      if (f.isDirectory()) {
        f.listFiles.foreach(delete(_)) 
      }

      // The directory is now empty so delete it
      val worked = f.delete();
      if (!worked) {
        throw new RuntimeException("Failed to delete " + f);
      }
    }

    def getLines() = Source.fromFile(file).getLines().map(_.trim)

    def write(text : String) = {
      val fw = new FileWriter(file)
      try{ fw.write(text) }
      finally{ fw.close }
    }
    
    def findFiles(): Iterable[File] = {
      def findFiles(root: File): Iterable[File] = {
        val retval = ListBuffer[File]()
        for (f <- root.listFiles()) {
          if (f.isDirectory()) {
            retval.appendAll(findFiles(f));
          }
          else {
            retval.append(f);
          }
        }
        return retval;
      }
      return findFiles(file)
    }
  }
}
