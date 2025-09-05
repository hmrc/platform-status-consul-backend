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
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.nio.charset.StandardCharsets
import javax.inject.{Inject, Singleton}

@Singleton()
class MeasureController @Inject()(
  cc: ControllerComponents
) extends BackendController(cc):

  private val logger: Logger = Logger(this.getClass)

  def measureRequest(): Action[AnyContent] =
    Action: request =>
      val fromHost      = request.headers.get(USER_AGENT).getOrElse("?")
      val remoteAddress = request.headers.get("Remote-Address").getOrElse("?")
      val requestID     = request.headers.get("X-Request-ID").getOrElse("?")
      val contentLength = request.headers.get(CONTENT_LENGTH).map(_.toInt).getOrElse(-1)

      val testHeader =
        for
          name       <- request.headers.get("X-Test-Header-Name")
          testHeader <- request.headers.get(name)
          byteSize   =  testHeader.getBytes(StandardCharsets.UTF_8).length
        yield s"test header: '$name' had length: $byteSize"

      val msg =
        s"""Reply from platform-status-consul-backend:
        |
        |received request from: $fromHost
        |remoteAddress: $remoteAddress
        |requestID: $requestID
        |body length: $contentLength
        |"""
        .stripMargin
          + testHeader.getOrElse("")

      logger.info(msg)
      Ok(msg)
