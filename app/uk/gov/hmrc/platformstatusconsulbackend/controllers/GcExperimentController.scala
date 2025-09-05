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

import play.api.Logger
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.platformstatusconsulbackend.services.GcExperiments
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.UUID
import javax.inject.Inject

class GcExperimentController @Inject()(
  cc: ControllerComponents
) extends BackendController(cc):
  private val logger = Logger(this.getClass)

  def experiment(test: String, count: Int): Action[AnyContent] =
    Action:
      val id = UUID.randomUUID().toString
      logger.info(s"Starting $test test $id")
      val start = System.currentTimeMillis()
      (1 to count)
        .foreach(_ => GcExperiments(test).iteration())
      val finish = System.currentTimeMillis()
      val duration = finish - start
      logger.info(s"Finished $test test $id in $duration ms")
      Ok(s"${duration}")
