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

trait GcExperiment:
  def iteration(): Any

object GcExperiments:

  private val experiments: Map[String, GcExperiment] =
    Map(
      "small-burst-heap-allocator"            -> burstHeapAllocator(4096, 1000),
      "large-burst-heap-allocator"            -> burstHeapAllocator(4096000, 10),
      "constant-heap-memory-occupancy"        -> heapMemoryBandwidthAllocator(8192000),
      "small-heap-memory-bandwidth-allocator" -> heapMemoryBandwidthAllocator(4096),
      "large-heap-memory-bandwidth-allocator" -> heapMemoryBandwidthAllocator(4096000),
      "small-read-barriers-looping"           -> readBarriersLooping(100),
      "large-read-barriers-looping"           -> readBarriersLooping(4000)
    )

  def apply(name: String): GcExperiment =
    experiments(name)

  private def burstHeapAllocator(sizeInBytes: Int, numberOfObjects: Int): GcExperiment =
    () => (1 to numberOfObjects).map(_ => new Array[Byte](sizeInBytes)).toList

  private def heapMemoryBandwidthAllocator(sizeInBytes: Int): GcExperiment =
    () => new Array[Byte](sizeInBytes)

  private def readBarriersLooping(numberOfObjects: Int): GcExperiment =
    new GcExperiment:
      private val ints = (1 to numberOfObjects).toList
      override def iteration(): Any =
        var x: Int = 0
        ints.foreach(x += _)
        x
