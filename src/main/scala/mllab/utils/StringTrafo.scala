package utils

import java.awt.Color


/** Provides auxiliary functions for string transformations */
object StringTrafo {

  def convertToColorCode(col: java.awt.Color): String = {
    val binCol = col.getRGB.toBinaryString
    val b = Integer.parseInt(binCol.takeRight(8), 2)
    val g = Integer.parseInt(binCol.dropRight(8).takeRight(8), 2)
    val r = Integer.parseInt(binCol.dropRight(16).takeRight(8), 2)
    "[%d,%d,%d]".format(r, g, b)
  }

}
