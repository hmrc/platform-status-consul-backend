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

package uk.gov.hmrc.platformstatusconsulbackend.models

import play.api.libs.json.{Json, Format}

case class GcPoolUsage(
  init     : Long,
  used     : Long,
  committed: Long,
  max      : Long
)

object GcPoolUsage:
  given Format[GcPoolUsage] = Json.format[GcPoolUsage]


case class GcPoolUsageThreshold(
  threshold: Long,
  count    : Long,
  exceeded : Boolean
)

object GcPoolUsageThreshold:
  given Format[GcPoolUsageThreshold] = Json.format[GcPoolUsageThreshold]


case class GcPoolInfo(
  `type`                  : String,
  memoryManagerNames      : Seq[String],
  valid                   : Boolean,
  usage                   : Option[GcPoolUsage],
  peakUsage               : Option[GcPoolUsage],
  collectionUsage         : Option[GcPoolUsage],
  usageThreshold          : Option[GcPoolUsageThreshold],
  collectionUsageThreshold: Option[GcPoolUsageThreshold]
)

object GcPoolInfo:
  given Format[GcPoolInfo] = Json.format[GcPoolInfo]
