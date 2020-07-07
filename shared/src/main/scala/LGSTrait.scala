package edu.holycross.shot.greek

import edu.holycross.shot.mid.orthography._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.citevalidator._
import edu.holycross.shot.scm._
import edu.holycross.shot.dse._

import wvlet.log._
import wvlet.log.LogFormatter.SourceCodeLogFormatter


import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.annotation.tailrec

/** Representation of a Greek string written in conventional literary orthography.
*
* @param str A string in either the ascii or ucode representation of the [[LiteraryGreekString]]
* system.
*/
trait LGSTrait  extends GreekString with  Ordered[GreekString] with LogSupport  {

  //require(str.nonEmpty, "Cannot create LiteraryGreekString from empty String")
  def alphabetString: String //= LiteraryGreekString.alphabetString
  def puncutationString: String

  def vowels: Vector[Char]
  def consonants: Vector[Char]
  def accents: Vector[Char]
  def breathings: Vector[Char]

  def comboChars: Vector[Char]
  def combos: String



  /** The ASCII representation of this string.
  */
  def ascii = literaryAsciiOf(combos.replaceAll("ς","σ"))


  //val ucode =  literaryUcodeOf(fixedCombos.replace("s ","Σ ").replaceAll("s$","Σ").replaceAll("σ ", "ς ").replaceAll("σ$", "ς"))

  /** The representation of this string with glyphs in the "Greek and Coptic"
  * and "Extended Greek" blocks of Unicode.
  */
  def ucode =  {
    val lowered = literaryUcodeOf(combos).replaceAll("σ ", "ς ").replaceAll("σ$", "ς")
    lowered
  }

  /**
  */
  def fixedCombos(str: String): String = {
    if (str.head == '“') {
      '“' +  CodePointTranscoder.swapPrecedingBreathings(str.tail)
    } else {
      CodePointTranscoder.swapPrecedingBreathings(str)
    }
  }


  /** Compare this string to a second [[GreekString]] alphabetically
  * using the [[GreekString]] trait's implementation of [[asciiCompare]].
  *
  * @param that Second [[GreekString]] to compare.
  */
  override def compare(that:GreekString): Int = {
    asciiCompare(this.flipGrave.ascii, that.flipGrave.ascii)
  }


  /** Required function to convert lowercase to uppercase form.
  */
  def toUpper: LGSTrait /*= {
    ucString(ascii,"")
  }*/

  /** Recursively converts characters in src to upper case form.
  *
  * @param src Ascii representation of a [[GreekString]] to convert to upper case.
  * @param accumulator String of previously converted characters.
  */
  private def ucString(src: String, accumulator: String) : String = {
    if (src.isEmpty) {
      accumulator

    } else {
      if (src.head == '*') {
        val transferred = accumulator + src(0) + src(1)
        ucString(src.drop(2), transferred)
      } else {
        val transferred = accumulator + "*" + src(0)
        ucString(src.tail, transferred)
      }
    }
  }


  /** Required function to convert uppercase to lowercase form.
  */
  def toLower: LGSTrait /*= {
    lcString(ascii,"")
  }*/


  /** Recursively converts characters in src to upper case form.
  *
  * @param src Ascii representation of a [[GreekString]] to convert to upper case.
  * @param accumulator String of previously converted characters.

  private def lcString(src: String, accumulator: String) : LiteraryGreekString = {
    if (src.isEmpty) {
      LiteraryGreekString(accumulator)

    } else {
      if (src.head == '*') {
        lcString(src.tail, accumulator)
      } else {
        lcString(src.tail, accumulator + src.head)
      }
    }
  }
  */


  /** Capitalize first letter of the string if not already
  * in uppercase form.

  def capitalize: LiteraryGreekString = {
    if (ascii.head == '*') {
      LiteraryGreekString(ascii)
    } else {
      LiteraryGreekString("*" + ascii)
    }
  }
  */

  /** Capitalize all white-space delimited words in the string.

  def camelCase: LiteraryGreekString = {
    val splits = ascii.split(" ").toVector.map(LiteraryGreekString(_))
    val camelAscii = splits.map(_.capitalize.ascii)
    LiteraryGreekString(camelAscii.mkString(" "))
  }
  */


  /** Required function to create a new [[GreekString]] with accents removed.
  */
  def stripAccent: LGSTrait
  def stripAccentString: String = {
    stripAccs(ascii,"")
  }

  def stripBreathing: LGSTrait
  def stripBreathingString: String = {
    stripBreathingsString(ascii,"")
  }


  def stripBreathingAccent: LGSTrait
  def stripBreathingAccentString: String = {
    val noBreath = stripBreathingsString(ascii,"")
    stripAccsString(noBreath,"")
  }


  /** Create a [[LiteraryGreekString]] with grave accent (barytone) converted to acute (oxytone).*/
  def flipGrave: LGSTrait
  def flipGraveString(ascii: String): String  =  {
    ascii.replaceAll("\\\\", "/")
  }

  /** Format a transliterated version of the string value for
  * human readers. */
  def xlit: String /* = {
    val replacements = stripBreathingAccent.ascii.replaceAll("h", "ê").replaceAll("q", "th").
    replaceAll("c", "x").replaceAll("f", "ph").replaceAll("x", "ch").
    replaceAll("y", "ps").replaceAll("w", "ô")

    val uc = "\\*(.)".r
    uc.replaceAllIn(replacements,m => m.group(1).toUpperCase)
  }*/




  /** Create a [[LiteraryGreekString]] with no accent characters
  * from an `ascii` String by recursively looking at the first character
  * of the ascii string and adding it to a new string only if it is
  * not an accent.
  *
  * @param src Remaining ascii String to strip accents from.
  * @param accumulator String of non-accent characters accumulated so far.
  *
  */
  @tailrec private def stripAccs(src: String, accumulator: String): String = {
    if (src.isEmpty) {
      accumulator

    } else {
      if (isAccent(src.head)) {
        stripAccs(src.tail, accumulator)
      } else {
        stripAccs(src.tail, accumulator + src.head)
      }
    }
  }

  @tailrec private def stripBreathingsString(src: String, accumulator: String): String = {


    if (src.isEmpty) {
      accumulator

    } else {

      if (isBreathing(src.head)) {
        stripBreathingsString(src.tail, accumulator)
      } else {
        stripBreathingsString(src.tail, accumulator + src.head)
      }
    }
  }
  /** Recursively strips punctuation tokens off the end of a String,
  * to build list of tokens.
  *
  * @param s String to depunctuate.
  * @param depunctVector List of result tokens.
  * @param punctuation String containing all punctuation characters.

  @tailrec def depunctuate (s: String, depunctVector: Vector[String] = Vector.empty, punctuationChars: String = punctuationString): Vector[String] = {
    val trimmed = s.trim
    val trailChar = s"${trimmed.last}"
    if (punctuationChars.contains(trailChar)) {
      val dropLast = trimmed.reverse.tail.reverse
      if (dropLast.nonEmpty) {
        depunctuate(dropLast, trailChar +: depunctVector)
      } else {
        s +: depunctVector
      }

    } else {
      s +: depunctVector
    }
  }
  */

    /** True if given character is a vowel.
    *
    * @param c Character to check.
    */
    def isVowel (c: Character): Boolean = {vowels.contains(c)}

    /** True if given character is a consonant.
    *
    * @param c Character to check.
    */
    def isConsonant (c: Character): Boolean = {consonants.contains(c)}

    /** True if given character is alphabetic.
    *
    * @param c Character to check.
    */
    def isAlpha(c: Character): Boolean = (isVowel(c) || isConsonant(c))

    /** True if given character is an accent.
    *
    * @param c Character to check.
    */
    def isAccent(c: Character): Boolean = {accents.contains(c)}


    /** True if given character is a breathing.
    *
    * @param c Character to check.
    */
    def isBreathing(c: Character): Boolean = {breathings.contains(c)}

    /** True if given character combines with other characters in `ucode` view.
    *
    * @param c Character to check.
    */
    def isCombining(c: Character): Boolean = {
      (comboChars.contains(c) || isAccent(c) || isBreathing(c))

    }
    /** String label for class of a character.
    *
    * @param c Character to classify.
    */
    def classOfChar(c: Character): String = {
      if (vowels.contains(c)) {
        "vowel"
      } else if (consonants.contains(c)) {
        "consonant"
      } else if (breathings.contains(c)) {
        "breathing"
      } else if (accents.contains(c)) {
        "accent"
      } else if (comboChars.contains(c)) {
        "combining"
      } else {
        "invalid"
      }
    }

    @tailrec private def stripBreathings(src: String, accumulator: String): String = {
      if (src.isEmpty) {
        accumulator

      } else {

        if (isBreathing(src.head)) {
          stripBreathings(src.tail, accumulator)
        } else {
          stripBreathings(src.tail, accumulator + src.head)
        }
      }
    }
    /** Create a [[LiteraryGreekString]] with no accent characters
    * from an `ascii` String by recursively looking at the first character
    * of the ascii string and adding it to a new string only if it is
    * not an accent.
    *
    * @param src Remaining ascii String to strip accents from.
    * @param accumulator String of non-accent characters accumulated so far.
    *
    */
    @tailrec private def stripAccsString(src: String, accumulator: String): String = {
      if (src.isEmpty) {
        accumulator

      } else {
        if (isAccent(src.head)) {
          stripAccsString(src.tail, accumulator)
        } else {
          stripAccsString(src.tail, accumulator + src.head)
        }
      }
    }
}

/** Utility functions for working with definitions of the [[LiteraryGreekString]]
* class's character encoding.
*/
object LiteraryGreekStringXX  extends LogSupport  {
  Logger.setDefaultLogLevel(LogLevel.WARN)
  // 4 methods required by MidOrthography
  //
  // 1. required by MidOrthography trait
  /** Label for orthographic system.*/
  def orthography: String = "Conventional modern orthography of literary Greek"

  // 2. required by MidOrthography trait
  /** Test if cp is a valid code point.
  *
  * @param cp Code point to test.

  def validCP(cp: Int): Boolean = {
    val s = Character.toChars(cp.toInt).toVector.mkString
    val ascii = literaryAsciiOf(s)
    if (ascii.isEmpty){
      warn("NO LITERARY ASCII found for " + s)
      false
    } else {
      val asciiCP = ascii(0).toInt
      validAsciiCP(asciiCP)
    }
  }  */

  // 3. required by MidOrthography trait
  /** Complete enumeration of MidTokenCategory values. */
  def tokenCategories : Vector[MidTokenCategory] = Vector(
    PunctuationToken, LexicalToken, NumericToken
  )

  // 4. required by MidOrthography trait
  /** Tokenize a citable node.
  *
  * @param n Node to tokenize.

  def tokenizeNode(n: CitableNode): Vector[MidToken] = {
    val urn = n.urn
    // initial chunking on white space
    val lgs = LiteraryGreekString(n.text)
    val units = lgs.ascii.split("[\\s]+").filter(_.nonEmpty).toVector
    debug("TOKENIZE LGS WITH u " + lgs.ucode)
    debug("ascii " + lgs.ascii)
    debug ("units " + units)

    val classified = for ( (unit, idx) <- units.zipWithIndex) yield {
      val newPassage = urn.passageComponent + "." + idx
      val newVersion = urn.addVersion(urn.versionOption.getOrElse("") + LiteraryGreekString.exemplarId)
      val newUrn = CtsUrn(newVersion.dropPassage.toString + newPassage)

      debug(s"Look at unit ${idx}: " + unit)
      val trimmed = unit.trim

      if (trimmed.isEmpty) {
        n.text.size match {
          case 0 => {
            warn("LiteraryGreekString: empty text citable node " + n)
            Vector.empty[MidToken]
          }
          case _ => {
            warn (s"LiteraryGreekString: evil unicode detected.  Text node of length ${n.text.size} has no tokens.")
            warn("Code points were:")
            warn(LiteraryGreekString.sideBySide(n.text).mkString("\n"))
            Vector.empty[MidToken]
          }
        }

        // (n)
        Vector.empty[MidToken]
      } else {
        // Catch leading quotation?
        val tokensClassified: Vector[MidToken] = if (trimmed(0) == '"') {
            Vector(MidToken(newUrn, "\"", Some(PunctuationToken)))

        } else {
          val depunctuated = depunctuate(unit)
          val first =  MidToken(newUrn, depunctuated.head, lexicalCategory(depunctuated.head))

          val trailingPunct = for (punct <- depunctuated.tail zipWithIndex) yield {
            MidToken(CtsUrn(newUrn + "_" + punct._2), punct._1, Some(PunctuationToken))
          }
          first +: trailingPunct
        }
        tokensClassified
      }
    }
    classified.toVector.flatten
  }

  def exemplarId: String = "_lgstkn"


  def validAsciiCP(cp: Int): Boolean = {
    val cArray = Character.toChars(cp)
    alphabetString.contains(cArray(0))
  }*/













  /** Identify token type of a string.  Numeric tokens are flagged by
  * the trailing numeric tick character.  Punctuation tokens are those
  * found in an list that defaults to the punctuationString.  Other valid
  * strings are lexical tokens.
  *
  * @param s String to classify.
  * @param punctuationChars List of punctuation characters.

  def lexicalCategory(s: String, punctuationChars: String = punctuationString): Option[MidTokenCategory] = {
    if (s.last == numericTick) {
      Some(NumericToken)

    } else if (punctuationChars.contains(s)) {
      Some(PunctuationToken)

    } else if (LiteraryGreekString(s).valid) {
      Some(LexicalToken)

    } else {
      None
    }
  }

  */


  //val alphabetString = "*abgdezhqiklmncoprsΣtufxyw'.|()/\\=+,:;.“”— \n\r"





  /** Extract first series of characters from an ascii String
  * forming a single Unicode code point by recursively looking ahead
  * as long as following character is a combining character.
  *
  * @param s String to extract code point from.
  * @param accumulator String accumulasted so far.
  *

  @tailrec def peekAhead(s: String, accumulator: String): String = {
    if (s.size < 2) {
      accumulator + s
    } else {
      if (s(0) == '*') {
        if (s.size == 2) {
          accumulator + s
        } else if (isCombining(s(2))) {
          peekAhead(s.drop(2), accumulator + s.take(2))
        } else {
          accumulator + s.take(2)
        }

      } else if (isCombining(s(1))) {
        peekAhead(s.drop(1), accumulator + s.head)
      } else {
        accumulator + s.head.toString
      }
    }
  }
*/

  /** Use the [[CodePointTranscoder]] object to recursively
  * convert code points represented in `ascii` view to
  * `ucode` code points.
  *
  * @param ascii String to convert to `ucode` view.
  * @param ucode Accumluated string of Unicode code  points
  * in `ucode` view's encoding.

  @tailrec def asciiToUcode(ascii: String, ucode: String): String = {
    //Logger.setDefaultLogLevel(LogLevel.INFO)
    //debug("asciiToUcode: a vs u " + ascii + " vs " + ucode)
    if (ascii.size == 0 ) {
      ucode

    } else if (ascii.size == 1) {
      ucode + CodePointTranscoder.ucodeCodePoint(ascii)

    } else {
      val chunk = peekAhead(ascii,"")
      val newUcode = ucode + CodePointTranscoder.ucodeCodePoint(chunk)
      val newAscii = ascii.drop(chunk.size)
      asciiToUcode(newAscii, newUcode)
    }
  }
*/
  /** Recursively converts code points in a Unicode string in form NFC to
  * equivalent characters in `ascii` view.
  *
  * @param ucode String to convert.  Note that the String must be in
  * Unicode Form NFC.
  * @param ascii String of `ascii` view accumulated so far.

  def nfcToAscii(ucode: String, ascii: String): String = {
    //debug("nfcToAscii: " + ucode + " and " + ascii)
    if (ucode.size == 0 ) {
      debug("going with provided ascii " + ascii)
      ascii

    } else if (ucode.size == 1) {
      debug("transcoding ucode " + ucode)
      ascii +  CodePointTranscoder.asciiCodePoint(ucode)

    } else {
      val newUcode = ucode.drop(1)
      val newAscii = ascii + CodePointTranscoder.asciiCodePoint(ucode.head.toString)
      nfcToAscii(newUcode,newAscii )
    }
  }
  */
  // Compute Vector of code point values from String
  def strToCps(s: String, cpVector: Vector[Int] = Vector.empty[Int], idx : Int = 0) : Vector[Int] = {
   if (idx >= s.length) {
     cpVector
   } else {
     val cp = s.codePointAt(idx)
     strToCps(s, cpVector :+ cp, idx + java.lang.Character.charCount(cp))
   }
  }

  // Compose a String from a Vector of code point values
  def cpsToString(v: Vector[Int]) = {
    val chs = v.map { cp =>
      new String(Character.toChars(cp))
    }
    chs.mkString
  }

  def sideBySide(s: String) =  {
    def cpList = strToCps(s)
    cpList.map(cp => cp + s" (${cpsToString(Vector(cp))})")
  }

}
