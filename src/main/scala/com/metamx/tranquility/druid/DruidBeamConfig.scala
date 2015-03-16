/*
 * Tranquility.
 * Copyright 2013, 2014, 2015  Metamarkets Group, Inc.
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
package com.metamx.tranquility.druid

import org.scala_tools.time.Imports._

case class DruidBeamConfig(
  firehoseGracePeriod: Period = 5.minutes,
  firehoseQuietPeriod: Period = 1.minute,
  firehoseRetryPeriod: Period = 1.minute,
  firehoseChunkSize: Int = 1000,
  randomizeTaskId: Boolean = false,
  indexRetryPeriod: Period = 1.minute
) extends IndexServiceConfig

object DruidBeamConfig
{
  /**
   * Builder for ClusteredBeamTuning objects.
   */
  def builder() = new Builder(DruidBeamConfig())

  class Builder private[tranquility](config: DruidBeamConfig)
  {
    /**
     * Druid indexing tasks will shut down this long after the windowPeriod has elapsed. The purpose of this extra delay
     * is to allow time to receive the last few events that are valid from our perspective. Otherwise, we could think
     * an event is just barely on-time, but the index task may not be available to receive it.
     */
    def firehoseGracePeriod(x: Period) = new Builder(config.copy(firehoseGracePeriod = x))

    /**
     * When we create new Druid indexing tasks, wait this long for the task to appear before complaining that it cannot
     * be found.
     */
    def firehoseQuietPeriod(x: Period) = new Builder(config.copy(firehoseQuietPeriod = x))

    /**
     * If a push to Druid fails for some apparently-transient reason, retry for this long before complaining that the
     * events could not be pushed.
     */
    def firehoseRetryPeriod(x: Period) = new Builder(config.copy(firehoseRetryPeriod = x))

    /**
     * Maximum number of events to send to Druid in one HTTP request. Larger batches will be broken up.
     */
    def firehoseChunkSize(x: Int) = new Builder(config.copy(firehoseChunkSize = x))

    /**
     * True if we should add a random suffix to Druid task IDs. This is useful for testing, since it allows us to
     * re-submit tasks that would otherwise conflict with each other. But for the same reason, it's risky in production,
     * since it allows us to re-submit tasks that conflict with each other.
     */
    def randomizeTaskId(x: Boolean) = new Builder(config.copy(randomizeTaskId = x))

    /**
     * If an indexing service overlord call fails for some apparently-transient reason, retry for this long before
     * giving up.
     */
    def indexRetryPeriod(x: Period) = new Builder(config.copy(indexRetryPeriod = x))

    def build(): DruidBeamConfig = config
  }

}
