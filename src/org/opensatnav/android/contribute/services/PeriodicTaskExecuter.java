/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opensatnav.android.contribute.services;

import org.opensatnav.android.OpenSatNavConstants;

import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class will periodically announce the user's trip statitics.
 *
 * @author Sandor Dornbush
 */
public class PeriodicTaskExecuter {

  private final TrackRecordingService service;

  /**
   * A timer to schedule the announcements.
   */
  private Timer timer = new Timer();

  private final PeriodicTask task;

  public PeriodicTaskExecuter(PeriodicTask task,
                              TrackRecordingService service) {
    this.task = task;
    this.service = service;
  }

  /**
   * Schedules the task at the given interval.
   *
   * @param interval The interval in milliseconds
   */
  public void scheduleTask(long interval) {
    timer.cancel();
    timer.purge();
    timer = new Timer();
    if (interval <= 0) {
      return;
    }

    long now = System.currentTimeMillis();
    long next = service.getTripStatistics().getStartTime();
    while (next < now) next += interval;

    Date start = new Date(next);
    Log.i(OpenSatNavConstants.LOG_TAG,
          "StatusAnnouncer scheduled to start at " + start + " every "
          + interval + " milliseconds.");
    timer.scheduleAtFixedRate(new PeriodicTimerTask(), start, interval);
  }

  /**
   * Cleans up this object.
   */
  public void shutdown() {
    timer.cancel();
    timer.purge();
    timer = null;
    task.shutdown();
  }

  /**
   * The timer task to announce the trip status.
   */
  private class PeriodicTimerTask extends TimerTask {
    @Override
    public void run() {
      task.run(service);
    }
  }
}
