/*
 * Copyright 2008 Google Inc.
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
package org.opensatnav.android.contribute.util;


import java.util.Date;
import java.util.Vector;

import org.opensatnav.android.OpenSatNavConstants;
import cl.droid.transantiago.R;
import org.opensatnav.android.contribute.content.DescriptionGenerator;
import org.opensatnav.android.contribute.content.Track;
import org.opensatnav.android.contribute.content.Waypoint;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Various string manipulation methods.
 *
 * @author Sandor Dornbush
 * @author Rodrigo Damazio
 */
public class StringUtils implements DescriptionGenerator {

  private final Context context;

  /**
   * Formats a number of milliseconds as a string.
   *
   * @param time - A period of time in milliseconds.
   * @return A string of the format M:SS, MM:SS or HH:MM:SS
   */
  public static String formatTime(long time) {
    return formatTimeInternal(time, false);
  }

  /**
   * Formats a number of milliseconds as a string. To be used when we need the
   * hours to be shown even when it is zero, e.g. exporting data to a
   * spreadsheet.
   *
   * @param time - A period of time in milliseconds
   * @return A string of the format HH:MM:SS even if time is less than 1 hour
   */
  public static String formatTimeAlwaysShowingHours(long time) {
    return formatTimeInternal(time, true);
  }

  /**
   * Formats the given text as a CDATA element to be used in a XML file. This
   * includes adding the starting and ending CDATA tags. Please notice that this
   * may result in multiple consecutive CDATA tags.
   *
   * @param unescaped the unescaped text to be formatted
   * @return the formatted text, inside one or more CDATA tags
   */
  public static String stringAsCData(String unescaped) {
    // "]]>" needs to be broken into multiple CDATA segments, like:
    // "Foo]]>Bar" becomes "<![CDATA[Foo]]]]><![CDATA[>Bar]]>"
    // (the end of the first CDATA has the "]]", the other has ">")
    String escaped = unescaped.replaceAll("]]>", "]]]]><![CDATA[>");
    return "<![CDATA[" + escaped + "]]>";
  }

  /**
   * Formats a number of milliseconds as a string.
   *
   * @param time - A period of time in milliseconds
   * @param alwaysShowHours - Whether to display 00 hours if time is less than 1
   *        hour
   * @return A string of the format HH:MM:SS
   */
  private static String formatTimeInternal(long time, boolean alwaysShowHours) {
    int[] parts = getTimeParts(time);
    StringBuilder builder = new StringBuilder();
    if (parts[2] > 0 || alwaysShowHours) {
      builder.append(parts[2]);
      builder.append(':');
      if (parts[1] <= 9) {
        builder.append("0");
      }
    }

    builder.append(parts[1]);
    builder.append(':');
    if (parts[0] <= 9) {
      builder.append("0");
    }
    builder.append(parts[0]);

    return builder.toString();
  }

  /**
   * Gets the time as an array of parts.
   */
  public static int[] getTimeParts(long time) {
    if (time < 0) {
      int[] parts = getTimeParts(time * -1);
      parts[0] *= -1;
      parts[1] *= -1;
      parts[2] *= -1;
      return parts;
    }
    int[] parts = new int[3];

    long seconds = time / 1000;
    parts[0] = (int) (seconds % 60);
    int tmp = (int) (seconds / 60);
    parts[1] = tmp % 60;
    parts[2] = tmp / 60;

    return parts;
  }

  public StringUtils(Context context) {
    this.context = context;
  }

  public String formatTimeLong(long time) {
    int[] parts = getTimeParts(time);
    String secLabel =
        context.getString(parts[0] == 1 ? R.string.second : R.string.seconds);
    String minLabel =
        context.getString(parts[1] == 1 ? R.string.minute : R.string.minutes);
    String hourLabel =
        context.getString(parts[2] == 1 ? R.string.hour : R.string.hours);

    StringBuilder sb = new StringBuilder();
    if (parts[2] != 0) {
      sb.append(parts[2]);
      sb.append(" ");
      sb.append(hourLabel);
      sb.append(" ");
      sb.append(parts[1]);
      sb.append(minLabel);
    } else {
      sb.append(parts[1]);
      sb.append(" ");
      sb.append(minLabel);
      sb.append(" ");
      sb.append(parts[0]);
      sb.append(secLabel);
    }
    return sb.toString();
  }

  /**
   * Generates a description for a track (with information about the
   * statistics).
   *
   * @param track the track
   * @return a track description
   */
  public String generateTrackDescription(Track track, Vector<Double> distances,
      Vector<Double> elevations) {
    boolean displaySpeed = true;
    SharedPreferences preferences =
        context.getSharedPreferences(OpenSatNavConstants.SETTINGS_NAME, 0);
    //TODO: Add this to settings too
    /*if (preferences != null) {
      displaySpeed =
          preferences.getBoolean(MyTracksSettings.REPORT_SPEED, true);
    }*/

    final double distanceInKm = track.getTotalDistance() / 1000;
    final double distanceInMiles = distanceInKm * UnitConversions.KM_TO_MI;
    final long minElevationInMeters = Math.round(track.getMinElevation());
    final long minElevationInFeet =
        Math.round(track.getMinElevation() * UnitConversions.M_TO_FT);
    final long maxElevationInMeters = Math.round(track.getMaxElevation());
    final long maxElevationInFeet =
        Math.round(track.getMaxElevation() * UnitConversions.M_TO_FT);
    final long elevationGainInMeters =
        Math.round(track.getTotalElevationGain());
    final long elevationGainInFeet =
        Math.round(track.getTotalElevationGain() * UnitConversions.M_TO_FT);

    long minGrade = 0;
    long maxGrade = 0;
    double trackMaxGrade = track.getMaxGrade();
    double trackMinGrade = track.getMinGrade();
    if (!Double.isNaN(trackMaxGrade)
        && !Double.isInfinite(trackMaxGrade)) {
      maxGrade = Math.round(trackMaxGrade * 100);
    }
    if (!Double.isNaN(trackMinGrade) && !Double.isInfinite(trackMinGrade)) {
      minGrade = Math.round(trackMinGrade * 100);
    }

    String category = context.getString(R.string.unknown);
    String trackCategory = track.getCategory();
    if (trackCategory != null && trackCategory.length() > 0) {
      category = trackCategory;
    }

    String averageSpeed =
        getSpeedString(track.getAverageSpeed(),
            R.string.average_speed_label,
            R.string.average_pace_label,
            displaySpeed);

    String averageMovingSpeed =
        getSpeedString(track.getAverageMovingSpeed(),
            R.string.average_moving_speed_label,
            R.string.average_moving_pace_label,
            displaySpeed);

    String maxSpeed =
        getSpeedString(track.getMaxSpeed(),
            R.string.max_speed_label,
            R.string.min_pace_label,
            displaySpeed);

    return String.format("%s<p>"
        + "%s: %.2f %s (%.1f %s)<br>"
        + "%s: %s<br>"
        + "%s: %s<br>"
        + "%s %s %s"
        + "%s: %d %s (%d %s)<br>"
        + "%s: %d %s (%d %s)<br>"
        + "%s: %d %s (%d %s)<br>"
        + "%s: %d %%<br>"
        + "%s: %d %%<br>"
        + "%s: %tc<br>"
        + "%s: %s<br>"
        + "<img border=\"0\" src=\"%s\"/>",

       

        // Line 2
        context.getString(R.string.total_distance_label),
        distanceInKm, context.getString(R.string.kilometer),
        distanceInMiles, context.getString(R.string.mile),

        // Line 3
        context.getString(R.string.total_time_label),
        StringUtils.formatTime(track.getTotalTime()),

        // Line 4
        context.getString(R.string.moving_time_label),
        StringUtils.formatTime(track.getMovingTime()),

        // Line 5
        averageSpeed, averageMovingSpeed, maxSpeed,

        // Line 6
        context.getString(R.string.min_elevation_label),
        minElevationInMeters, context.getString(R.string.meter),
        minElevationInFeet, context.getString(R.string.feet),

        // Line 7
        context.getString(R.string.max_elevation_label),
        maxElevationInMeters, context.getString(R.string.meter),
        maxElevationInFeet, context.getString(R.string.feet),

        // Line 8
        context.getString(R.string.elevation_gain_label),
        elevationGainInMeters, context.getString(R.string.meter),
        elevationGainInFeet, context.getString(R.string.feet),

        // Line 9
        context.getString(R.string.max_grade_label), maxGrade,

        // Line 10
        context.getString(R.string.min_grade_label), minGrade,

        // Line 11
        context.getString(R.string.recorded_date),
        new Date(track.getStartTime()));

  }

  private String getSpeedString(double speed, int speedLabel, int paceLabel,
      boolean displaySpeed) {
    double speedInKph = speed * 3.6;
    double speedInMph = speedInKph * UnitConversions.KMH_TO_MPH;
    if (displaySpeed) {
      return String.format("%s: %.2f %s (%.1f %s)<br>",
          context.getString(speedLabel),
          speedInKph, context.getString(R.string.kilometer_per_hour),
          speedInMph, context.getString(R.string.mile_per_hour));
    } else {
      double paceInKm;
      double paceInMi;
      if (speed == 0) {
        paceInKm = 0.0;
        paceInMi = 0.0;
      } else {
        paceInKm = 60.0 / speedInKph;
        paceInMi = 60.0 / speedInMph;
      }
      return String.format("%s: %.2f %s (%.1f %s)<br>",
          context.getString(paceLabel),
          paceInKm, context.getString(R.string.min_per_kilometer),
          paceInMi, context.getString(R.string.min_per_mile));
    }
  }

  /**
   * Generates a description for a waypoint (with information about the
   * statistics).
   *
   * @return a track description
   */
  public String generateWaypointDescription(Waypoint waypoint) {
    final double distanceInKm = waypoint.getTotalDistance() / 1000;
    final double distanceInMiles = distanceInKm * UnitConversions.KM_TO_MI;
    final double averageSpeedInKmh = waypoint.getAverageSpeed() * 3.6;
    final double averageSpeedInMph =
        averageSpeedInKmh * UnitConversions.KMH_TO_MPH;
    final double movingSpeedInKmh = waypoint.getAverageMovingSpeed() * 3.6;
    final double movingSpeedInMph =
        movingSpeedInKmh * UnitConversions.KMH_TO_MPH;
    final double maxSpeedInKmh = waypoint.getMaxSpeed() * 3.6;
    final double maxSpeedInMph = maxSpeedInKmh * UnitConversions.KMH_TO_MPH;
    final long minElevationInMeters = Math.round(waypoint.getMinElevation());
    final long minElevationInFeet =
        Math.round(waypoint.getMinElevation() * UnitConversions.M_TO_FT);
    final long maxElevationInMeters = Math.round(waypoint.getMaxElevation());
    final long maxElevationInFeet =
        Math.round(waypoint.getMaxElevation() * UnitConversions.M_TO_FT);
    final long elevationGainInMeters =
      Math.round(waypoint.getTotalElevationGain());
    final long elevationGainInFeet = Math.round(waypoint.getTotalElevationGain()
        * UnitConversions.M_TO_FT);
    long theMinGrade = 0;
    long theMaxGrade = 0;
    if (!Double.isNaN(waypoint.getMaxGrade()) &&
        !Double.isInfinite(waypoint.getMaxGrade())) {
      theMaxGrade = Math.round(waypoint.getMaxGrade() * 100);
    }
    if (!Double.isNaN(waypoint.getMinGrade()) &&
        !Double.isInfinite(waypoint.getMinGrade())) {
      theMinGrade = Math.round(waypoint.getMinGrade() * 100);
    }
    final String percent = "%";

    return String.format(
        "%s: %.2f %s (%.1f %s)\n"
        + "%s: %s\n"
        + "%s: %s\n"
        + "%s: %.2f %s (%.1f %s)\n"
        + "%s: %.2f %s (%.1f %s)\n"
        + "%s: %.2f %s (%.1f %s)\n"
        + "%s: %d %s (%d %s)\n"
        + "%s: %d %s (%d %s)\n"
        + "%s: %d %s (%d %s)\n"
        + "%s: %d %s\n"
        + "%s: %d %s\n",
        context.getString(R.string.distance_label),
            distanceInKm, context.getString(R.string.kilometer),
            distanceInMiles, context.getString(R.string.mile),
        context.getString(R.string.time_label),
            StringUtils.formatTime(waypoint.getTotalTime()),
        context.getString(R.string.moving_time_label),
            StringUtils.formatTime(waypoint.getMovingTime()),
        context.getString(R.string.average_speed_label),
            averageSpeedInKmh, context.getString(R.string.kilometer_per_hour),
            averageSpeedInMph, context.getString(R.string.mile_per_hour),
        context.getString(R.string.average_moving_speed_label),
            movingSpeedInKmh, context.getString(R.string.kilometer_per_hour),
            movingSpeedInMph, context.getString(R.string.mile_per_hour),
        context.getString(R.string.max_speed_label),
            maxSpeedInKmh, context.getString(R.string.kilometer_per_hour),
            maxSpeedInMph, context.getString(R.string.mile_per_hour),
        context.getString(R.string.min_elevation_label),
            minElevationInMeters, context.getString(R.string.meter),
            minElevationInFeet, context.getString(R.string.feet),
        context.getString(R.string.max_elevation_label),
            maxElevationInMeters, context.getString(R.string.meter),
            maxElevationInFeet, context.getString(R.string.feet),
        context.getString(R.string.elevation_gain_label),
            elevationGainInMeters, context.getString(R.string.meter),
            elevationGainInFeet, context.getString(R.string.feet),
        context.getString(R.string.max_grade_label),
            theMaxGrade, percent,
        context.getString(R.string.min_grade_label),
            theMinGrade, percent);
  }
}
