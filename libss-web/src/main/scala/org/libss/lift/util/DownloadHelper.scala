package org.libss.lift.util

import java.awt.AlphaComposite
import java.awt.image.{BufferedImage, RenderedImage}
import java.io.{ByteArrayOutputStream, InputStream}
import java.net.{HttpURLConnection, URL}
import javax.imageio.ImageIO

import org.apache.commons.io.IOUtils

/**
  * Created by Kaa 
  * on 15.06.2016 at 23:36.
  */
trait DownloadHelper extends ImageHelper {
  private def downloadHandler(urlToDownload: String, dataReader: (InputStream) => Array[Byte]) = {
    var in: InputStream = null
    try {
      val url: URL = new URL(urlToDownload)
      val uc = url.openConnection()
      val connection = uc.asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      in = connection.getInputStream
      val data = dataReader(in)
      Some(data)
    }
    catch {
      case e: Exception =>
        println(e.printStackTrace())
        None
    }
    finally {
      Option(in).foreach(_.close())
    }
  }

  def downloadFrom(url: String): Option[Array[Byte]] = {
    downloadHandler(url, IOUtils.toByteArray)
  }

  def imageDownloadAndResize(imageUrl: String,
                             maxWidthTo: Int,
                             maxHeightTo: Int) = {
    downloadHandler(imageUrl, resize(_, maxWidth = maxWidthTo, maxHeight = maxHeightTo))
  }

}

trait ImageHelper {
  def imageBytes(image: RenderedImage, format: String = "png"): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(image, format, baos)
    baos.close()
    baos.toByteArray
  }

  def resize(is: InputStream, maxWidth:Int, maxHeight:Int): Array[Byte] = {
    val originalImage: BufferedImage = ImageIO.read(is)

    val height = originalImage.getHeight
    val width = originalImage.getWidth
    if (width <= maxWidth && height <= maxHeight)
      imageBytes(originalImage)
    else {
      var scaledWidth:Int = width
      var scaledHeight:Int = height
      val ratio:Double = width/height
      if (scaledWidth > maxWidth){
        scaledWidth = maxWidth
        scaledHeight = (scaledWidth.doubleValue/ratio).intValue
      }
      if (scaledHeight > maxHeight){
        scaledHeight = maxHeight
        scaledWidth = (scaledHeight.doubleValue*ratio).intValue
      }
      val scaledBI = new BufferedImage(scaledWidth, scaledHeight,  BufferedImage.TYPE_INT_RGB)
      val g = scaledBI.createGraphics
      g.setComposite(AlphaComposite.Src)
      g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null)
      g.dispose()
      imageBytes(scaledBI)
    }
  }
}