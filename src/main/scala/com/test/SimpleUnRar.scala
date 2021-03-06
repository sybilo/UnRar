/*
 * Copyright (C) 2019-present, Chenai Nakam(chenai.nakam@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test

import java.io.{File, FileNotFoundException, FileOutputStream, FileWriter}
import java.util.regex.Pattern
import java.util.Date
import de.innosystec.unrar.Archive
import de.innosystec.unrar.exception.RarException
import hobby.chenai.nakam.lang.J2S._

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 07/09/2019
 */
object SimpleUnRar extends App with Src {
  @volatile var needStop = false
  val debug = false

  //  (step1 :: step2 :: step3 :: step4 :: step5 :: step6 :: step7 :: step8 :: Nil).
  (wordsReg1 :: wordsReg2 :: wordsReg3 :: wordsReg4 :: wordsReg5 :: wordsReg6
    :: wordsReg7 :: wordsReg8 :: Nil).zipWithIndex.foreach { wr =>
    Worker.sThreadPoolExecutor.submit({
      exec13(wr._1, wr._2)
      }.run$)
  }
  //  breakable {
  //    for (n <- (12000 to 99930000).reverse) {
  //      if (needStop) break
  //      Worker.sThreadPoolExecutor.submit({
  //        try {
  //          exec(String.format("%08d", Integer.valueOf(n)))
  //        } catch {
  //          case t: Throwable => t.printStackTrace()
  //        }
  //        }.run$)
  //      println(s"count: $n")
  //      while (Worker.sPoolWorkQueue.size() >= 5000) {
  //        try Thread.sleep(1500) catch {
  //          case t: Throwable => t.printStackTrace()
  //        }
  //      }
  //      Thread.`yield`()
  //    }
  //  }

  def execSteps(from: Int, to: Int, step: Int): Unit = {
    breakable {
      for (n <- from until to) {
        if (needStop) break
        val ns = String.format("%08d", Integer.valueOf(n))
        if (n % 100 == 0) println(s"[$from, $ns, $to] step: $step")
        exec(ns)
      }
    }
  }

  def exec13(wordsReg: WordsReg, n: Int): Unit = {
    var count = 0
    while (!needStop) {
      val pwd_maybe = wordsReg.next()
      /*if (count % 1000 == 0) */ println(s"$pwd_maybe, count: $count, N:$n.")
      exec(pwd_maybe)
      count += 1
    }
  }

  def exec(f: => String): Unit = {
    if (needStop) return
    val timeBegin = System.nanoTime()
    val pwdMaybe = f // wordsRegs((Math.random() * wordsRegs.length).toInt).next()
    //    println(s"[${new Date(System.currentTimeMillis())}]----> pwd_maybe: $pwdMaybe <----------------------------")

    def boo: Boolean = {
      val archive = new Archive(src, pwdMaybe, false)
      try {
        //  println("archive: " + archive)
        //  println("MAINHEADER:")
        //  archive.getMainHeader.print() // 打印文件信息.
        //  println("FILEHEADER:")
        val fh = archive.nextFileHeader
        archive.extractFile(fh, null)
        true
      } catch {
        case e: RarException =>
          //          println("RarException: " + e.getMessage)
          false
        case e: Throwable =>
          e.printStackTrace()
          false
      } finally {
        try archive.close() catch {
          case _: Throwable =>
        }
      }
    }

    if (boo) {
      // done.
      needStop = true
      println(s"----------------------------- DONE >>> pwd_final: $pwdMaybe -------------------------------------")

      if (!dst.exists) dst.mkdirs
      // 将密码输出到文件。
      val writer = new FileWriter(dst.getPath / "pwd_final.txt")
      try {
        writer.append(pwdMaybe)
        writer.append("\n")
        writer.flush()
        writer.close()
      } catch {
        case t: Throwable => t.printStackTrace()
      }

      val archive = new Archive(src, pwdMaybe, false)
      var fhd = archive.nextFileHeader
      var fout: FileOutputStream = null
      try {
        while (fhd.nonNull) {
          val fileName = if (existZH(fhd.getFileNameByteArray)) fhd.getFileNameW else fhd.getFileNameString
          println("fileName: " + fileName)
          val path = dst.getPath / fileName.replaceAll("""\\""", "/")
          println("path: " + path)
          val dir = new File(path).getParentFile
          if (!dir.exists) dir.mkdirs
          val b = try {
            fout = new FileOutputStream(path)
            true
          } catch {
            case _: FileNotFoundException => false
          }
          if (b) {
            archive.extractFile(fhd, fout)
            try fout.close() catch {
              case _: Throwable =>
            }
          }
          fhd = archive.nextFileHeader
        }
      } catch {
        case t: Throwable =>
          println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 测试 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
          t.printStackTrace()
          println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
      } finally {
        try archive.close() catch {
          case _: Throwable =>
        }
        if (fout.nonNull) try fout.close() catch {
          case _: Throwable =>
        }
      }
      println(s"[DONE]> [${new Date(System.currentTimeMillis())}] --- pwd_final: $pwdMaybe ----- time duration: ${(System.nanoTime() - timeBegin) * 1e-9}s")
    } else {
      //      println(s"[NO DONE]> [${new Date(System.currentTimeMillis())}] --- pwd_maybe: $pwdMaybe ----- time duration: ${(System.nanoTime() - timeBegin) * 1e-9}s")
    }
  }

  lazy val wordsReg1 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg2 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg3 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg4 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg5 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg6 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg7 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg8 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))

  // ASCii 字符序：
  // ! " # $ % & ' ( ) * + , - . /
  // 0 1 2 3 4 5 6 7 8 9 : ; < = > ? @
  // A B C D E F G H I J K L M N O P Q R S T U V W X Y Z [ \ ] ^ _ `
  // a b c d e f g h i j k l m n o p q r s t u v w x y z { | } ~

  // 可能的字符集：
  // ! # $ % ^ & * - _
  // 0 1 2 3 4 5 6 7 8 9
  // A-Z
  // a-z
  // 可能的字符出现频率：
  //  lazy val wordsReg01 = new WordsReg(13,
  //    new WordRule(3, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(1, "!#$%&*-_".toCharArray))
  //  lazy val wordsReg02 = new WordsReg(13,
  //    new WordRule(3, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(1, "!#$%&*-_".toCharArray))
  //  lazy val wordsReg03 = new WordsReg(13,
  //    new WordRule(3, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(1, "!#$%&*-_".toCharArray))
  //  lazy val wordsReg04 = new WordsReg(13,
  //    new WordRule(3, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(1, "!#$%&*-_".toCharArray))
  //  lazy val wordsReg05 = new WordsReg(13,
  //    new WordRule(4, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(2, "!#$%&*-_".toCharArray))
  //  // 以下考虑没有特殊字符的情况
  //  lazy val wordsReg06 = new WordsReg(13,
  //    new WordRule(4, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')))
  //  lazy val wordsReg07 = new WordsReg(13,
  //    new WordRule(4, ('A', 'Z')),
  //    new WordRule(12, ('0', '9'), ('a', 'z')))
  //  lazy val wordsReg08 = new WordsReg(13,
  //    new WordRule(4, ('A', 'Z')),
  //    new WordRule(12, ('0', '9'), ('a', 'z')))
  //  lazy val wordsReg09 = new WordsReg(13,
  //    new WordRule(3, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(1, "_".toCharArray, '_'))
  //  lazy val wordsReg10 = new WordsReg(13,
  //    new WordRule(3, ('A', 'Z')),
  //    new WordRule(30, ('0', '9'), ('a', 'z')),
  //    new WordRule(2, "!#$%&*-_".toCharArray))

  ///////////////////////////////////////////////////////////////////////////////////

  class WordsReg(fixedSize: Int, wordRules: WordRule*) {
    require(fixedSize <= (0 /: wordRules) { (sum, w) => sum + w.weight })

    def next(): String = {
      def remainingSize = (0 /: wordRules) { (n, w) =>
        n + w.remaining
      }

      require(remainingSize >= fixedSize)

      val array = new Array[Char](fixedSize)

      for (n <- 0 until fixedSize) {
        val rem = remainingWordRules(array, n)
        val rule = rem((Math.random() * rem.length).toInt)
        array(n) = {
          var next = rule.next()
          var bool = true
          while (n > 0 && bool) {
            bool = (0 until n).exists(array(_) == next)
            if (bool) next = rule.next()
          }
          rule.nextDone()
          next
        }
      }
      reset()
      array.mkString("")
    }

    private def remainingWordRules(array: Array[Char], index: Int): Seq[WordRule] = {
      var seq: List[WordRule] = Nil
      wordRules.foreach { w =>
        if (w.hasNext && (
          if (index < fixedSize * 2 / 3) !w.is$ else true) && (
          if (前边有连续两个大写字母(array, index)) !w.isA else true) && (
          if (到最后一位了前面还没有特殊字符(array, index)) w.is$ else true)
        ) seq ::= w
      }
      seq
    }

    private def 前边有连续两个大写字母(array: Array[Char], index: Int): Boolean = (index >= 2
      && array(index - 1) >= 'A' && array(index - 1) <= 'Z'
      && array(index - 2) >= 'A' && array(index - 2) <= 'Z')

    private def 到最后一位了前面还没有特殊字符(array: Array[Char], index: Int): Boolean =
      index == fixedSize - 1 && ! {
        val $rul = wordRules.filter(_.is$).head
        (0 until index).exists(i => $rul.array.contains(array(i)))
      }

    private def reset(): Unit = {
      wordRules.foreach(_.reset())
    }
  }

  /**
   * @param weight 设置各种字符之间的比例。
   * @param array
   * @param $      判断标点符号的字符。
   */
  class WordRule(val weight: Int, val array: Array[Char], $: Char = '$') {
    @volatile var remaining = weight

    def this(maxSize: Int, words: (Char, Char)*) = this(maxSize, (new ListBuffer[Char] /: words) { (b, w) =>
      b ++= (w._1 to w._2)
    }.toArray)

    lazy val isA = array.contains('A')
    lazy val is$ = array.contains($)

    def hasNext = remaining > 0

    def next(): Char = {
      require(remaining > 0)
      array((Math.random() * array.length).toInt)
    }

    def nextDone(): Unit = {
      remaining -= 1
    }

    def reset(): Unit = {
      remaining = weight
    }
  }

  /** 判断是否是中文 */
  def existZH(bytes: Array[Byte]) = pattern.matcher(new String(bytes)).find

  lazy val pattern = Pattern.compile("[\\u4e00-\\u9fa5]")
}
