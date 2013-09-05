package com.example.calculator;

import com.udojava.evalex.Expression;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ScientificActivity extends Activity implements android.view.View.OnClickListener {

  /** Contains the current expression and result values */
  EditText valueEdit;

  /** Shows the previous expression for a result show in the valueEdit */
  TextView calc;

  boolean equals = false;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.scientific_activity);

    // retrieve reference to the view components
    valueEdit = (EditText) findViewById(R.id.editValue);
    calc = (TextView) findViewById(R.id.lblCalc);

    int[] names = { R.id.buttonSin, R.id.buttonCos, R.id.buttonTan, R.id.buttonPower, R.id.buttonLog,
        R.id.buttonSquareRoot, R.id.buttonPI, R.id.buttonRad, R.id.buttonDegree, R.id.buttonOpenParenthesis,
        R.id.buttonCloseParenthesis, R.id.buttonAbsolute, R.id.buttonClear, R.id.buttonDot,
        R.id.buttonEquals, R.id.buttonMore };

    Button b;
    for (int n : names) {
      b = (Button) findViewById(n);
      b.setOnClickListener(this);
    }

    // restore state
    Bundle exstra = getIntent().getExtras();
    if (exstra != null) {
      valueEdit.setText(exstra.getString("valueEdit"));
      calc.setText(exstra.getString("calcValue"));
      equals = exstra.getBoolean("equals");
      valueEdit.setSelection(exstra.getInt("start"));
    } else
      valueEdit.setText("");

  }

  @Override
  public void onClick(View v) {
    // retrieve the button that was pressed
    Button button = (Button) v;

    // get the digit/operator that was pressed
    String value = button.getText().toString();
    String expression = valueEdit.getText().toString();

    Resources res = getResources();

    if (value.equals(res.getString(R.string.equals))) {
      equals = true;

      // this is equal sign
      Expression ex = new Expression(expression);
      valueEdit.setText(ex.eval().toPlainString());
      calc.setText(expression + res.getString(R.string.equals));

    } else if (value.equals(res.getString(R.string.clear))) {

      // this is a clear operation
      valueEdit.setText("");
      calc.setText("");

    } else if (value.equals(res.getString(R.string.simple))) {

      // move to scientific activity
      Intent i = new Intent(ScientificActivity.this, com.example.calculator.SimpleActivity.class);
      i.putExtras(getExtras());
      startActivity(i);

    } else {

      // clear text if this was the first button press after equals has been called
      if (equals == true) {
        calc.setText("");
        equals = false;
      }
      
      if(value.equals("\u221A")){
        value = "sqrt()";
      }

      int start = valueEdit.getSelectionStart();
      int end = valueEdit.getSelectionEnd();
      valueEdit.getText().replace(Math.min(start, end), Math.max(start, end), value, 0, value.length());
    }
  }

  /**
   * Stores current state in a bundle
   * 
   * @return
   */
  private Bundle getExtras() {
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
