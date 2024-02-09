/*
 * CoolReader for Android
 * Copyright (C) 2014 Vadim Lopatin <coolreader.org@gmail.com>
 * Copyright (C) 2014 fuero <the_master_of_disaster@gmx.at>
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.coolreader.crengine;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.List;

public class EinkScreenTolino implements EinkScreen {

	public static final Logger log = L.create("tolino", Log.VERBOSE);

	/// variables
	protected EinkUpdateMode mUpdateMode = EinkUpdateMode.Unspecified;
	// 0 - Clear, set only for old_mode == 2
	// 1 - Fast, always set in prepare
	// 2 - Active, set in prepare
	protected int mUpdateInterval;
	protected int mRefreshNumber = -1;
	protected boolean mIsSleep = false;

	@Override
	public void setupController(EinkUpdateMode mode, int updateInterval, View view) {
		mUpdateInterval = updateInterval;
		if (mUpdateMode.equals(mode))
			return;
		log.d("EinkScreenTolino.setupController(): mode=" + mode);
		switch (mode) {
			case Clear:
				if (mUpdateMode == EinkUpdateMode.Active) {
					mRefreshNumber = -1;
				} else {
					mRefreshNumber = 0;
				}
				break;
			case Fast:
				mRefreshNumber = 0;
				break;
			default:
				mRefreshNumber = -1;
		}
		mUpdateMode = mode;
	}

	@Override
	public void prepareController(View view, boolean isPartially) {
		//System.err.println("Sleep = " + isPartially);
		if (isPartially || mIsSleep != isPartially) {
			tolinoSleepController(isPartially, view);
//			if (isPartially)
			return;
		}
		if (mRefreshNumber == -1) {
			switch (mUpdateMode) {
				case Clear:
					tolinoSetMode(view, mUpdateMode);
					break;
				case Active:
					if (mUpdateInterval == 0) {
						tolinoSetMode(view, mUpdateMode);
					}
					break;
			}
			mRefreshNumber = 0;
			return;
		}
		if (mUpdateMode == EinkUpdateMode.Clear) {
			tolinoSetMode(view, mUpdateMode);
			return;
		}
		if (mUpdateInterval > 0 || mUpdateMode == EinkUpdateMode.Fast) {
			if (mRefreshNumber == 0 || (mUpdateMode == EinkUpdateMode.Fast && mRefreshNumber < mUpdateInterval)) {
				switch (mUpdateMode) {
					case Active:
						tolinoSetMode(view, mUpdateMode);
						break;
					case Fast:
						tolinoSetMode(view, mUpdateMode);
						break;
				}
			} else if (mUpdateInterval <= mRefreshNumber) {
				tolinoSetMode(view, EinkUpdateMode.Clear);
				mRefreshNumber = -1;
			}
			if (mUpdateInterval > 0) {
				mRefreshNumber++;
			}
		}
	}


	// private methods
	private void tolinoSleepController(boolean toSleep, View view) {
		if (toSleep != mIsSleep) {
			log.d("+++SleepController " + toSleep);
			mIsSleep = toSleep;
			if (mIsSleep) {
				switch (mUpdateMode) {
					case Clear:
						break;
					case Fast:
						break;
					case Active:
						tolinoSetMode(view, EinkUpdateMode.Clear);
						mRefreshNumber = -1;
				}
			} else {
				setupController(mUpdateMode, mUpdateInterval, view);
			}
		}
	}

	private void tolinoSetMode(View view, EinkUpdateMode mode) {
		TolinoEpdController.setMode(view, mode);
	}
	@Override
	public void updateController(View view, boolean isPartially) {
		// do nothing...
	}

	@Override
	public void refreshScreen(View view) {
		mRefreshNumber = -1;
	}

	@Override
	public EinkUpdateMode getUpdateMode() {
		return mUpdateMode;
	}

	@Override
	public int getUpdateInterval() {
		return mUpdateInterval;
	}

	@Override
	public int getFrontLightValue(Context context) {
		return 0;
	}

	@Override
	public boolean setFrontLightValue(Context context, int value) {
		return false;
	}

	@Override
	public int getWarmLightValue(Context context) {
		return 0;
	}

	@Override
	public boolean setWarmLightValue(Context context, int value) {
		return false;
	}

	@Override
	public List<Integer> getFrontLightLevels(Context context) {
		return null;
	}

	@Override
	public List<Integer> getWarmLightLevels(Context context) {
		return null;
	}

	@Override
	public boolean isAppOptimizationEnabled() {
		return false;
	}
}
