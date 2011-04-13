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
package org.opensatnav.android.contribute.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class providing easy access to locations and tracks in the
 * MyTracksProvider. All static members.
 *
 * @author Leif Hendrik Wilden
 */
public class ProviderUtils implements IProviderUtils {

  private final Context context;

  public ProviderUtils(Context context) {
    this.context = context;
  }

  /**
   * Creates the ContentValues for a given location object.
   *
   * @param location a given location
   * @param trackId the id of the track it belongs to
   * @return a filled in ContentValues object
   */
  private static ContentValues createContentValues(
      Location location, long trackId) {
    ContentValues values = new ContentValues();
    values.put(TrackPointsColumns.TRACKID, trackId);
    values.put(TrackPointsColumns.LATITUDE,
        (int) (location.getLatitude() * 1E6));
    values.put(TrackPointsColumns.LONGITUDE,
        (int) (location.getLongitude() * 1E6));
    // This is an ugly hack for Samsung phones that don't properly populate the
    // time field.
    values.put(TrackPointsColumns.TIME,
        (location.getTime() == 0)
            ? System.currentTimeMillis()
            : location.getTime());
    if (location.hasAltitude()) {
      values.put(TrackPointsColumns.ALTITUDE, location.getAltitude());
    }
    if (location.hasBearing()) {
      values.put(TrackPointsColumns.BEARING, location.getBearing());
    }
    if (location.hasAccuracy()) {
      values.put(TrackPointsColumns.ACCURACY, location.getAccuracy());
    }
    if (location.hasSpeed()) {
      values.put(TrackPointsColumns.SPEED, location.getSpeed());
    }
    return values;
  }

  /**
   * Creates the ContentValues for a given Track object.
   *
   * Note: If the track has an id<0 the id column will not be filled.
   *
   * @param track a given track object
   * @return a filled in ContentValues object
   */
  private static ContentValues createContentValues(Track track) {
    ContentValues values = new ContentValues();
    // Values id < 0 indicate no id is available:
    if (track.getId() >= 0) {
      values.put(BaseColumns._ID, track.getId());
    }
    values.put(TracksColumns.NAME, track.getName());
    values.put(TracksColumns.DESCRIPTION, track.getDescription());
    values.put(TracksColumns.MAPID, track.getMapId());
    values.put(TracksColumns.CATEGORY, track.getCategory());
    values.put(TracksColumns.NUMPOINTS, track.getNumberOfPoints());
    values.put(TracksColumns.STARTID, track.getStartId());
    values.put(TracksColumns.STARTTIME, track.getStartTime());
    values.put(TracksColumns.STOPTIME, track.getStopTime());
    values.put(TracksColumns.STOPID, track.getStopId());
    values.put(TracksColumns.TOTALDISTANCE, track.getTotalDistance());
    values.put(TracksColumns.TOTALTIME, track.getTotalTime());
    values.put(TracksColumns.MOVINGTIME, track.getMovingTime());
    values.put(TracksColumns.MAXLAT, track.getTop());
    values.put(TracksColumns.MINLAT, track.getBottom());
    values.put(TracksColumns.MAXLON, track.getRight());
    values.put(TracksColumns.MINLON, track.getLeft());
    values.put(TracksColumns.AVGSPEED, track.getAverageSpeed());
    values.put(TracksColumns.AVGMOVINGSPEED, track.getAverageMovingSpeed());
    values.put(TracksColumns.MAXSPEED, track.getMaxSpeed());
    values.put(TracksColumns.MINELEVATION, track.getMinElevation());
    values.put(TracksColumns.MAXELEVATION, track.getMaxElevation());
    values.put(TracksColumns.ELEVATIONGAIN, track.getTotalElevationGain());
    values.put(TracksColumns.MINGRADE, track.getMinGrade());
    values.put(TracksColumns.MAXGRADE, track.getMaxGrade());
    return values;
  }

  private static ContentValues createContentValues(Waypoint waypoint) {
    ContentValues values = new ContentValues();
    // Values id < 0 indicate no id is available:
    if (waypoint.getId() >= 0) {
      values.put(BaseColumns._ID, waypoint.getId());
    }
    values.put(WaypointsColumns.NAME, waypoint.getName());
    values.put(WaypointsColumns.DESCRIPTION, waypoint.getDescription());
    values.put(WaypointsColumns.CATEGORY, waypoint.getCategory());
    values.put(WaypointsColumns.ICON, waypoint.getIcon());
    values.put(WaypointsColumns.TRACKID, waypoint.getTrackId());
    values.put(WaypointsColumns.TYPE, waypoint.getType());
    values.put(WaypointsColumns.LENGTH, waypoint.getLength());
    values.put(WaypointsColumns.DURATION, waypoint.getDuration());
    values.put(WaypointsColumns.STARTTIME, waypoint.getStartTime());
    values.put(WaypointsColumns.STARTID, waypoint.getStartId());
    values.put(WaypointsColumns.STOPID, waypoint.getStopId());

    values.put(WaypointsColumns.TOTALDISTANCE, waypoint.getTotalDistance());
    values.put(WaypointsColumns.TOTALTIME, waypoint.getTotalTime());
    values.put(WaypointsColumns.MOVINGTIME, waypoint.getMovingTime());
    values.put(WaypointsColumns.AVGSPEED, waypoint.getAverageSpeed());
    values.put(WaypointsColumns.AVGMOVINGSPEED,
        waypoint.getAverageMovingSpeed());
    values.put(WaypointsColumns.MAXSPEED, waypoint.getMaxSpeed());
    values.put(WaypointsColumns.MINELEVATION, waypoint.getMinElevation());
    values.put(WaypointsColumns.MAXELEVATION, waypoint.getMaxElevation());
    values.put(WaypointsColumns.ELEVATIONGAIN,
        waypoint.getTotalElevationGain());
    values.put(WaypointsColumns.MINGRADE, waypoint.getMinGrade());
    values.put(WaypointsColumns.MAXGRADE, waypoint.getMaxGrade());

    Location location = waypoint.getLocation();
    if (location != null) {
      values.put(WaypointsColumns.LATITUDE,
          (int) (location.getLatitude() * 1E6));
      values.put(WaypointsColumns.LONGITUDE,
          (int) (location.getLongitude() * 1E6));
      values.put(WaypointsColumns.TIME, location.getTime());
      if (location.hasAltitude()) {
        values.put(WaypointsColumns.ALTITUDE, location.getAltitude());
      }
      if (location.hasBearing()) {
        values.put(WaypointsColumns.BEARING, location.getBearing());
      }
      if (location.hasAccuracy()) {
        values.put(WaypointsColumns.ACCURACY, location.getAccuracy());
      }
      if (location.hasSpeed()) {
        values.put(WaypointsColumns.SPEED, location.getSpeed());
      }
    }

    return values;
  }

  @Override
  public Location createLocation(Cursor cursor) {
    int idxLatitude = cursor.getColumnIndexOrThrow(TrackPointsColumns.LATITUDE);
    int idxLongitude =
        cursor.getColumnIndexOrThrow(TrackPointsColumns.LONGITUDE);
    int idxAltitude = cursor.getColumnIndexOrThrow(TrackPointsColumns.ALTITUDE);
    int idxTime = cursor.getColumnIndexOrThrow(TrackPointsColumns.TIME);
    int idxBearing = cursor.getColumnIndexOrThrow(TrackPointsColumns.BEARING);
    int idxAccuracy = cursor.getColumnIndexOrThrow(TrackPointsColumns.ACCURACY);
    int idxSpeed = cursor.getColumnIndexOrThrow(TrackPointsColumns.SPEED);

    Location location = new Location("");
    if (!cursor.isNull(idxLatitude)) {
      location.setLatitude(1. * cursor.getInt(idxLatitude) / 1E6);
    }
    if (!cursor.isNull(idxLongitude)) {
      location.setLongitude(1. * cursor.getInt(idxLongitude) / 1E6);
    }
    if (!cursor.isNull(idxAltitude)) {
      location.setAltitude(cursor.getFloat(idxAltitude));
    }
    if (!cursor.isNull(idxTime)) {
      location.setTime(cursor.getLong(idxTime));
    }
    if (!cursor.isNull(idxBearing)) {
      location.setBearing(cursor.getFloat(idxBearing));
    }
    if (!cursor.isNull(idxSpeed)) {
      location.setSpeed(cursor.getFloat(idxSpeed));
    }
    if (!cursor.isNull(idxAccuracy)) {
      location.setAccuracy(cursor.getFloat(idxAccuracy));
    }
    return location;
  }

  /**
   * Creates a Track object from a given cursor.
   *
   * @param cursor a cursor pointing at a db or provider with tracks
   * @return a new Track object
   */
  private static Track createTrack(Cursor cursor) {
    int idxId = cursor.getColumnIndexOrThrow(BaseColumns._ID);
    int idxName = cursor.getColumnIndexOrThrow(TracksColumns.NAME);
    int idxDescription =
        cursor.getColumnIndexOrThrow(TracksColumns.DESCRIPTION);
    int idxMapId = cursor.getColumnIndexOrThrow(TracksColumns.MAPID);
    int idxCategory = cursor.getColumnIndexOrThrow(TracksColumns.CATEGORY);
    int idxStartId = cursor.getColumnIndexOrThrow(TracksColumns.STARTID);
    int idxStartTime = cursor.getColumnIndexOrThrow(TracksColumns.STARTTIME);
    int idxStopTime = cursor.getColumnIndexOrThrow(TracksColumns.STOPTIME);
    int idxStopId = cursor.getColumnIndexOrThrow(TracksColumns.STOPID);
    int idxNumPoints = cursor.getColumnIndexOrThrow(TracksColumns.NUMPOINTS);
    int idxMaxlat = cursor.getColumnIndexOrThrow(TracksColumns.MAXLAT);
    int idxMinlat = cursor.getColumnIndexOrThrow(TracksColumns.MINLAT);
    int idxMaxlon = cursor.getColumnIndexOrThrow(TracksColumns.MAXLON);
    int idxMinlon = cursor.getColumnIndexOrThrow(TracksColumns.MINLON);

    int idxTotalDistance =
        cursor.getColumnIndexOrThrow(TracksColumns.TOTALDISTANCE);
    int idxTotalTime = cursor.getColumnIndexOrThrow(TracksColumns.TOTALTIME);
    int idxMovingTime = cursor.getColumnIndexOrThrow(TracksColumns.MOVINGTIME);
    int idxAverageSpeed = cursor.getColumnIndexOrThrow(TracksColumns.AVGSPEED);
    int idxAverageMovingSpeed =
        cursor.getColumnIndexOrThrow(TracksColumns.AVGMOVINGSPEED);
    int idxMaxSpeed = cursor.getColumnIndexOrThrow(TracksColumns.MAXSPEED);
    int idxMinElevation =
        cursor.getColumnIndexOrThrow(TracksColumns.MINELEVATION);
    int idxMaxElevation =
        cursor.getColumnIndexOrThrow(TracksColumns.MAXELEVATION);
    int idxElevationGain =
        cursor.getColumnIndexOrThrow(TracksColumns.ELEVATIONGAIN);
    int idxMinGrade = cursor.getColumnIndexOrThrow(TracksColumns.MINGRADE);
    int idxMaxGrade = cursor.getColumnIndexOrThrow(TracksColumns.MAXGRADE);

    Track track = new Track();
    if (!cursor.isNull(idxId)) {
      track.setId(cursor.getLong(idxId));
    }
    if (!cursor.isNull(idxName)) {
      track.setName(cursor.getString(idxName));
    }
    if (!cursor.isNull(idxDescription)) {
      track.setDescription(cursor.getString(idxDescription));
    }
    if (!cursor.isNull(idxMapId)) {
      track.setMapId(cursor.getString(idxMapId));
    }
    if (!cursor.isNull(idxCategory)) {
      track.setCategory(cursor.getString(idxCategory));
    }
    if (!cursor.isNull(idxStartId)) {
      track.setStartId(cursor.getInt(idxStartId));
    }
    if (!cursor.isNull(idxStartTime)) {
      track.setStartTime(cursor.getLong(idxStartTime));
    }
    if (!cursor.isNull(idxStopTime)) {
      track.setStopTime(cursor.getLong(idxStopTime));
    }
    if (!cursor.isNull(idxStopId)) {
      track.setStopId(cursor.getInt(idxStopId));
    }
    if (!cursor.isNull(idxNumPoints)) {
      track.setNumberOfPoints(cursor.getInt(idxNumPoints));
    }
    if (!cursor.isNull(idxTotalDistance)) {
      track.setTotalDistance(cursor.getFloat(idxTotalDistance));
    }
    if (!cursor.isNull(idxTotalTime)) {
      track.setTotalTime(cursor.getLong(idxTotalTime));
    }
    if (!cursor.isNull(idxMovingTime)) {
      track.setMovingTime(cursor.getLong(idxMovingTime));
    }
    if (!cursor.isNull(idxMaxlat)
        && !cursor.isNull(idxMinlat)
        && !cursor.isNull(idxMaxlon)
        && !cursor.isNull(idxMinlon)) {
      int top = cursor.getInt(idxMaxlat);
      int bottom = cursor.getInt(idxMinlat);
      int right = cursor.getInt(idxMaxlon);
      int left = cursor.getInt(idxMinlon);
      track.setBounds(left, top, right, bottom);
    }
    if (!cursor.isNull(idxAverageSpeed)) {
      track.setAverageSpeed(cursor.getFloat(idxAverageSpeed));
    }
    if (!cursor.isNull(idxAverageMovingSpeed)) {
      track.setAverageMovingSpeed(cursor.getFloat(idxAverageMovingSpeed));
    }
    if (!cursor.isNull(idxMaxSpeed)) {
      track.setMaxSpeed(cursor.getFloat(idxMaxSpeed));
    }
    if (!cursor.isNull(idxMinElevation)) {
      track.setMinElevation(cursor.getFloat(idxMinElevation));
    }
    if (!cursor.isNull(idxMaxElevation)) {
      track.setMaxElevation(cursor.getFloat(idxMaxElevation));
    }
    if (!cursor.isNull(idxElevationGain)) {
      track.setTotalElevationGain(cursor.getFloat(idxElevationGain));
    }
    if (!cursor.isNull(idxMinGrade)) {
      track.setMinGrade(cursor.getFloat(idxMinGrade));
    }
    if (!cursor.isNull(idxMaxGrade)) {
      track.setMaxGrade(cursor.getFloat(idxMaxGrade));
    }
    return track;
  }

  @Override
  public Waypoint createWaypoint(Cursor cursor) {
    int idxId = cursor.getColumnIndexOrThrow(BaseColumns._ID);
    int idxName = cursor.getColumnIndexOrThrow(WaypointsColumns.NAME);
    int idxDescription =
        cursor.getColumnIndexOrThrow(WaypointsColumns.DESCRIPTION);
    int idxCategory = cursor.getColumnIndexOrThrow(WaypointsColumns.CATEGORY);
    int idxIcon = cursor.getColumnIndexOrThrow(WaypointsColumns.ICON);
    int idxTrackId = cursor.getColumnIndexOrThrow(WaypointsColumns.TRACKID);
    int idxType = cursor.getColumnIndexOrThrow(WaypointsColumns.TYPE);
    int idxLength = cursor.getColumnIndexOrThrow(WaypointsColumns.LENGTH);
    int idxDuration = cursor.getColumnIndexOrThrow(WaypointsColumns.DURATION);
    int idxStartTime = cursor.getColumnIndexOrThrow(WaypointsColumns.STARTTIME);
    int idxStartId = cursor.getColumnIndexOrThrow(WaypointsColumns.STARTID);
    int idxStopId = cursor.getColumnIndexOrThrow(WaypointsColumns.STOPID);

    int idxTotalDistance =
        cursor.getColumnIndexOrThrow(WaypointsColumns.TOTALDISTANCE);
    int idxTotalTime = cursor.getColumnIndexOrThrow(WaypointsColumns.TOTALTIME);
    int idxMovingTime =
        cursor.getColumnIndexOrThrow(WaypointsColumns.MOVINGTIME);
    int idxAverageSpeed =
        cursor.getColumnIndexOrThrow(WaypointsColumns.AVGSPEED);
    int idxAverageMovingSpeed =
        cursor.getColumnIndexOrThrow(WaypointsColumns.AVGMOVINGSPEED);
    int idxMaxSpeed = cursor.getColumnIndexOrThrow(WaypointsColumns.MAXSPEED);
    int idxMinElevation =
        cursor.getColumnIndexOrThrow(WaypointsColumns.MINELEVATION);
    int idxMaxElevation =
        cursor.getColumnIndexOrThrow(WaypointsColumns.MAXELEVATION);
    int idxElevationGain =
        cursor.getColumnIndexOrThrow(WaypointsColumns.ELEVATIONGAIN);
    int idxMinGrade = cursor.getColumnIndexOrThrow(WaypointsColumns.MINGRADE);
    int idxMaxGrade = cursor.getColumnIndexOrThrow(WaypointsColumns.MAXGRADE);

    int idxLatitude = cursor.getColumnIndexOrThrow(WaypointsColumns.LATITUDE);
    int idxLongitude = cursor.getColumnIndexOrThrow(WaypointsColumns.LONGITUDE);
    int idxAltitude = cursor.getColumnIndexOrThrow(WaypointsColumns.ALTITUDE);
    int idxTime = cursor.getColumnIndexOrThrow(WaypointsColumns.TIME);
    int idxBearing = cursor.getColumnIndexOrThrow(WaypointsColumns.BEARING);
    int idxAccuracy = cursor.getColumnIndexOrThrow(WaypointsColumns.ACCURACY);
    int idxSpeed = cursor.getColumnIndexOrThrow(WaypointsColumns.SPEED);

    Waypoint waypoint = new Waypoint();
    if (!cursor.isNull(idxId)) {
      waypoint.setId(cursor.getLong(idxId));
    }
    if (!cursor.isNull(idxName)) {
      waypoint.setName(cursor.getString(idxName));
    }
    if (!cursor.isNull(idxDescription)) {
      waypoint.setDescription(cursor.getString(idxDescription));
    }
    if (!cursor.isNull(idxCategory)) {
      waypoint.setCategory(cursor.getString(idxCategory));
    }
    if (!cursor.isNull(idxIcon)) {
      waypoint.setIcon(cursor.getString(idxIcon));
    }
    if (!cursor.isNull(idxTrackId)) {
      waypoint.setTrackId(cursor.getLong(idxTrackId));
    }
    if (!cursor.isNull(idxType)) {
      waypoint.setType(cursor.getInt(idxType));
    }
    if (!cursor.isNull(idxLength)) {
      waypoint.setLength(cursor.getDouble(idxLength));
    }
    if (!cursor.isNull(idxDuration)) {
      waypoint.setDuration(cursor.getLong(idxDuration));
    }
    if (!cursor.isNull(idxStartTime)) {
      waypoint.setStartTime(cursor.getLong(idxStartTime));
    }
    if (!cursor.isNull(idxStartId)) {
      waypoint.setStartId(cursor.getLong(idxStartId));
    }
    if (!cursor.isNull(idxStopId)) {
      waypoint.setStopId(cursor.getLong(idxStopId));
    }
    if (!cursor.isNull(idxTotalDistance)) {
      waypoint.setTotalDistance(cursor.getFloat(idxTotalDistance));
    }
    if (!cursor.isNull(idxTotalTime)) {
      waypoint.setTotalTime(cursor.getLong(idxTotalTime));
    }
    if (!cursor.isNull(idxMovingTime)) {
      waypoint.setMovingTime(cursor.getLong(idxMovingTime));
    }
    if (!cursor.isNull(idxAverageSpeed)) {
      waypoint.setAverageSpeed(cursor.getFloat(idxAverageSpeed));
    }
    if (!cursor.isNull(idxAverageMovingSpeed)) {
      waypoint.setAverageMovingSpeed(cursor.getFloat(idxAverageMovingSpeed));
    }
    if (!cursor.isNull(idxMaxSpeed)) {
      waypoint.setMaxSpeed(cursor.getFloat(idxMaxSpeed));
    }
    if (!cursor.isNull(idxMinElevation)) {
      waypoint.setMinElevation(cursor.getFloat(idxMinElevation));
    }
    if (!cursor.isNull(idxMaxElevation)) {
      waypoint.setMaxElevation(cursor.getFloat(idxMaxElevation));
    }
    if (!cursor.isNull(idxElevationGain)) {
      waypoint.setTotalElevationGain(cursor.getFloat(idxElevationGain));
    }
    if (!cursor.isNull(idxMinGrade)) {
      waypoint.setMinGrade(cursor.getFloat(idxMinGrade));
    }
    if (!cursor.isNull(idxMaxGrade)) {
      waypoint.setMaxGrade(cursor.getFloat(idxMaxGrade));
    }

    Location location = new Location("");
    if (!cursor.isNull(idxLatitude) && !cursor.isNull(idxLongitude)) {
      location.setLatitude(1. * cursor.getInt(idxLatitude) / 1E6);
      location.setLongitude(1. * cursor.getInt(idxLongitude) / 1E6);
    }
    if (!cursor.isNull(idxAltitude)) {
      location.setAltitude(cursor.getFloat(idxAltitude));
    }
    if (!cursor.isNull(idxTime)) {
      location.setTime(cursor.getLong(idxTime));
    }
    if (!cursor.isNull(idxBearing)) {
      location.setBearing(cursor.getFloat(idxBearing));
    }
    if (!cursor.isNull(idxSpeed)) {
      location.setSpeed(cursor.getFloat(idxSpeed));
    }
    if (!cursor.isNull(idxAccuracy)) {
      location.setAccuracy(cursor.getFloat(idxAccuracy));
    }
    waypoint.setLocation(location);
    return waypoint;
  }

  @Override
  public void deleteAllTracks() {
    context.getContentResolver().delete(TracksColumns.CONTENT_URI, null, null);
    context.getContentResolver().delete(TrackPointsColumns.CONTENT_URI,
        null, null);
    context.getContentResolver().delete(
        WaypointsColumns.CONTENT_URI, null, null);
  }

  @Override
  public void deleteTrack(long trackId) {
    Track track = getTrack(trackId);
    if (track != null) {
      context.getContentResolver().delete(TrackPointsColumns.CONTENT_URI,
          "_id>=" + track.getStartId() + " AND _id<=" + track.getStopId(),
          null);
    }
    context.getContentResolver().delete(WaypointsColumns.CONTENT_URI,
        WaypointsColumns.TRACKID + "=" + trackId, null);
    context.getContentResolver().delete(
        TracksColumns.CONTENT_URI, "_id=" + trackId, null);
  }

  @Override
  public void deleteWaypoint(long waypointId,
      DescriptionGenerator descriptionGenerator) {
    final Waypoint deletedWaypoint = getWaypoint(waypointId);
    if (deletedWaypoint != null
        && deletedWaypoint.getType() == Waypoint.TYPE_STATISTICS) {
      final Waypoint nextWaypoint =
          getNextStatisticsWaypointAfter(deletedWaypoint);
      if (nextWaypoint != null) {
        Log.d(OSNProvider.TAG, "Correcting marker " + nextWaypoint.getId()
            + " after deleted marker " + deletedWaypoint.getId());
        nextWaypoint.combine(deletedWaypoint);
        nextWaypoint.setDescription(
            descriptionGenerator.generateWaypointDescription(nextWaypoint));
        if (!updateWaypoint(nextWaypoint)) {
          Log.w(OSNProvider.TAG, "Update of marker was unsuccessful.");
        }
      } else {
        Log.d(OSNProvider.TAG,
            "No statistics marker after the deleted one was found.");
      }
    }
    context.getContentResolver().delete(
        WaypointsColumns.CONTENT_URI, "_id=" + waypointId, null);
  }

  @Override
  public Waypoint getNextStatisticsWaypointAfter(Waypoint waypoint) {
    final String selection = BaseColumns._ID + ">" + waypoint.getId()
        + " AND " + WaypointsColumns.TRACKID + "=" + waypoint.getTrackId()
        + " AND " + WaypointsColumns.TYPE + "=" + Waypoint.TYPE_STATISTICS;
    final String sortOrder = BaseColumns._ID + " LIMIT 1";
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(
          WaypointsColumns.CONTENT_URI,
          null /*projection*/,
          selection,
          null /*selectionArgs*/,
          sortOrder);
      if (cursor != null && cursor.moveToFirst()) {
        return createWaypoint(cursor);
      }
    } catch (RuntimeException e) {
      Log.w(OSNProvider.TAG, "Caught unexpected exception.", e);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public boolean updateWaypoint(Waypoint waypoint) {
    try {
      final int rows = context.getContentResolver().update(
          WaypointsColumns.CONTENT_URI,
          createContentValues(waypoint),
          "_id=" + waypoint.getId(),
          null /*selectionArgs*/);
      return rows == 1;
    } catch (RuntimeException e) {
      Log.e(OSNProvider.TAG, "Caught unexpected exception.", e);
    }
    return false;
  }

  /**
   * Finds a locations from the provider by the given selection.
   *
   * @param select a selection argument that identifies a unique location
   * @return the fist location matching, or null if not found
   */
  private Location findLocationBy(String select) {
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(
          TrackPointsColumns.CONTENT_URI, null, select, null, null);
      if (cursor != null && cursor.moveToNext()) {
        return createLocation(cursor);
      }
    } catch (RuntimeException e) {
      Log.w(OSNProvider.TAG, "Caught an unexpeceted exception.", e);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  /**
   * Finds a track from the provider by the given selection.
   *
   * @param select a selection argument that identifies a unique track
   * @return the first track matching, or null if not found
   */
  private Track findTrackBy(String select) {
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(
          TracksColumns.CONTENT_URI, null, select, null, null);
      if (cursor != null && cursor.moveToNext()) {
        return createTrack(cursor);
      }
    } catch (RuntimeException e) {
      Log.w(OSNProvider.TAG, "Caught unexpected exception.", e);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public Location getLastLocation() {
    return findLocationBy("_id=(select max(_id) from trackpoints)");
  }

  @Override
  public Waypoint getFirstWaypoint(long trackId) {
    Cursor cursor = context.getContentResolver().query(
        WaypointsColumns.CONTENT_URI,
        null /*projection*/,
        "trackid=" + trackId,
        null /*selectionArgs*/,
        "_id LIMIT 1");
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          return createWaypoint(cursor);
        }
      } catch (RuntimeException e) {
        Log.w(OSNProvider.TAG, "Caught an unexpected exception.", e);
      } finally {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public Waypoint getWaypoint(long waypointId) {
    Cursor cursor = context.getContentResolver().query(
        WaypointsColumns.CONTENT_URI,
        null /*projection*/,
        "_id=" + waypointId,
        null /*selectionArgs*/,
        null /*sortOrder*/);
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          return createWaypoint(cursor);
        }
      } catch (RuntimeException e) {
        Log.w(OSNProvider.TAG, "Caught an unexpected exception.", e);
      } finally {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public long getLastLocationId(long trackId) {
    final String[] projection = {"_id"};
    Cursor cursor = context.getContentResolver().query(
        TrackPointsColumns.CONTENT_URI,
        projection,
        "_id=(select max(_id) from trackpoints WHERE trackid=" + trackId + ")",
        null /*selectionArgs*/,
        null /*sortOrder*/);
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          return cursor.getLong(
              cursor.getColumnIndexOrThrow(BaseColumns._ID));
        }
      } catch (RuntimeException e) {
        Log.w(OSNProvider.TAG, "Caught an unexpected exception.", e);
      } finally {
        cursor.close();
      }
    }
    return -1;
  }

  @Override
  public long getFirstWaypointId(long trackId) {
    final String[] projection = {"_id"};
    Cursor cursor = context.getContentResolver().query(
        WaypointsColumns.CONTENT_URI,
        projection,
        "trackid=" + trackId,
        null /*selectionArgs*/,
        "_id LIMIT 1" /*sortOrder*/);
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          return cursor.getLong(
              cursor.getColumnIndexOrThrow(BaseColumns._ID));
        }
      } catch (RuntimeException e) {
        Log.w(OSNProvider.TAG, "Caught an unexpected exception.", e);
      } finally {
        cursor.close();
      }
    }
    return -1;
  }

  @Override
  public long getLastWaypointId(long trackId) {
    final String[] projection = {"_id"};
    Cursor cursor = context.getContentResolver().query(
        WaypointsColumns.CONTENT_URI,
        projection,
        WaypointsColumns.TRACKID + "=" + trackId,
        null /*selectionArgs*/,
        "_id DESC LIMIT 1" /*sortOrder*/);
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          return cursor.getLong(
              cursor.getColumnIndexOrThrow(BaseColumns._ID));
        }
      } catch (RuntimeException e) {
        Log.w(OSNProvider.TAG, "Caught an unexpected exception.", e);
      } finally {
        cursor.close();
      }
    }
    return -1;
  }

  @Override
  public Track getLastTrack() {
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(
          TracksColumns.CONTENT_URI, null, "_id=(select max(_id) from tracks)",
          null, null);
      if (cursor != null && cursor.moveToNext()) {
        return createTrack(cursor);
      }
    } catch (RuntimeException e) {
      Log.w(OSNProvider.TAG, "Caught an unexpected exception.", e);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public long getLastTrackId() {
    String[] proj = { BaseColumns._ID };
    Cursor cursor = context.getContentResolver().query(
        TracksColumns.CONTENT_URI, proj, "_id=(select max(_id) from tracks)",
        null, null);
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          return cursor.getLong(
              cursor.getColumnIndexOrThrow(BaseColumns._ID));
        }
      } finally {
        cursor.close();
      }
    }
    return -1;
  }

  @Override
  public Location getLocation(long id) {
    String selection = BaseColumns._ID + "=" + id;
    return findLocationBy(selection);
  }

  @Override
  public Cursor getLocationsCursor(long trackId, long minTrackPointId,
      int maxLocations, boolean descending) {
    String selection; 
    if (minTrackPointId > 0) {
      selection = String.format("%s=%d AND %s>=%d",
          TrackPointsColumns.TRACKID, trackId,
          BaseColumns._ID, minTrackPointId);
    } else {
      selection = String.format("%s=%d",
          TrackPointsColumns.TRACKID, trackId);
    }

    String sortOrder = "_id " + (descending ? "DESC" : "ASC");
    if (maxLocations > 0) {
      sortOrder += " LIMIT " + maxLocations;
    }

    return context.getContentResolver().query(
        TrackPointsColumns.CONTENT_URI, null, selection, null, sortOrder);
  }

  @Override
  public Cursor getWaypointsCursor(long trackId, long minWaypointId,
      long maxWaypoints) {
    String selection;
    if (minWaypointId > 0) {
      selection = String.format("%s=%d AND %s>=%d",
          WaypointsColumns.TRACKID, trackId,
          BaseColumns._ID, minWaypointId);
    } else {
      selection = String.format("%s=%d",
          WaypointsColumns.TRACKID, trackId);
    }

    String sortOrder = "_id ASC";
    if (maxWaypoints > 0) {
      sortOrder += " LIMIT " + maxWaypoints;
    }

    return context.getContentResolver().query(
        WaypointsColumns.CONTENT_URI, null, selection, null, sortOrder);
  }

  @Override
  public Track getTrack(long id) {
    String select = BaseColumns._ID + "=" + id;
    return findTrackBy(select);
  }

  @Override
  public long getTrackPoints(Track track, int maxPoints) {
    long lastId = -1;
    Cursor cursor = getLocationsCursor(track.getId(), -1, maxPoints, true);
    if (cursor == null) {
      Log.w(OSNProvider.TAG, "Cannot get a locations cursor!");
      return lastId;
    }
    try {
      final int idColumnIdx =
          cursor.getColumnIndexOrThrow(BaseColumns._ID);
      if (cursor.moveToLast()) {
        do {
          Location location = createLocation(cursor);
          if (location == null) {
            continue;
          }
          track.addLocation(location);
          lastId = cursor.getLong(idColumnIdx);
        } while (cursor.moveToPrevious());
      }
    } catch (RuntimeException e) {
      Log.w(OSNProvider.TAG, "Caught unexpected exception.", e);
    } finally {
      cursor.close();
    }
    return lastId;
  }

  @Override
  public void getTrackPoints(Track track, TrackBuffer buffer) {
    long startingPoint = buffer.getLastLocationRead() == 0 ? track.getStartId()
        : buffer.getLastLocationRead();
    buffer.reset();
    Cursor cursor = getLocationsCursor(track.getId(),
                                       startingPoint,
                                       buffer.getSize(), false);
    final int idColumnIdx =
        cursor.getColumnIndexOrThrow(BaseColumns._ID);
    if (cursor == null) {
      Log.w(OSNProvider.TAG, "Cannot get a locations cursor!");
      buffer.setInvalid();
      return;
    }
    try {
      if (cursor.getCount() == 0) {
        Log.w(OSNProvider.TAG, "No matching locations found.");
        buffer.resetAt(startingPoint + buffer.getSize());
        return;
      }

      if (!cursor.moveToFirst()) {
        Log.w(OSNProvider.TAG, "Could not move to first.");
        buffer.setInvalid();
        return;
      }

      while (cursor.moveToNext()) {
        Location location = createLocation(cursor);
        if (location == null) {
          continue;
        }
        buffer.add(location, cursor.getLong(idColumnIdx));
      }
      if (buffer.getLocationsLoaded() == 0) {
        Log.w(OSNProvider.TAG, "No locations read.");
        buffer.resetAt(startingPoint + buffer.getSize());
      }
    } catch (RuntimeException e) {
      Log.w(OSNProvider.TAG, "Caught unexpected exception.", e);
    } finally {
      cursor.close();
    }
  }

  @Override
  public Cursor getTracksCursor(String selection) {
    Cursor cursor = context.getContentResolver().query(
        TracksColumns.CONTENT_URI, null, selection, null, "_id");
    return cursor;
  }

  @Override
  public Uri insertTrack(Track track) {
    Log.d(OSNProvider.TAG, "MyTracksProviderUtilsImpl.insertTrack");
    return context.getContentResolver().insert(TracksColumns.CONTENT_URI,
        createContentValues(track));
  }

  @Override
  public Uri insertTrackPoint(Location location, long trackId) {
    Log.d(OSNProvider.TAG, "MyTracksProviderUtilsImpl.insertTrackPoint");
    return context.getContentResolver().insert(TrackPointsColumns.CONTENT_URI,
        createContentValues(location, trackId));
  }

  @Override
  public Uri insertWaypoint(Waypoint waypoint) {
    Log.d(OSNProvider.TAG, "MyTracksProviderUtilsImpl.insertWaypoint");
    waypoint.setId(-1);
    return context.getContentResolver().insert(WaypointsColumns.CONTENT_URI,
        createContentValues(waypoint));
  }

  @Override
  public Uri insertTrackAndTrackPoints(Track track) {
    // Insert the track right away to have its ID
    Uri trackUri = insertTrack(track);
    long trackId = Long.parseLong(trackUri.getLastPathSegment());
    track.setId(trackId);

    // Insert all the points (associated with the track ID)
    boolean firstPoint = true;
    long pointId = -1;
    for (Location location : track.getLocations()) {
      Uri pointUri = insertTrackPoint(location, trackId);
      pointId = Long.parseLong(pointUri.getLastPathSegment());
      if (firstPoint) {
        track.setStartId(pointId);
        firstPoint = false;
      }
    }
    track.setStopId(pointId);

    // Update the track with the start and end point IDs
    updateTrack(track);
    return trackUri;
  }

  @Override
  public boolean trackExists(long id) {
    Cursor cursor = null;
    try {
      final String[] projection = { BaseColumns._ID };
      cursor = context.getContentResolver().query(
          TracksColumns.CONTENT_URI,
          projection,
          BaseColumns._ID + "=" + id/*selection*/,
          null/*selectionArgs*/,
          null/*sortOrder*/);
      if (cursor != null && cursor.moveToNext()) {
        return true;
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return false;
  }

  @Override
  public void updateTrack(Track track) {
    Log.d(OSNProvider.TAG, "MyTracksProviderUtilsImpl.updateTrack");
    context.getContentResolver().update(TracksColumns.CONTENT_URI,
        createContentValues(track), "_id=" + track.getId(), null);
  }
}
