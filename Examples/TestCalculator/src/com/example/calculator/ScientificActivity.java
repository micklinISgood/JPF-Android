package com.example.calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ScientificActivity extends Activity implements android.view.View.OnClickListener {

  EditText valueEdit;
  char op;

  float value = 0;

  // Button add,minus,mult,div;
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.scientific_activity);

    valueEdit = (EditText) findViewById(R.id.editValue);

    Bundle exstra = getIntent().getExtras();
    value = exstra.getFloat("value");
    op = exstra.getChar("op");
    valueEdit.setText(exstra.getString("valueEdit"));

    int[] names = { R.id.buttonSin, R.id.buttonCos, R.id.buttonTan, R.id.buttonCsin, R.id.buttonSec,
        R.id.buttonCot, R.id.buttonLn, R.id.buttonLog, R.id.buttonExp, R.id.button0, R.id.buttonPlus,
        R.id.buttonMinus, R.id.buttonMul, R.id.buttonDiv, R.id.buttonDot, R.id.buttonEquals,
        R.id.buttonClear, R.id.buttonMore };

    Button b;
    for (int n : names) {
      b = (Button) findViewById(n);
      b.setOnClickListener(this);
    }

  }

  @Override
  public void onClick(View v) {
    Button button = (Button) v;
    String text = button.getText().toString();
    String val = valueEdit.getText().toString();
    if (text.matches("[0-9]") || text.equals(".")) {
      if (!val.equals("") && Float.parseFloat(val) == 0) {
        valueEdit.setText(text);
      } else {
        valueEdit.setText(val + "" + text);
      }
    } else if (text.equals("=")) {
      value = eval(value, op, Float.parseFloat(valueEdit.getText().toString()));
      valueEdit.setText(String.valueOf(value));

    } else if (text.equals("C")) {
      value = 0;
      op = 0;
      valueEdit.setText("0");
    } else if (text.equals("->")) {
      Intent i = new Intent(ScientificActivity.this, SimpleActivity.class);
      i.putExtras(getExtras());
      startActivity(i);

    } else {
      valueEdit.setText("");
      value = Float.parseFloat(val);
      op = text.charAt(0);
    }

  }

  private float eval(float v1, char op, float v2) {
    float result = 0;
    switch (op) {
    case '+':
      result = v1 + v2;
      break;
    case '-':
      result = v1 - v2;
      break;
    case '*':
      result = v1 * v2;
      break;
    case '/':
      System.out.println("********************************************************** ");
      if (v2 == 0) {
        System.out.println("********************************************************** ");
        throw new ArithmeticException("DivisionByZero");
      }
      result = v1 / v2;
      break;

    default:
      break;
    }
    return result;
  }

  private Bundle getExtras() {
    String val = valueEdit.getText().toString();
    Bundle b = new Bundle();
    b.putFloat("value", value);
    b.putString("valueEdit", val);
    b.putChar("op", op);
    return b;
  }

}
