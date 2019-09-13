/*
 * Copyright (C) 2017-present, Wei Chou(weichou2010@gmail.com)
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

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
  * 优化的线程池实现。
  *
  * @author Wei Chou(weichou2010@gmail.com)
  * @version 1.0, 11/02/2017
  */
object Worker {
  private implicit lazy val lock: ReentrantLock = new ReentrantLock()

  private final val sThreadFactory = new ThreadFactory() {
    private val mIndex = new AtomicInteger(0)

    def newThread(runnable: Runnable): Thread = {
      val thread = new Thread(runnable, "pool-thread-" + Worker.getClass.getName + "#" + mIndex.getAndIncrement())
      resetThread(thread)
      thread
    }
  }

  private def resetThread(thread: Thread) {
    Thread.interrupted()
    if (thread.isDaemon) thread.setDaemon(false)
  }

  lazy val sPoolWorkQueue: BlockingQueue[Runnable] = new LinkedBlockingQueue[Runnable](2048) {
    override def offer(r: Runnable) = {
      /* 如果不放入队列并返回false，会迫使增加线程。但是这样又会导致总是增加线程，而空闲线程得不到重用。
      因此在有空闲线程的情况下就直接放入队列。若大量长任务致使线程数增加到上限，
      则threadPool启动reject流程(见ThreadPoolExecutor构造器的最后一个参数)，此时再插入到本队列。
      这样即完美实现[先增加线程数到最大，再入队列，空闲释放线程]这个基本逻辑。*/
      val b = sThreadPoolExecutor.getActiveCount < sThreadPoolExecutor.getPoolSize && super.offer(r)
      b
    }
  }

  lazy val sThreadPoolExecutor: ThreadPoolExecutor = new ThreadPoolExecutor(8, 12,
    10, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new RejectedExecutionHandler {
      override def rejectedExecution(r: Runnable, executor: ThreadPoolExecutor): Unit = {
        try {
          sPoolWorkQueue.offer(r, 1, TimeUnit.DAYS)
        } catch {
          case ignore: InterruptedException => throw ignore
        }
      }
    })
}
