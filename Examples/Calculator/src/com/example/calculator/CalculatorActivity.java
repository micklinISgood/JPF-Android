package com.example.calculator;

import gov.nasa.jpf.annotation.Checkpoint;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udojava.evalex.Expression;

/**
 * Base Activity class for SimpleActivity and ScienticActivity. Handles the
 * logic of both Activities.
 * 
 * @author Heila van der Merwe
 * 
 */
public class CalculatorActivity extends Activity implements OnClickListener {
	private final String TAG = this.getClass().getSimpleName();

	/** Contains the current expression and result values */
	EditText valueEdit;

	/** Shows the previous expression for a result show in the valueEdit */
	TextView calc;

	boolean equals = false;

	@Checkpoint("setupGUI")
	protected void setupGUI() {
		// retrieve reference to the view components
		valueEdit = (EditText) findViewById(R.id.editValue);
		calc = (TextView) findViewById(R.id.lblCalc);

		int[] names = { R.id.button0, R.id.button1, R.id.button2, R.id.button3,
				R.id.button4, R.id.button5, R.id.button6, R.id.button7,
				R.id.button8, R.id.button9, R.id.buttonPlus, R.id.buttonMinus,
				R.id.buttonMul, R.id.buttonDiv, R.id.buttonSin, R.id.buttonCos,
				R.id.buttonTan, R.id.buttonPower, R.id.buttonLog,
				R.id.buttonSquareRoot, R.id.buttonPI, R.id.buttonRad,
				R.id.buttonDegree, R.id.buttonOpenParenthesis,
				R.id.buttonCloseParenthesis, R.id.buttonAbsolute,
				R.id.buttonClear, R.id.buttonDot, R.id.buttonEquals,
				R.id.buttonNext, R.id.buttonBack };

		// add listener to buttons
		Button b;
		for (int n : names) {
			b = (Button) findViewById(n);
			if (b != null)
				b.setOnClickListener(this);
		}

		// restore state
		restoreState();
	}

	@Checkpoint("restoreState")
	protected void restoreState() {
		Bundle exstra = getIntent().getExtras();
		if (exstra != null) {
			// restore state
			valueEdit.setText(exstra.getString("valueEdit"));
			calc.setText(exstra.getString("calcValue"));
			equals = exstra.getBoolean("equals");
			valueEdit.setSelection(exstra.getInt("start"));
		} else
			valueEdit.setText("");
	}

	@Override
	@Checkpoint("buttonclick")
	public void onClick(View v) {
		// retrieve the button that was pressed
		Button button = (Button) v;

		// get the digit/operator that was pressed
		String value = button.getText().toString();

		// get the current expression in the valueEdit
		String expression = valueEdit.getText().toString();

		Resources res = getResources();

		if (value.equals(res.getString(R.string.equals))) {
			equals = true;

			// calculate result of current expression
			calculate(expression);

		} else if (value.equals(res.getString(R.string.back))) {

			// this is a clear operation
			int start = valueEdit.getSelectionStart();
			int end = valueEdit.getSelectionEnd();
			valueEdit.getText().replace(Math.min(start, end) - 1,
					Math.max(start, end), "", 0, 0);

		} else if (value.equals(res.getString(R.string.clear))) {

			// this is a clear operation
			clearDisplay();

		} else if (value.equals(res.getString(R.string.next))) {

			// move to scientific activity
			startNextActivity();

		} else {
			expressionButtonPressed(value);
		}

	}

	@Checkpoint("clearDisplay")
	private void clearDisplay() {
		valueEdit.setText("");
		calc.setText("");
	}

	@Checkpoint(value = "calculate")
	private void calculate(String expression) {
		BigDecimal result = null;
		try {

			// evaluate expression
			Expression ex = new Expression(expression);
			result = ex.eval();

			Log.i(TAG,
					"Expression: " + expression + "=" + result.toPlainString());

			// update the display showing the result
			updateDisplay(result.toPlainString(), expression + "=");

		} catch (Exception e) {
			// the expression had an error, display an error and clear display
			showError(e);
			clearDisplay();
		}
	}

	@Checkpoint(value = "updateDisplay")
	private void updateDisplay(String currentExpression, String oldExpression) {

		valueEdit.setText(currentExpression);
		calc.setText(oldExpression);

	}

	@Checkpoint("showError")
	private void showError(Exception e) {
		Toast.makeText(this, "Error evaluating expression: " + e.toString(),
				Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Error evaluating expression: "
				+ valueEdit.getText().toString());
	}

	@Checkpoint("startNextActivity")
	protected void startNextActivity() {
	}

	@Checkpoint("expressionButtonPressed")
	private void expressionButtonPressed(String value) {

		// clear text if this was the first button press after equals has
		// been called
		if (equals == true) {
			calc.setText("");
			equals = false;
		}

		if (value.equals("\u221A")) {
			value = "sqrt(";
		}

		// correct way to do this, but we do not support this yet
		// int start = valueEdit.getSelectionStart();
		// int end = valueEdit.getSelectionEnd();
		// valueEdit.getText().replace(Math.min(start, end), Math.max(start,
		// end), value, 0, value.length());
		valueEdit.setText(valueEdit.getText().toString() + value);
	}

	/**
	 * Stores current state in a bundle
	 * 
	 * @return
	 */
	@Checkpoint("saveState")
	protected Bundle getState() {
		String expression = valueEdit.getText().toString();
		Bundle b = new Bundle();
		b.putString("valueEdit", expression);
		b.putString("calcValue", calc.getText().toString());
		b.putBoolean("equals", equals);
		int start = valueEdit.getSelectionStart();
		b.putInt("start", start);
		return b;
	}
}
