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
import uk.gov.hmrc.platformstatusconsulbackend.models.{GcPoolInfo, GcPoolUsage, GcPoolUsageThreshold}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.lang.management.{ManagementFactory, MemoryUsage}
import javax.inject.{Inject, Singleton}
import scala.collection.SortedMap
import scala.jdk.CollectionConverters._

@Singleton
class PrintVmDiagnosticController @Inject()(
  cc: ControllerComponents
) extends BackendController(cc):

  def printVmOptions(): Action[AnyContent] =
    Action:
      val hsdiag    = ManagementFactory.getPlatformMXBean(classOf[com.sun.management.HotSpotDiagnosticMXBean])
      val vmOptions = hsdiag.getDiagnosticOptions.asScala.toSeq
      Ok(toJson(SortedMap(vmOptions.map(e => e.getName -> e.getValue): _*)))

  def getMemoryPoolInfo(): Action[AnyContent] =
    Action:
      def toGcPoolUsage(usage: MemoryUsage) =
        Option(usage)
          .map: u =>
            GcPoolUsage(
              init      = u.getInit,
              used      = u.getUsed,
              committed = u.getCommitted,
              max       = u.getMax
            )

      Ok(toJson(
        ManagementFactory.getMemoryPoolMXBeans.asScala
          .map: e =>
            e.getName ->
              GcPoolInfo(
                `type`                  = e.getType.name,
                memoryManagerNames      = e.getMemoryManagerNames.toSeq,
                valid                   = e.isValid,
                usage                   = toGcPoolUsage(e.getUsage),
                peakUsage               = toGcPoolUsage(e.getPeakUsage),
                collectionUsage         = toGcPoolUsage(e.getCollectionUsage),
                usageThreshold          = if e.isUsageThresholdSupported
                                          then
                                            Some(GcPoolUsageThreshold(
                                              threshold = e.getUsageThreshold,
                                              count     = e.getUsageThresholdCount,
                                              exceeded  = e.isUsageThresholdExceeded
                                            ))
                                          else
                                            None,
                collectionUsageThreshold = if e.isCollectionUsageThresholdSupported then
                                            Some(GcPoolUsageThreshold(
                                              threshold = e.getCollectionUsageThreshold,
                                              count     = e.getCollectionUsageThresholdCount,
                                              exceeded  = e.isCollectionUsageThresholdExceeded
                                            ))
                                          else
                                            None
              )
      ))
