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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GcExperimentsSpec extends AnyWordSpec with Matchers:

  "GC Experiments" should:
    "run small-burst-heap-allocator" in:
      GcExperiments("small-burst-heap-allocator").iteration()

    "run large-burst-heap-allocator" in:
      GcExperiments("large-burst-heap-allocator").iteration()

    "run constant-heap-memory-occupancy" in:
      GcExperiments("constant-heap-memory-occupancy").iteration()

    "run small-heap-memory-bandwidth-allocator" in:
      GcExperiments("small-heap-memory-bandwidth-allocator").iteration()

    "run large-heap-memory-bandwidth-allocator" in:
      GcExperiments("large-heap-memory-bandwidth-allocator").iteration()

    "run small-read-barriers-looping" in:
      GcExperiments("small-read-barriers-looping").iteration()

    "run large-read-barriers-looping" in:
      GcExperiments("large-read-barriers-looping").iteration()
