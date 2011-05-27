package org.opensatnav.android.services;

import org.andnav.osm.util.constants.OpenStreetMapConstants;
import org.opensatnav.android.OpenSatNavConstants;
import cl.droid.transantiago.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationHandler implements OpenSatNavConstants, OpenStreetMapConstants {

	protected LocationListenerAdaptor mGpsLocationListener;
	protected LocationListenerAdaptor mNetworkLocationListener;

	/** true if Gps Location is activated, false otherwise */
	protected boolean gpsLocationActivated = false;
	/** true if Network Location is activated, false otherwise */
	protected boolean networkLocationActivated = false;
	protected String lastLocation = "";

	protected LocationManager mLocationManager;
	protected LocationListener mLocationReceiver;
	protected Context mContext;

	protected Location firstLocation;
	protected Location mostRecentLocation;
	protected int mNumSatellites = NOT_SET;

	public LocationHandler(LocationManager lm, LocationListener dest, Context ctx) {
		if (OpenSatNavConstants.DEBUGMODE)
			Log.v(OpenSatNavConstants.LOG_TAG, "LocationHandler construtor.  Context: " + ctx.getClass());
		mLocationManager = lm;
		mLocationReceiver = dest;
		mContext = ctx;
	}

	public Location getFirstLocation() {
		return firstLocation;
	}
	
	public Location getMostRecentLocation() {
		if (mostRecentLocation == null)
		{
			return getFirstLocation();
		} else {
			return mostRecentLocation;
		}
		
	}

	public synchronized void start() throws NoLocationProvidersException {
		if (OpenSatNavConstants.DEBUGMODE)
			Log.v(OpenSatNavConstants.LOG_TAG, "LocationHandler start()");
		// initialize state of location providers and launch location listeners
		if (!networkLocationActivated &&
				mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			networkLocationActivated = true;
			mNetworkLocationListener = new LocationListenerAdaptor();
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					this.mNetworkLocationListener);
		}
		if (!gpsLocationActivated &&
				mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsLocationActivated = true;
			mGpsLocationListener = new LocationListenerAdaptor();
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0,
					this.mGpsLocationListener);
		}
		// get the best location using bestProvider()
		try {
			firstLocation = mLocationManager.getLastKnownLocation(
					bestProvider());
		} catch (Exception e) {
			Log.e(OpenSatNavConstants.LOG_TAG, "Error getting the first location");
		}

		// test to see which location services are available
		if (!gpsLocationActivated) {
			if (!networkLocationActivated) {
				throw new NoLocationProvidersException();
			} else {
				// we have network location but no GPS, tell the user that
				// accuracy is bad because of this
				Toast.makeText(mContext, R.string.gps_disabled, Toast.LENGTH_LONG)
						.show();
			}
		} else if (!networkLocationActivated) {
			// we have GPS (but no network), this tells the user
			// that they might have to wait for a fix
			Toast.makeText(mContext, R.string.getting_gps_fix, Toast.LENGTH_LONG)
					.show();
		}
	}
	
	public synchronized void stop() {
		if (OpenSatNavConstants.DEBUGMODE)
			Log.v(OpenSatNavConstants.LOG_TAG, "LocationHandler Stop");
		try {
			mLocationManager.removeUpdates(mGpsLocationListener);
			networkLocationActivated = false;
			mGpsLocationListener = null;
		} catch (IllegalArgumentException e) {
			Log.d(OpenSatNavConstants.LOG_TAG, "Ignoring: " + e);
			// there's no gps location listener to disable
		}
		try {
			mLocationManager.removeUpdates(mNetworkLocationListener);
			gpsLocationActivated = false;
			mNetworkLocationListener = null;
		} catch (IllegalArgumentException e) {
			Log.v(OpenSatNavConstants.LOG_TAG, "Ignoring: " + e);
			// there's no network location listener to disable
		}
	}
	
	/**
	 * Tests if the given provider is the best among all location providers
	 * available
	 * 
	 * @param myLocation
	 * @return true if the location is the best choice, false otherwise
	 */
	private boolean isBestProvider(Location myLocation) {
		if (myLocation == null)
			return false;
		boolean isBestProvider = false;
		String myProvider = myLocation.getProvider();
		boolean gpsCall = myProvider
				.equalsIgnoreCase(LocationManager.GPS_PROVIDER);
		boolean networkCall = myProvider
				.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER);
		// get all location accuracy in meter; note that less is better!
		float gpsAccuracy = Float.MAX_VALUE;
		long gpsTime = 0;
		if (gpsLocationActivated) {
			Location lastGpsLocation = mLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastGpsLocation != null) {
				gpsAccuracy = lastGpsLocation.getAccuracy();
				gpsTime = lastGpsLocation.getTime();
			}
		}
		float networkAccuracy = Float.MAX_VALUE;
		if (networkLocationActivated) {
			Location lastNetworkLocation = mLocationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (lastNetworkLocation != null)
				networkAccuracy = lastNetworkLocation.getAccuracy();
		}
		float currentAccuracy = myLocation.getAccuracy();
		long currentTime = myLocation.getTime();
		// Use myLocation if:
		// 1. it's a gps location & network is disabled
		// 2. it's a gps loc & network activated
		// & gps accuracy is better than network
		// 3. it's a network loc & gps is disabled
		// 4. it's a network loc, gps enabled
		// & (network accuracy is better than gps
		// OR last network fix is newer than last gps fix+30seconds)
		boolean case1 = gpsCall && !networkLocationActivated;
		boolean case2 = gpsCall && networkLocationActivated
				&& currentAccuracy < networkAccuracy;
		boolean case3 = networkCall && !gpsLocationActivated;
		boolean case4 = networkCall
				&& gpsLocationActivated
				&& (currentAccuracy < gpsAccuracy || currentTime > gpsTime + 30000);
		if (case1 || case2 || case3 || case4) {
			isBestProvider = true;
		}
		return isBestProvider;
	}

	/**
	 * Defines the best location provider using isBestProvider() test
	 * 
	 * @return LocationProvider or null if none are available
	 */
	protected String bestProvider() {
		String bestProvider = null;
		if (networkLocationActivated
				&& isBestProvider(mLocationManager.getLastKnownLocation(
						LocationManager.NETWORK_PROVIDER))) {
			bestProvider = LocationManager.NETWORK_PROVIDER;
		} else if (gpsLocationActivated) {
			bestProvider = LocationManager.GPS_PROVIDER;
		}
		return bestProvider;
	}

	private class LocationListenerAdaptor implements LocationListener {
		public void onLocationChanged(final Location loc) {
			if (isBestProvider(loc)) {
				mLocationReceiver.onLocationChanged(loc);
				lastLocation = loc.getProvider();
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			LocationHandler.this.mNumSatellites = extras.getInt(
					"satellites", NOT_SET); // TODO Check on an actual device
			if (provider.equals(bestProvider())) {
				mLocationReceiver.onStatusChanged(provider, status, extras);
			}
		}

		public void onProviderEnabled(String a) { /* ignore */
		}

		public void onProviderDisabled(String a) { /* ignore */
		}
	}
}
