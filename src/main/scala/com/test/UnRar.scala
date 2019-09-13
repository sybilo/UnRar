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

import de.innosystec.unrar.Archive
import de.innosystec.unrar.exception.RarException
import hobby.chenai.nakam.lang.J2S._
import hobby.wei.c.reflow._
import hobby.wei.c.reflow.implicits._
import hobby.wei.c.reflow.Feedback.Progress.Policy
import java.io.{File, FileOutputStream, FileWriter}
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern
import scala.collection.mutable.ListBuffer

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 04/09/2019
 */
object UnRar extends /*App with*/ Src {
  Reflow.setDebugMode(false)
  //  Reflow.setConfig(Config(5, 12))

  lazy val reflow = Reflow.create(trait4GenPwd).next(trait4TryUnRar).submit(pwd_final)
  lazy val reflow01 = reflow.fork()
  lazy val reflow02 = reflow.fork()
  lazy val reflow03 = reflow.fork()
  lazy val reflow04 = reflow.fork()
  lazy val reflow05 = reflow.fork()
  lazy val reflow06 = reflow.fork()
  lazy val reflow07 = reflow.fork()
  lazy val reflow08 = reflow.fork()
  lazy val reflow09 = reflow.fork()
  lazy val reflow10 = reflow.fork()
  //  lazy val pulse01: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse02: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse03: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse04: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse05: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse06: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse07: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse08: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse09: Pulse = reflow.fork.pulse(null, pulseFeedback)
  //  lazy val pulse10: Pulse = reflow.fork.pulse(null, pulseFeedback)

  @volatile var needStop = false
  val debug = false

  while (!needStop) {
    //    pulse01.input(words_reg -> wordsReg01 + (pulse_id -> "pulse01"))
    //    pulse02.input(words_reg -> wordsReg02 + (pulse_id -> "pulse02"))
    //    pulse03.input(words_reg -> wordsReg03 + (pulse_id -> "pulse03"))
    //    pulse04.input(words_reg -> wordsReg04 + (pulse_id -> "pulse04"))
    //    pulse05.input(words_reg -> wordsReg05 + (pulse_id -> "pulse05"))
    //    pulse06.input(words_reg -> wordsReg06 + (pulse_id -> "pulse06"))
    //    pulse07.input(words_reg -> wordsReg07 + (pulse_id -> "pulse07"))
    //    pulse08.input(words_reg -> wordsReg08 + (pulse_id -> "pulse08"))
    //    pulse09.input(words_reg -> wordsReg09 + (pulse_id -> "pulse09"))
    //    pulse10.input(words_reg -> wordsReg10 + (pulse_id -> "pulse10"))

    Thread.sleep(if (Math.random() >= 0.5) 5000 else 10)

    reflow01.start(words_reg -> wordsReg01 + (pulse_id -> "pulse01"), feedback)
    //    reflow02.start(words_reg -> wordsReg02 + (pulse_id -> "pulse02"), feedback)
    //    reflow03.start(words_reg -> wordsReg03 + (pulse_id -> "pulse03"), feedback)
    //    reflow04.start(words_reg -> wordsReg04 + (pulse_id -> "pulse04"), feedback)
    //    reflow05.start(words_reg -> wordsReg05 + (pulse_id -> "pulse05"), feedback)
    //    reflow06.start(words_reg -> wordsReg06 + (pulse_id -> "pulse06"), feedback)
    //    reflow07.start(words_reg -> wordsReg07 + (pulse_id -> "pulse07"), feedback)
    //    reflow08.start(words_reg -> wordsReg08 + (pulse_id -> "pulse08"), feedback)
    //    reflow09.start(words_reg -> wordsReg09 + (pulse_id -> "pulse09"), feedback)
    //    reflow10.start(words_reg -> wordsReg10 + (pulse_id -> "pulse10"), feedback)
  }

  def abort() = {
    //    pulse01.abort()
    //    pulse02.abort()
    //    pulse03.abort()
    //    pulse04.abort()
    //    pulse05.abort()
    //    pulse06.abort()
    //    pulse07.abort()
    //    pulse08.abort()
    //    pulse09.abort()
    //    pulse10.abort()
  }

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
  lazy val wordsReg01 = new WordsReg(13,
    new WordRule(3, ('A', 'Z')),
    new WordRule(15, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg02 = new WordsReg(13,
    new WordRule(3, ('A', 'Z')),
    new WordRule(15, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg03 = new WordsReg(13,
    new WordRule(3, ('A', 'Z')),
    new WordRule(15, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg04 = new WordsReg(13,
    new WordRule(3, ('A', 'Z')),
    new WordRule(15, ('0', '9'), ('a', 'z')),
    new WordRule(1, "!#$%&*-_".toCharArray))
  lazy val wordsReg05 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(15, ('0', '9'), ('a', 'z')),
    new WordRule(2, "!#$%&*-_".toCharArray))
  // 以下考虑没有特殊字符的情况
  lazy val wordsReg06 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(12, ('0', '9'), ('a', 'z')))
  lazy val wordsReg07 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(12, ('0', '9'), ('a', 'z')))
  lazy val wordsReg08 = new WordsReg(13,
    new WordRule(4, ('A', 'Z')),
    new WordRule(12, ('0', '9'), ('a', 'z')))
  lazy val wordsReg09 = new WordsReg(13,
    new WordRule(3, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(1, "_".toCharArray))
  lazy val wordsReg10 = new WordsReg(13,
    new WordRule(3, ('A', 'Z')),
    new WordRule(10, ('0', '9'), ('a', 'z')),
    new WordRule(2, "!#$%&*-_".toCharArray))

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
        val rem = remainingWordRules(n == 0)
        val rule = rem((Math.random() * rem.length).toInt)
        array(n) = rule.next()
      }
      reset()
      array.mkString("")
    }

    private def remainingWordRules(begin: Boolean): Seq[WordRule] = {
      var seq: List[WordRule] = Nil
      wordRules.foreach { w =>
        if (w.hasNext && (if (begin) !w.is$ else true)) seq ::= w
      }
      seq
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

    lazy val is$ = array.contains($)

    def hasNext = remaining > 0

    def next(): Char = {
      require(remaining > 0)
      remaining -= 1
      array((Math.random() * array.length).toInt)
    }

    def reset(): Unit = {
      remaining = weight
    }
  }

  lazy val trait4GenPwd = Trait("gen pwd", SHORT, pwd_maybe, words_reg) { ctx =>
    ctx.output(pwd_maybe, ctx.input(words_reg).get.next())
  }

  lazy val trait4TryUnRar = Trait("try unrar 4 test pwd", TRANSIENT, pwd_final, pwd_maybe + pulse_id) { ctx =>
    val password = ctx.input(pwd_maybe).get
    println(s"------------------------> pwd_maybe: $password, pulse_id: ${ctx.input(pulse_id).get} <----------------------------")

    if ( {
      val archive = new Archive(src, password, false)
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
          println("RarException: " + e.getMessage)
          false
        case e: Throwable =>
          e.printStackTrace()
          false
      } finally {
        try archive.close() catch {
          case _: Throwable =>
        }
      }
    }) {
      // done.
      needStop = true

      val archive = new Archive(src, password, false)
      val fh = archive.nextFileHeader
      var fhd = fh
      var fout: FileOutputStream = null
      try {
        while (fhd.nonNull) {
          val fileName = if (existZH(fh.getFileNameByteArray)) fh.getFileNameW else fh.getFileNameString
          println("fileName: " + fileName)
          fout = new FileOutputStream(dst.getPath / fileName)
          archive.extractFile(fh, fout)
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
      println("----------------------------- DONE.-------------------------------------")
      // 终止任务，输出密码。
      ctx.output(pwd_final, password)
      // 将密码输出到文件。
      val writer = new FileWriter(dst.getPath / pwd_final.key + ".txt")
      try {
        writer.append(password)
        writer.append("\n")
        writer.flush()
        writer.close()
      } catch {
        case _: Throwable =>
      }
    } else {
      // 返回去尝试新的密码
      // 这里什么也不用做，因为一直有新密码输入，知道终止任务。
      ctx.output(pwd_final, "no pwd")
    }
  }

  /** 判断是否是中文 */
  def existZH(bytes: Array[Byte]) = pattern.matcher(new String(bytes)).find

  lazy val pattern = Pattern.compile("[\\u4e00-\\u9fa5]")


  lazy val alloc_pwd_ns = new Kce[java.lang.Long]("alloc_pwd_ns") {}
  lazy val pulse_id = new Kce[String]("pulse_id") {}
  lazy val words_reg = new Kce[WordsReg]("words_reg") {}
  lazy val pwd_maybe = new Kce[String]("pwd_maybe") {}
  lazy val pwd_final = new Kce[String]("pwd_final") {}

  implicit lazy val poster: Poster = new Poster {
    override def post(runner: Runnable): Unit = runner.run()
  }

  implicit lazy val policy: Policy = Policy.FullDose

  lazy val feedback = new Feedback.Adapter {
    override def onPending(): Unit = println("onPending")

    override def onComplete(out: Out): Unit = {
      if (needStop) abort()
      println(" onComplete: " + out(pwd_final))
    }

    override def onFailed(trat: Trait, e: Exception): Unit = {
      needStop = true
      abort()
      println("onFailed:")
      if (e.getCause.nonNull) e.getCause.printStackTrace()
      else e.printStackTrace()
    }
  }

  lazy val pulseFeedback = new Pulse.Feedback.Adapter {
    val n = new AtomicLong(0)

    override def onPending(serialNum: Long): Unit = if (debug) println("onPending: " + n.addAndGet(serialNum))

    override def onComplete(serialNum: Long, out: Out): Unit = {
      if (needStop) abort()
      println(" onComplete: " + out(pwd_final))
    }

    override def onFailed(serialNum: Long, trat: Trait, e: Exception): Unit = {
      needStop = true
      abort()
      println("onFailed:")
      if (e.getCause.nonNull) e.getCause.printStackTrace()
      else e.printStackTrace()
    }
  }
}
