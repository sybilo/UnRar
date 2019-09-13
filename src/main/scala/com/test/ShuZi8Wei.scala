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

import com.test.implicts.{CryptoBytes, CryptoString}
import hobby.chenai.nakam.lang.J2S._

import scala.util.control.Breaks._

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 08/09/2019
 */
object ShuZi8Wei /*extends App*/ {
  lazy val path = "/home/weichou/test/8位纯数字.txt"
  lazy val BOM = "efbbbf".decodeHex
  lazy val FFFE = "fffe".decodeHex
  lazy val FEFF = "feff".decodeHex
  lazy val A0 = "0a".decodeHex

  lazy val HASH_CONTAINS = "1.rar:$RAR3$*1*42153bfd38455446*18f4dd1f*176*171*1*7581534c7c04fb4503d26e9e749ac3a2b0bc964a3b31f78e63e7f8aa8886583d791e2a049b1f0c8c9f94b7260b17afe5a8fafe8e64420b7839289e8803278b039e4a13a7b11a7e4556970e050d9ad975873ea313dcfbbfc94744a873d54e025a0b4a40e31ad559a4e50e2c2ce7672bd6141cd3f1b3b7f42f1bb13c11fee4dd532cd47982cf98477ec9f3032782ff7e5f68f903fd32cf8e3f67a40aa1cee2d8de3f6d0e0963f33f11d039815c482f1175*33:1::8位纯数字.txt"

  println(BOM)
  println(FFFE)
  println(FEFF)
  println(A0)

  println(
    String.format("%08d", Integer.valueOf(666)).getBytes().toHex
  )

  @volatile var needStop = false
  lazy val executor = Worker.sThreadPoolExecutor

  executor.submit({
    exec(b => b)
    }.run$)
  executor.submit({
    exec(b => b ++ A0)
    }.run$)
  executor.submit({
    exec(b => BOM ++ b)
    }.run$)
  executor.submit({
    exec(b => BOM ++ b ++ A0)
    }.run$)
  executor.submit({
    exec(b => FFFE ++ b)
    }.run$)
  executor.submit({
    exec(b => FFFE ++ b ++ A0)
    }.run$)
  executor.submit({
    exec(b => FEFF ++ b)
    }.run$)
  executor.submit({
    exec(b => FEFF ++ b ++ A0)
    }.run$)

  //  val file1 = s8bytes
  //  val file2 = s8bytes ++ A0
  //  val file3 = BOM ++ s8bytes
  //  val file4 = BOM ++ s8bytes ++ A0
  //  val file5 = FFFE ++ s8bytes
  //  val file6 = FFFE ++ s8bytes ++ A0
  //  val file7 = FEFF ++ s8bytes
  //  val file8 = FEFF ++ s8bytes ++ A0

  // TODO: 有几种文件格式；
  //  1.纯净的；
  //  2.末尾以 0a 结束的；
  //  3.以 BOM 开头的（win系统）；
  //  （以上是 utf-8）
  //  4.FFFE 其它编码；
  //  5.有无回车换行。

  lazy val runnable1: Runnable = {
    exec(b => b)
    }.run$

  def exec(f: Array[Byte] => Array[Byte]): Unit = {
    breakable {
      for (n <- 0 to 99999999) { // TODO: 错了，for循环不应该写在这。
        if (needStop) break
        println(n)
        val s8bytes = String.format("%08d", Integer.valueOf(n)).getBytes()
        val file1 = f(s8bytes)
        if (HASH_CONTAINS.contains(file1.toHash256.toHex)) {
          println(s"[${new String(file1)}]-[${file1.toHex}]")
          needStop = true
        }
      }
    }
  }


  //  lazy val writer = new FileWriter(path, false)

  //  try {
  //    println(writer.getEncoding)
  //    writer.write(s8)
  //    writer.flush()


  //    val bytes = byteArrOut.toByteArray
  //    val hex = bytes.toHex
  //    println(s"[$hex]")
  //    println(s"${bytes.take(4).toHex}, ${hex.take(11)}")
  //    println(hex.slice(11, 14))
  //    println(bytes.map(_.toBinaryString).mkString("[", " ", "]"))
  //  } catch {
  //    case t: Throwable => t.printStackTrace()
  //  } finally {
  //    writer.close()
  //    byteArrOut.reset()
  //  }

  //  lazy val reader = new FileInputStream(path)
  //  lazy val bytes = new Array[Byte](20)
  //
  //  reader.read(bytes)
  //  reader.close()
  //
  //  val hex = bytes.toHex
  //  println(s"[$hex]")
  //  println(s"${bytes.take(4).toHex}, ${hex.take(11)}")
  //  println(bytes.map(_.toHexString).mkString("[", " ", "]"))
}
