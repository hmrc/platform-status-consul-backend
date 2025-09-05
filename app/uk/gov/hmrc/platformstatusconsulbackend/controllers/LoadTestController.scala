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

package uk.gov.hmrc.platformstatusconsulbackend.controllers

import play.api.mvc.ControllerComponents
import uk.gov.hmrc.platformstatusconsulbackend.services.LoadGenerator
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.duration.DurationLong

class LoadTestController @Inject()(
  cc: ControllerComponents
) extends BackendController(cc):

  /* Maxes out cpu on specified number of threads for a fixed duration */
  def cpuMax(threads: Option[Int], seconds: Long) =
    Action:
      LoadGenerator.cpuLoad(threads.getOrElse(Runtime.getRuntime.availableProcessors()), seconds.seconds)
        .fold(
          _        => ServiceUnavailable("Another test in in progress"),
          accepted => Accepted(s"Load test submitted: ${accepted.id}")
        )

  /* times how long it takes to complete a number of cpu bound tasks split evenly over a specified number of threads */
  def cpuTasks(threads: Option[Int], count: Int) =
    Action:
      LoadGenerator.cpuTasks(threads.getOrElse(Runtime.getRuntime.availableProcessors()), count)
        .fold(
          _        => ServiceUnavailable("Another test in in progress"),
          accepted => Accepted(s"Load test submitted: ${accepted.id}")
        )

  def gc(test: String, threads: Option[Int], count: Int) =
    Action:
      LoadGenerator.gc(test, threads.getOrElse(Runtime.getRuntime.availableProcessors()), count)
        .fold(
          _        => ServiceUnavailable("Another test in in progress"),
          accepted => Accepted(s"Load test submitted: ${accepted.id}")
        )
