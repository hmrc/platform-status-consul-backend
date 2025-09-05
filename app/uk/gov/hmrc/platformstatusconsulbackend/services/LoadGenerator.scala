/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.platformstatusconsulbackend.services

import play.api.Logger

import java.util.UUID
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor}
import javax.inject.Singleton
import scala.concurrent.duration.{Duration, SECONDS}
import scala.util.Random

case class TestAccepted(id: UUID)
case class TestInProgress()

@Singleton()
object LoadGenerator:
  private val logger = Logger(this.getClass)

  private val threadPool = ThreadPoolExecutor(1, 16, 0L, SECONDS, LinkedBlockingQueue[Runnable]());

  sys.addShutdownHook:
    threadPool.shutdownNow()

  def cpuLoad(threadCount: Int, duration: Duration): Either[TestInProgress, TestAccepted] =
    runTest(threadCount): (testId: TestAccepted, threadId: Int) =>
      () =>
        logger.info(s"Test ${testId.id} Thread $threadId - maxing cpu for ${duration.toSeconds} seconds")
        val endAt = System.currentTimeMillis() + duration.toMillis
        while (System.currentTimeMillis() < endAt) {}
        logger.info(s"Test ${testId.id} Thread $threadId - completed maxing cpu")


  def cpuTasks(threadCount: Int, tasks: Int): Either[TestInProgress, TestAccepted] =
    runTest(threadCount): (test: TestAccepted, threadId: Int) =>
      () =>
        val start          = System.currentTimeMillis()
        var max            = 0
        val tasksPerThread = tasks / threadCount

        logger.info(s"Test ${test.id} Thread $threadId - processing $tasksPerThread tasks")

        Range(0, tasksPerThread)
          .foreach: _ =>
            val data = Iterator.continually(Random.nextInt()).take(4096).toList
            val res  = data.foldLeft(0): (acc, i) =>
                        acc + data.foldLeft(0)((acc2, i2) => acc2 + i2 * i)
            max = Math.max(res, max) // might not be needed, assignment here was to ensure the compiler doesn't optimize away the line above

        logger.info(s"Test ${test.id} Thread $threadId - complete $tasksPerThread tasks in ${System.currentTimeMillis() - start} ms")


  def gc(name: String, threadCount: Int, tasks: Int) : Either[TestInProgress, TestAccepted] =
    logger.debug(s"Forcing GC prior to GC Experiment $name")
    System.gc()

    runTest(threadCount): (test: TestAccepted, threadId: Int) =>
      () =>
        logger.info(s"Test ${test.id} Thread $threadId - starting GC Experiment $name")

        val tasksPerThread = tasks / threadCount
        val start = System.currentTimeMillis()

        try
          (1 to tasks)
            .foreach: _ =>
              val experiment = GcExperiments(name)
              experiment.iteration()
        catch
          case t: Throwable =>
            logger.error(s"Test ${test.id} Thread $threadId - aborted GC Experiment $name", t)

        val finish = System.currentTimeMillis()

        logger.info(s"Test ${test.id} Thread $threadId - completed GC Experiment $name ($tasksPerThread tasks) in ${finish - start} ms")


  private def runTest(threadCount: Int)(f: (TestAccepted, Int) => Runnable): Either[TestInProgress, TestAccepted] =
    synchronized:
      if threadPool.getActiveCount > 0
      then
        // since we're cpu bound only allow one at a time
        Left(TestInProgress())
      else
        threadPool.setCorePoolSize(threadCount)
        threadPool.setMaximumPoolSize(threadCount)
        val test = TestAccepted(UUID.randomUUID())
        Range(0, threadCount).foreach(i => threadPool.submit(f(test, i)))
        Right(test)
