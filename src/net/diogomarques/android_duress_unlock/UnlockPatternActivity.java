package net.diogomarques.android_duress_unlock;

import java.util.List;

import net.diogomarques.com.android.internal.widget.LockPatternView;
import net.diogomarques.com.android.internal.widget.LockPatternView.Cell;
import net.diogomarques.com.android.internal.widget.LockPatternView.DisplayMode;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class UnlockPatternActivity extends Activity {

	protected static final int MAX_TRIALS = 3;
	protected int tryCount = 0;

	protected static final String TAG = UnlockPatternActivity.class
			.getSimpleName();
	LockPatternView mLockPatternView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullWindowNoRotationAndLock();
		setContentView(R.layout.activity_unlock_pattern);
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

	private void handlePatternDetection(List<Cell> pattern, float x, float y) {
		Cell lastCell = pattern.get(pattern.size() - 1);
		Log.w(TAG,
				"Last cell x,y: "
						+ mLockPatternView.getCenterXForColumn(lastCell.column)
						+ "," + mLockPatternView.getCenterYForRow(lastCell.row));
		Log.w(TAG, "Release x,y: " + x + "," + y);
		String detectedPattern = PatternUtils.convertDrawPattern(pattern);
		if (detectedPattern.equals(getPattern()))
			handleUnlockSucess();
		else
			handleUnlockFailure();

	}

	protected void handleUnlockSucess() {
		mLockPatternView.clearPattern();
		Toast.makeText(this, "Unlocked!", Toast.LENGTH_SHORT).show();
		setResult(Activity.RESULT_OK);
		finish();
	}

	protected void handleUnlockFailure() {
		tryCount++;
		if (tryCount >= MAX_TRIALS) {
			handleUnlockFailureExceededTrials();
		} else {
			handleUnlockFailureAttemptsLeft();
		}
	}

	protected void handleUnlockFailureAttemptsLeft() {
		mLockPatternView.setDisplayMode(DisplayMode.Wrong);
		Toast.makeText(this,
				"Wrong code." + (MAX_TRIALS - tryCount) + " attempts left.",
				Toast.LENGTH_SHORT).show();
	}

	protected void handleUnlockFailureExceededTrials() {
		Toast.makeText(this,
				"Wrong code. Failed all " + MAX_TRIALS + " attemps.",
				Toast.LENGTH_SHORT).show();
	}

	protected String getPattern() {
		// TODO: get persisted pattern
		return "1234";
	}

}
