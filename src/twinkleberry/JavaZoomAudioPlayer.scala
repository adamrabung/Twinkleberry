package twinkleberry

import java.io.File;
import java.io.FileInputStream;
import java.io.Closeable

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.FactoryRegistry;

/**
 * a hybrid of javazoom.jl.player.Player tweeked to include <code>play(startFrame, endFrame)</code>
 * hopefully this will be included in the api
 */
class JavaZoomAudioPlayer(val song: File) extends AudioPlayer with Closeable {
  var lastPosition = 0

  private val bitstream = new Bitstream(new FileInputStream(song))
  private var audio = FactoryRegistry.systemRegistry().createAudioDevice()
  private val decoder = new Decoder();
  private var complete = false
  private var closed = false

  audio.open(decoder);


  /**
   * Plays a range of MPEG audio frames
   * @param start The first frame to play
   * @param end The last frame to play
   * @return true if the last frame was played, or false if there are more frames.
   */
  def play(start: Int, end: Int): Boolean = {
    var ret = true;
    var offset = start;
    while (offset > 0 && ret) {
      ret = skipFrame();
      offset = offset - 1;
    }
    return play(end - start);
  }
  
  private def play(maxFrames: Int): Boolean = {
    import DecodeStatus._
    var moreFramesRemain = Success;

    var frames = maxFrames;
    while (frames > 0 && moreFramesRemain == Success) {
      moreFramesRemain = decodeFrame()
      lastPosition = lastPosition + 1;
      frames = frames - 1
    }
    //printf("done = %s, frames = %s\n", moreFramesRemain, frames)

    if (moreFramesRemain == Done) {
      // last frame, ensure all data flushed to the audio device.
      if (audio != null) {
        audio.flush();
        //synchronized {
        complete = (!closed);
        close();
        //}
      }
    }
    return moreFramesRemain == Done;
  }

  /**
   * Closes this player. Any audio currently playing is stopped
   * immediately.
   */
  def close() {
    if (audio != null) {
      closed = true;
      lastPosition = audio.getPosition();
      audio.close();

      audio = null;
      try {
        bitstream.close();
      } catch {case e: BitstreamException =>}
    }
  }

  private object DecodeStatus extends Enumeration {val Success, Done, Error = Value}

  /**
   * Decodes a single frame.
   *
   * @return true if there are no more frames to decode, false otherwise.
   */
  private def decodeFrame(): DecodeStatus.Value = {
    try {
      import DecodeStatus._
      if (audio == null) {
        return DecodeStatus.Error;
      }

      val h = bitstream.readFrame();
      if (h == null) {
        return Done;
      }

      // sample buffer set when decoder constructed
      var output = decoder.decodeFrame(h, bitstream).asInstanceOf[SampleBuffer];

      //synchronized {
      if (audio != null) {
        audio.write(output.getBuffer(), 0, output.getBufferLength());
      }
      //}

      bitstream.closeFrame();
      return Success;
    }
    catch {case e => throw new RuntimeException("Failed to decode frame", e)}
  }

  /**
   * skips over a single frame
   * @return false if there are no more frames to decode, true otherwise.
   */
  private def skipFrame(): Boolean = {
    val h = bitstream.readFrame();
    if (h == null) {
      return false;
    }
    bitstream.closeFrame();
    return true;
  }

  def stop() {
    close();
  }
}
