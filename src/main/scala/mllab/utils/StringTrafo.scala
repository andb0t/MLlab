package utils

import java.awt.Color


/** Provides auxiliary functions for string transformations */
object StringTrafo {

  /** Convert color object to RGB string */
  def convertToColorCode(col: java.awt.Color): String = {
    val binCol = col.getRGB.toBinaryString
    val b = Integer.parseInt(binCol.takeRight(8), 2)
    val g = Integer.parseInt(binCol.dropRight(8).takeRight(8), 2)
    val r = Integer.parseInt(binCol.dropRight(16).takeRight(8), 2)
    "[%d,%d,%d]".format(r, g, b)
  }

  /** Splits a string at top level commas: doesn't enter parentheses */
  def splitString(str: String, sep: String=","): List[String] =
    if (str.isEmpty) Nil
    else if (!str.contains(sep)) List(str)
    else {
      def topSep(index: Int, brackets: Int, str: String): Int =
        if (str.isEmpty()) index
        else if (str.startsWith("(")) topSep(index + 1, brackets + 1, str.tail)
        else if (str.startsWith(")")) topSep(index + 1, brackets - 1, str.tail)
        else if (str.startsWith(",") && brackets == 0) index
        else topSep(index + 1, brackets, str.tail)
      val index = topSep(0, 0, str)
      val front = str.slice(0, index)
      val rest = str.slice(index + 1, str.length)
      front :: splitString(rest, sep)
    }

    /** Retrieves string between two delimiters */
    def between(str: String, start: String = "(", end: String = ")"): String =
      str.slice(str.indexOf(start) + 1, str.lastIndexOf(end))

}
