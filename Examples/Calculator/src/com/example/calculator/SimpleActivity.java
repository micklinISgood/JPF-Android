package com.example.calculator;

import gov.nasa.jpf.annotation.Checkpoint;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udojava.evalex.Expression;

/**
 * This application is a simple Calculator that has two modes: Simple and Scientific mode. Each mode is
 * represented by an Activity.
 * 
 * The application shows how jpf-android supports ANY structures in its input script to execute multiple event
 * sequences. It also shows how jpf-android can support multiple Activity switching.
 * 
 * The application is used to detect an Arithmetic exception, using jpf-android, that is thrown by the
 * application when we divide by zero or asks for the square root of a negative number. Lastly it can detect a
 * NullPointer de-referencing exception when sending data between Activities.
 * 
 * @author Heila van der Merwe
 * @version 3 - 6 July 2013
 * 
 */
public class SimpleActivity extends Activity implements android.view.View.OnClickListener {

  /** Contains the current expression and result values */
  EditText valueEdit;

  /** Shows the previous expression for a result show in the valueEdit */
  TextView calc;

  boolean equals = false;

  EditText editText1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);

    editText1 = (EditText) findViewById(R.id.editText1);
    editText1.setText("EQUALS: " + equals);

    // retrieve reference to the view components
    valueEdit = (EditText) findViewById(R.id.editValue);
    calc = (TextView) findViewById(R.id.lblCalc);

    int[] names = { R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
        R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.buttonPlus, R.id.buttonMinus,
        R.id.buttonMul, R.id.buttonDiv, R.id.buttonDot, R.id.buttonEquals, R.id.buttonClear, R.id.buttonMore,
        R.id.buttonOpenParenthesis, R.id.buttonCloseParenthesis, R.id.buttonBack };

    // add listener to buttons
    Button b;
    for (int n : names) {
      b = (Button) findViewById(n);
      b.setOnClickListener(this);
    }

    Bundle exstra = getIntent().getExtras();
    if (exstra != null) {
      restoreState(exstra);
    } else
      valueEdit.setText("");

  }

   @Checkpoint("restoreState")
  private void restoreState(Bundle exstra) {
    // restore state

    valueEdit.setText(exstra.getString("valueEdit"));
    calc.setText(exstra.getString("calcValue"));
    equals = exstra.getBoolean("equals");
    valueEdit.setSelection(exstra.getInt("start"));

  }

  @Override
   @Checkpoint("buttonclick")
  public void onClick(View v) {
    // retrieve the button that was pressed
    Button button = (Button) v;

    // get the digit/operator that was pressed
    String value = button.getText().toString();
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
      valueEdit.getText().replace(Math.min(start, end)-1, Math.max(start, end), "", 0, 0);

    } else if (value.equals(res.getString(R.string.clear))) {

      // this is a clear operation
      updateDisplay("", "");

    } else if (value.equals(res.getString(R.string.scientific))) {

      // move to scientific activity
      startScientificActivity();

    } else {
      // clear text if this was the first button press after equals has been called
      if (equals == true) {
        calc.setText("");
        equals = false;
      }
      int start = valueEdit.getSelectionStart();
      int end = valueEdit.getSelectionEnd();
      valueEdit.getText().replace(Math.min(start, end), Math.max(start, end), value, 0, value.length());
    }
    editText1.setText("EQUALS: " + equals);

  }

  @Checkpoint("calculate")
  private void calculate(String expression) {
    BigDecimal result = null;
    try {
      Expression ex = new Expression(expression);
      result = ex.eval();
      updateDisplay(result.toPlainString(), expression + "=");
    } catch (Exception e) {
      showError(e);
    }

  }

  @Checkpoint("updateDisplay")
  private void updateDisplay(String currentExpression, String oldExpression) {
    valueEdit.setText(currentExpression);
    calc.setText(oldExpression);

  }
  
  @Checkpoint("showError")
  private void showError(Exception e) {
    Toast.makeText(this, "Error evaluating expression: " + e.toString(), 3).show();
  }

  @Checkpoint("startScientificActivity")
  private void startScientificActivity() {
    Intent i = new Intent(SimpleActivity.this, com.example.calculator.ScientificActivity.class);
    i.putExtras(getState());
    startActivity(i);

  }

  /**
   * Stores current state in a bundle
   * 
   * @return
   */
   @Checkpoint("getSavedState")
  private Bundle getState() {
    String expression = valueEdit.getText().toString();
    Bundle b = new Bundle();
    b.putString("valueEdit", expression);
    b.putString("calcValue", calc.getText().toString());
    b.putBoolean("equals", equals);
    int start = valueEdit.getSelectionStart();
    b.putInt("start", start);
    return b;
  }
   
   
   public static void main(String[] args){
     ActivityThread.start(null);
   }

}
