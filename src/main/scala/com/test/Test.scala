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

import java.io.{File, FileNotFoundException, FileOutputStream}
import java.util.regex.Pattern
import de.innosystec.unrar.Archive
import hobby.chenai.nakam.lang.J2S._

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 09/09/2019
 */
object Test extends App with Src4Test {
  println()
  val archive = new Archive(src, password, false)
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
      val boo = try {
        fout = new FileOutputStream(path)
        true
      } catch {
        case _: FileNotFoundException => false
      }
      if (boo) {
        archive.extractFile(fhd, fout)
        try fout.close() catch {
          case _: Throwable =>
        }
      }
      fhd = archive.nextFileHeader
    }
    println(s"--------------------- $password ---------------------------")
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

  /** 判断是否是中文 */
  def existZH(bytes: Array[Byte]) = pattern.matcher(new String(bytes)).find

  lazy val pattern = Pattern.compile("[\\u4e00-\\u9fa5]")
}
