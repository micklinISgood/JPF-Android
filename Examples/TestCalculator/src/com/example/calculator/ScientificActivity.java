package com.example.calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ScientificActivity extends Activity implements android.view.View.OnClickListener {

  EditText valueEdit2;

  char op;
  double value = 0;
  TextView calc;

  // Button add,minus,mult,div;
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.scientific_activity);

    valueEdit2 = (EditText) findViewById(R.id.editValue2);
    calc = (TextView) findViewById(R.id.lblCalc2);

    Bundle exstra = getIntent().getExtras();
    value = exstra.getDouble("value");
    op = exstra.getChar("op");
    valueEdit2.setText(exstra.getString("valueEdit"));
    calc.setText(exstra.getString("calcValue"));

    int[] names = { R.id.buttonSin, R.id.buttonCos, R.id.buttonTan, R.id.button1x, R.id.buttonFac,
        R.id.buttonSqrt, R.id.buttonLn, R.id.buttonLog, R.id.buttonPers, R.id.buttonPi, R.id.buttonPow,
        R.id.buttonSqr, R.id.buttonClear2, R.id.buttonMore2 };

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
    String val = valueEdit2.getText().toString();
    double old = Double.parseDouble(val);
    if (text.equals("sin")) {
      value = Math.sin(old);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("cos")) {
      value = Math.cos(value);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("tan")) {
      value = Math.tan(value);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("ln")) {
      value = Math.log(value);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("log")) {
      value = Math.log10(value);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("1/x")) {
      value = 1 / value;
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("x!")) {
      if (value < 12) {
        int fact = 1;
        for (int i = 1; i <= value; i++) {
          fact = fact * i;
        }
        value = fact;
        valueEdit2.setText(String.valueOf(value));
        op = 0;
      }
    } else if (text.equals("x^2")) {
      value = Math.pow(value, 2);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("%")) {
      value = value / 100;
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("sqrt")) {
      value = Math.sqrt(value);
      valueEdit2.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("abs")) {
      value = Math.abs(value);
      valueEdit2.setText(String.valueOf(value));
      op = 0;
    }

    if (text.equals("C")) {
      value = 0;
      op = 0;
      valueEdit2.setText("0");
      calc.setText("");
    } else if (text.equals("->")) {
      Intent i = new Intent(ScientificActivity.this, SimpleActivity.class);
      i.putExtras(getExtras());
      startActivity(i);
    } else if (text.equals("x^y")) {
      valueEdit2.setText("0");
      op = '^';
      calc.setText(String.valueOf(value) + "^");
    } else {
      calc.setText(text + "(" + old + ")" + "=");
    }

  }

  private Bundle getExtras() {
    String val = valueEdit2.getText().toString();
    Bundle b = new Bundle();
    b.putDouble("value", value);
    b.putString("valueEdit", val);
    b.putChar("op", op);
    b.putString("calcValue", calc.getText().toString());
    return b;
  }

}
