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

import play.api.libs.concurrent.Futures
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.platformstatusconsulbackend.services.StatusChecker
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class MongoWriteController @Inject()(
  cc           : ControllerComponents,
  statusChecker: StatusChecker
)(using
  ExecutionContext,
  Futures
) extends BackendController(cc):

  def iteration3(): Action[AnyContent] =
    Action.async:
      for
        status <- statusChecker.iteration3Status()
      yield Ok(toJson(status))

  def iteration6(): Action[AnyContent] =
    Action.async:
      for
        status <- statusChecker.iteration6Status()
      yield Ok(toJson(status))
