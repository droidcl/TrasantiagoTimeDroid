/* 
This file is part of OpenSatNav.

    OpenSatNav is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenSatNav is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenSatNav.  If not, see <http://www.gnu.org/licenses/>.
 */
// Created by plusminus on 00:14:42 - 02.10.2008
package org.opensatnav.android;

import org.opensatnav.android.services.LocationHandler;
import org.opensatnav.android.services.NoLocationProvidersException;
import org.osmdroid.constants.OpenStreetMapConstants;

import cl.droid.transantiago.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

/**
 * Baseclass for Activities who want to contribute to the OpenStreetMap Project.
 * 
 * @author Nicolas Gramlich
 * 
 */
public abstract class OpenStreetMapActivity extends Activity implements
		OpenStreetMapConstants, LocationListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected PowerManager.WakeLock wl;

	protected LocationHandler mLocationHandler;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// check for network
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// get state for both phone network and wifi
		if ((connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED)
				&& (connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED)) {
			Toast.makeText(this, R.string.network_unavailable,
					Toast.LENGTH_LONG).show();
		}

		mLocationHandler = new LocationHandler(
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				this, this);
		try {
			mLocationHandler.start();
		} catch (NoLocationProvidersException e) {
			showNoLocationProvidersScreen();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			mLocationHandler.start();
		} catch (NoLocationProvidersException e) {
		}
	}

	@Override
	public void onRestart() {
		super.onRestart();
		try {
			mLocationHandler.start();
		} catch (NoLocationProvidersException e) {
			showNoLocationProvidersScreen();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// get screen to stay on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				OpenSatNavConstants.LOG_TAG);
		wl.acquire();
	}

	public void showNoLocationProvidersScreen() {
		// no location providers are available, ask the user if they
		// want to go and change the setting
		AlertDialog.Builder builder = new AlertDialog.Builder(
				OpenStreetMapActivity.this);
		builder.setCancelable(true);
		builder.setMessage(R.string.location_services_disabled).setCancelable(
				false).setPositiveButton(
		// FIXME ZeroG - 2009/12/28 replace by android.R.string.yes once it's
		// fixed upstream
				R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							dialog.dismiss();
						} catch (IllegalArgumentException e) {
							// if orientation change, thread continue but the
							// dialog cannot be dismissed without exception
						}
						OpenStreetMapActivity.this
								.startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton(
		// FIXME ZeroG - 2009/12/28 replace by android.R.string.no once it's
		// fixed upstream
				R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public abstract void onLocationChanged(final Location pLoc);

	public void onStatusChanged(String provider, int status, Bundle extras) { /* ignore */
	}

	public void onProviderEnabled(String a) { /* ignore */
	}

	public void onProviderDisabled(String a) { /* ignore */
	}

	@Override
	protected void onPause() {
		super.onPause();
		wl.release(); // allow the screen to turn off again
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationHandler.stop();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
