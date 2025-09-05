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

import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.platformstatusconsulbackend.models.GcInformation
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.lang.management.ManagementFactory
import javax.inject.Inject
import scala.jdk.CollectionConverters._

class GcInformationController @Inject()(
  cc: ControllerComponents
) extends BackendController(cc):

  def getGcInfo: Action[AnyContent] =
    Action:
      val gBeans    = ManagementFactory.getGarbageCollectorMXBeans.asScala
      val coreCount = Runtime.getRuntime.availableProcessors
      Ok(toJson(GcInformation(coreCount, gBeans)))
