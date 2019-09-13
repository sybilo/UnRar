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

import java.io.File

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 04/09/2019
 */
trait Src4Test {
  val path = "/home/weichou/git/sybilo/UnRarTest/libs/test/rar/test2.rar"
  val password = "1234"
  lazy val src = new File(path)
  println("path: " + path)
  lazy val dst = {
    val i = path.lastIndexOf('.')
    val p = if (i >= src.getParent.length + 2) path.substring(0, i) else path + ".dir"
    val dir = new File(p)
    if (!dir.exists) dir.mkdirs
    dir
  }
  println("dst: " + dst)
}
