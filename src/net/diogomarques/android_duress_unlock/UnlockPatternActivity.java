package net.diogomarques.android_duress_unlock;

import java.util.List;

import net.diogomarques.com.android.internal.widget.LockPatternView;
import net.diogomarques.com.android.internal.widget.LockPatternView.Cell;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class UnlockPatternActivity extends Activity {

	protected static final int MAX_TRIALS = 3;
	protected int tryCount = 0;
	// TODO: this value (and computations that use it) should be independent
	// from screen
	protected static final int MOVEMENT_AGAINST_GRAIN_THRESHOLD_IN_GNEXUS = 0;
	protected static final int TARGET_BOUNDAY_RADIUS = 60;

	protected static final String TAG = UnlockPatternActivity.class
			.getSimpleName();
	LockPatternView mLockPatternView;
	TextView mTextViewBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullWindowNoRotationAndLock();
		setContentView(R.layout.activity_unlock_pattern);
		mTextViewBottom = (TextView) findViewById(R.id.textViewBottom);
		mLockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
		mLockPatternView
				.setOnPatternListener(new LockPatternView.OnPatternListener() {

					@Override
					public void onPatternStart() {
					}

					@Override
					public void onPatternDetected(List<Cell> pattern, float x,
							float y) {
						handlePatternDetection(pattern, x, y);
					}

					@Override
					public void onPatternCleared() {
					}

					@Override
					public void onPatternCellAdded(List<Cell> pattern) {
					}
				});
	}

	public void setFullWindowNoRotationAndLock() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	enum LastStrokeOrientation {
		Vertical, Horizontal
	}

	private void handlePatternDetection(List<Cell> pattern, float releaseX,
			float releaseY) {
		// do nothing if pattern only has one point
		if (pattern.size() < 2)
			return;
		Cell lastCell = pattern.get(pattern.size() - 1);
		float lastCellX = mLockPatternView.getCenterXForColumn(lastCell.column);
		float lastCellY = mLockPatternView.getCenterYForRow(lastCell.row);

		Cell cellBeforeLast = pattern.get(pattern.size() - 2);
		Log.w(TAG, "Last cell (row " + lastCell.row + " collumn "
				+ lastCell.column + ") x,y: " + lastCellX + "," + lastCellY);
		Log.w(TAG, "Release x,y: " + releaseX + "," + releaseY);

		// Check if release is against natural movement
		// TODO: other orientations

		// get orientations
		LastStrokeOrientation orientation = LastStrokeOrientation.Horizontal;
		if (lastCell.column == cellBeforeLast.column)
			orientation = LastStrokeOrientation.Vertical;

		// verify that if release was against the flow
		// TODO: improve this using the angle between the stroke vector and the
		// center to release vector
		boolean releaseIsAgainstFlow = false;
		if (orientation == LastStrokeOrientation.Horizontal) {
			// get natural direction (1 LH to RH, -1 otherwise)
			int direction = lastCell.column - cellBeforeLast.column;
			double naturalReleaseTreshold = lastCellX - direction
					* MOVEMENT_AGAINST_GRAIN_THRESHOLD_IN_GNEXUS;
			releaseIsAgainstFlow = (direction * releaseX) < (direction * naturalReleaseTreshold);

		} else {
			// get natural direction (1 TOP to BOTTOM, -1 otherwise)
			int direction = lastCell.row - cellBeforeLast.row;
			double naturalReleaseTreshold = lastCellY - direction
					* MOVEMENT_AGAINST_GRAIN_THRESHOLD_IN_GNEXUS;
			releaseIsAgainstFlow = (direction * releaseY) < (direction * naturalReleaseTreshold);
		}

		// verify that if release was outside the radius
		boolean releaseIsOutsideRadius = (Math.pow((releaseX - lastCellX), 2) + Math
				.pow(releaseY - lastCellY, 2)) > Math.pow(
				TARGET_BOUNDAY_RADIUS, 2);

		String detectedPattern = PatternUtils.convertDrawPattern(pattern);
		if (releaseIsAgainstFlow && releaseIsOutsideRadius
				&& detectedPattern.equals(getPattern()))
			handleUnlockSucess();
		else
			handleUnlockFailure();
	}

	protected void handleUnlockSucess() {
		mLockPatternView.clearPattern();
		mTextViewBottom.setText("Unlocked!");
		new CountDownTimer(2000, 2000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				mTextViewBottom.setText("");
			}
		}.start();
	}

	protected void handleUnlockFailure() {
		mTextViewBottom.setText("Failed!");
	}

	protected String getPattern() {
		// TODO: get persisted pattern
		return "12365";
	}

}
