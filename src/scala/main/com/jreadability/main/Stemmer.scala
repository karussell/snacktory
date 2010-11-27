package com.jreadability.main

import java.lang.StringBuilder
import java.io._
import scala._
import scala.io.Source  

/**
 * Scala Porter Stemmer Implementation
 *
 */
object Stemmer {
  def stem (str: String): String = {
    // check for zero length
	  if (str.length() > 3) {
	    // all characters must be letters
	    for (ch <- str toList) {
		    if (!Character.isLetter(ch)) {
		      return str.toLowerCase()
		    }
	    }
	  } 
    var s: String = step_1(str)
	  step_5(step_4(step_3(step_2(step_1(str))))).toLowerCase
  }

  def step_1(str: String): String = step_1_c(step_1_b(step_1_a(str)))

  /*
   * Step 1a
   * SSES -> SS                         caresses  ->  caress
   * IES  -> I                          ponies    ->  poni
   *                                    ties      ->  ti
   * SS   -> SS                         caress    ->  caress
   * S    ->                            cats      ->  cat
   */
  def step_1_a(str: String): String = replacePatterns(str, List( ("sses", "ss"), ("ies", "i"), ("ss", "ss"), ("s", "")), _>=0)

  /*
   * Step 1b
   * (m>0) EED -> EE                    feed      ->  feed
   *                                    agreed    ->  agree
   * (*v*) ED  ->                       plastered ->  plaster
   *                                    bled      ->  bled
   * (*v*) ING ->                       motoring  ->  motor
   *                                    sing      ->  sing
   */
  def step_1_b (str: String): String = {
    // (m > 0) EED -> EE
    if (str.endsWith("eed")) {
      if (stringMeasure(str.substring(0, str.length - 3)) > 0)
        return str.substring(0, str.length() - 1)
      // (*v*) ED ->
    } else if ((str.endsWith("ed")) &&
               (containsVowel(str.substring(0, str.length - 2)))) {
                 return step_1_b_2(str.substring(0, str.length - 2))
                 // (*v*) ING ->
               } else if ((str.endsWith("ing")) &&
                          (containsVowel(str.substring(0, str.length - 3)))) {
                            return step_1_b_2(str.substring(0, str.length - 3))
                          } // end if
    str
  } // end step1b

  /*
   * If the second or third of the rules in Step 1b is successful, the following is done:
   * AT -> ATE                       conflat(ed)  ->  conflate
   * BL -> BLE                       troubl(ed)   ->  trouble
   * IZ -> IZE                       siz(ed)      ->  size
   * 
   * (*d and not (*L or *S or *Z))                ->  single letter
   *                                 hopp(ing)    ->  hop
   *                                 tann(ed)     ->  tan
   *                                 fall(ing)    ->  fall
   *                                 hiss(ing)    ->  hiss
   *                                 fizz(ed)     ->  fizz
   * 
   * (m=1 and *o) -> E               fail(ing)    ->  fail
   *                                 fil(ing)     ->  file
   */
  def step_1_b_2 (str: String): String =  {
    
    if (str.endsWith("at") ||
        str.endsWith("bl") ||
        str.endsWith("iz")) {
          return str + "e";
        }
    else if ((str.length() > 1) && (endsWithDoubleConsonent(str)) &&
             (!(str.endsWith("l") || str.endsWith("s") || str.endsWith("z")))) {
               return str.substring(0, str.length() - 1);
             }
    else if ((stringMeasure(str) == 1) &&
             (endsWithCVC(str))) {
               return str + "e"
             }
    str
  }

  /*
   *     (*v*) Y -> I                    happy        ->  happi
   *                                     sky          ->  sky
   */
  def step_1_c(str: String): String = {
    if (str.endsWith("y") && containsVowel(str.substring(0, str.length() - 1)))
      return str.substring(0, str.length() - 1) + "i"
    str
  } // end step1c

  /*
   * (m>0) ATIONAL ->  ATE           relational     ->  relate
   * (m>0) TIONAL  ->  TION          conditional    ->  condition
   *                                 rational       ->  rational
   *  (m>0) ENCI    ->  ENCE          valenci        ->  valence
   *  (m>0) ANCI    ->  ANCE          hesitanci      ->  hesitance
   *  (m>0) IZER    ->  IZE           digitizer      ->  digitize
   *  (m>0) ABLI    ->  ABLE          conformabli    ->  conformable
   *  (m>0) ALLI    ->  AL            radicalli      ->  radical
   *  (m>0) ENTLI   ->  ENT           differentli    ->  different
   *  (m>0) ELI     ->  E             vileli        - >  vile
   *  (m>0) OUSLI   ->  OUS           analogousli    ->  analogous
   *  (m>0) IZATION ->  IZE           vietnamization ->  vietnamize
   *  (m>0) ATION   ->  ATE           predication    ->  predicate
   *  (m>0) ATOR    ->  ATE           operator       ->  operate
   *  (m>0) ALISM   ->  AL            feudalism      ->  feudal
   *  (m>0) IVENESS ->  IVE           decisiveness   ->  decisive
   *  (m>0) FULNESS ->  FUL           hopefulness    ->  hopeful
   *  (m>0) OUSNESS ->  OUS           callousness    ->  callous
   *  (m>0) ALITI   ->  AL            formaliti      ->  formal
   *  (m>0) IVITI   ->  IVE           sensitiviti    ->  sensitive
   *  (m>0) BILITI  ->  BLE           sensibiliti    ->  sensible
   */
  def step_2 (str: String): String = replacePatterns(str, List( ("ational", "ate"), ("tional","tion"), ("enci","ence"), ("anci","ance"),
                                                                 ("izer","ize"), ("bli","ble"), ("alli", "al"), ("entli","ent"),("eli","e"),
                                                                 ("ousli","ous"), ("ization","ize"), ("ation","ate"), ("ator","ate"), ("alism","al"),
                                                                 ("iveness","ive"), ("fulness","ful"), ("ousness", "ous"), ("aliti", "al"), ("iviti","ive"),
                                                                 ("biliti", "ble"), ("logi", "log")))

  /*
   * (m>0) ICATE ->  IC              triplicate     ->  triplic
   * (m>0) ATIVE ->                  formative      ->  form
   * (m>0) ALIZE ->  AL              formalize      ->  formal
   * (m>0) ICITI ->  IC              electriciti    ->  electric
   * (m>0) ICAL  ->  IC              electrical     ->  electric
   * (m>0) FUL   ->                  hopeful        ->  hope
   * (m>0) NESS  ->                  goodness       ->  good
   */
  def step_3 (str: String): String = replacePatterns(str, List( ("icate", "ic"),("ative",""),("alize","al"),("iciti","ic"),("ical","ic"),("ful",""),("ness","")))

  /*
   * (m>1) AL    ->                  revival        ->  reviv
   * (m>1) ANCE  ->                  allowance      ->  allow
   * (m>1) ENCE  ->                  inference      ->  infer
   * (m>1) ER    ->                  airliner       ->  airlin
   * (m>1) IC    ->                  gyroscopic     ->  gyroscop
   * (m>1) ABLE  ->                  adjustable     ->  adjust
   * (m>1) IBLE  ->                  defensible     ->  defens
   * (m>1) ANT   ->                  irritant       ->  irrit
   * (m>1) EMENT ->                  replacement    ->  replac
   * (m>1) MENT  ->                  adjustment     ->  adjust
   * (m>1) ENT   ->                  dependent      ->  depend
   * (m>1 and (*S or *T)) ION ->     adoption       ->  adopt
   * (m>1) OU    ->                  homologou      ->  homolog
   * (m>1) ISM   ->                  communism      ->  commun
   * (m>1) ATE   ->                  activate       ->  activ
   * (m>1) ITI   ->                  angulariti     ->  angular
   * (m>1) OUS   ->                  homologous     ->  homolog
   * (m>1) IVE   ->                  effective      ->  effect
   * (m>1) IZE   ->                  bowdlerize     ->  bowdler
   */ 
  def step_4 (str: String): String = {
    val res: String = replacePatterns(str, List( ("al",""),("ance",""),("ence",""),("er",""),("ic",""),("able",""),("ible",""),("ant",""),("ement",""),
                                                ("ment",""),("ent",""),("ou", ""),("ism",""),("ate",""),("iti",""),("ous",""),
                                                ("ive",""),("ize","")), _>1)
    if (str == res) {
      if ((str.endsWith("sion") || str.endsWith("tion")) && stringMeasure(str.substring(0, str.length() - 3)) > 1) 
        return str.substring(0, str.length() - 3)
      else
        return str
    }
    else {
      return res
    } 
  }

  def step_5 (str: String): String = step_5_b(step_5_a(str))

  /*
   * (m>1) E     ->                  probate        ->  probat
   *                                 rate           ->  rate
   * (m=1 and not *o) E ->           cease          ->  ceas
   */
  def step_5_a (str: String): String = {
    // (m > 1) E ->
    if ((stringMeasure(str.substring(0, str.length() - 1)) > 1) &&
        str.endsWith("e"))
      return str.substring(0, str.length() -1)
    // (m = 1 and not *0) E ->
    else if ((stringMeasure(str.substring(0, str.length() - 1)) == 1) &&
             (!endsWithCVC(str.substring(0, str.length() - 1))) &&
             (str.endsWith("e")))
      return str.substring(0, str.length() - 1)
    else
      return str
  } // end step5a

  /*
   * (m > 1 and *d and *L) -> single letter
   *                                 controll       ->  control
   *                                 roll           ->  roll
   */
  def step_5_b (str: String): String = {
    // (m > 1 and *d and *L) ->
    if (str.endsWith("l") &&
        endsWithDoubleConsonent(str) &&
        (stringMeasure(str.substring(0, str.length() - 1)) > 1)) {
      str.substring(0, str.length() - 1)
    } else {
      str
    }
  } // end step5b

  // does string contain a vowel?
  def containsVowel(str: String): Boolean = {
    for (ch <- str toList) {
      if (isVowel(ch))
        return true
    }
    // no aeiou but there is y
    if (str.indexOf('y') > -1)
      return true
    else
      false
  } // end function

  // is char a vowel?
  def isVowel(c: Char): Boolean = {
    for (ch <- "aeiou" toList)
      if (c == ch)
        return true
    false
  } // end function

  /*
   * Special check for 'y', since it may be both vowel and consonent depending on surrounding letters
   */
  def isVowel(str: String, i: Int): Boolean = {
    for (ch <- "aeiou" toList)
      if (str(i) == ch || (str(i) == 'y' && i > 0 && i+1 < str.length && !isVowel(str(i-1)) && !isVowel(str(i+1)) ))
        return true
    false
  } // end function

  // returns a CVC measure for the string
  def stringMeasure(str: String): Int = {
    var count = 0
    var vowelSeen: Boolean = false

    for (i <- 0 to str.length - 1) {
      if(isVowel(str, i)) {
        vowelSeen = true
      } else if (vowelSeen) {
        count += 1 
        vowelSeen = false
      }
    }
    count
  } // end function

  // does stem end with CVC?
  def endsWithCVC (str: String): Boolean = {
    if (str.length() >= 3) {
      val cvc = ( str(str.length - 1), str(str.length - 2), str(str.length - 3) )
      val cvc_str = cvc._1.toString + cvc._2 + cvc._3

      if ((cvc._1 == 'w') || (cvc._1 == 'x') || (cvc._1 == 'y')) 
        false
      else if (!isVowel(cvc._1) && isVowel(cvc_str, 1) && !isVowel(cvc._3)) 
        true
      else 
        false
    }
    else
      false
  } // end function

  // does string end with a double consonent?
  def endsWithDoubleConsonent(str: String): Boolean = {
    val c: Char = str.charAt(str.length() - 1);
    if (c == str.charAt(str.length() - 2))
      if (!containsVowel(str.substring(str.length() - 2))) {
        return true
      }
    false
  } // end function

  def replacePatterns(str: String, patterns: List[(String, String)]): String = replacePatterns(str, patterns, _>0)

  def replaceLast(str: String, pattern: String, replacement: String) = new StringBuilder(str).replace(str.lastIndexOf(pattern), str.lastIndexOf(pattern) + pattern.length, replacement).toString

  def replacePatterns(str: String, patterns: List[(String, String)], comparer: Int => Boolean): String = {
      for (pattern <- patterns)
        if (str.endsWith(pattern._1)) {
          val res = replaceLast(str, pattern._1, pattern._2)
          if (comparer(stringMeasure(replaceLast(str, pattern._1, ""))))
            return res
          else
            return str
        }
    str
  }

}
    
