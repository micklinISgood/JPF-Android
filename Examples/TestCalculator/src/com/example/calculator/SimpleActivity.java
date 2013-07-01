package com.example.calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author Heila van der Merwe
 * 
 */
public class SimpleActivity extends Activity implements android.view.View.OnClickListener {
  EditText valueEdit;
  char op = 0;
  double value = 0;
  TextView calc;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    valueEdit = (EditText) findViewById(R.id.editValue);
    calc = (TextView) findViewById(R.id.lblCalc);

    Bundle exstra = getIntent().getExtras();
    if (exstra != null) {
      value = exstra.getDouble("value");
      op = exstra.getChar("op");
      valueEdit.setText(exstra.getString("valueEdit"));
      calc.setText(exstra.getString("calcValue"));
    } else
      valueEdit.setText("0.0");

    int[] names = { R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
        R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.buttonPlus, R.id.buttonMinus,
        R.id.buttonMul, R.id.buttonDiv, R.id.buttonDot, R.id.buttonEquals, R.id.buttonClear, R.id.buttonMore };

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
      if (!val.equals("") && Double.parseDouble(val) == 0) {
        if (text.equals(".") || val.endsWith("."))
          valueEdit.setText(val + "" + text);
        else
          valueEdit.setText(text);

      } else {
        if (text.equals("."))
          valueEdit.setText("0" + text);
        else
          valueEdit.setText(val + "" + text);
      }
      if (op == 0)
        calc.setText("");
      else
        calc.setText(calc.getText() + text);
    } else if (text.matches("[+-/*]")) {
      if (op != 0) { // when this is not first operation first calculate next result
        if (!val.equals("")) // there is no starting value
          value = eval(value, op, Double.parseDouble(val));
      } else
        value = Double.parseDouble(val);
      valueEdit.setText("0");
      op = text.charAt(0);
      calc.setText(String.valueOf(value) + op);
    } else if (text.equals("=")) {
      double old = value;
      if (!val.equals("") && op != 0) { // there is no starting value
        value = eval(value, op, Double.parseDouble(val));
        valueEdit.setText(String.valueOf(value));
        calc.setText(String.valueOf(old) + Character.toString(op) + Double.parseDouble(val) + "=");
        System.out.println(calc.getText() + valueEdit.getText().toString());
        System.out.println("******************************");

      }
      op = 0;
    } else if (text.equals("C")) {
      value = 0;
      op = 0;
      valueEdit.setText("0");
      calc.setText("");
    } else if (text.equals("->")) {

      Intent i = new Intent(SimpleActivity.this, com.example.calculator.ScientificActivity.class);
      i.putExtras(getExtras());
      startActivity(i);
    }

  }

  private double eval(double v1, char op, double v2) {
    double result = 0;
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
      if (v2 == 0) {
       // throw new ArithmeticException("DivisionByZero");
      }
      result = v1 / v2;
      break;
    case '^':
      result = Math.pow(v1, v2);
      break;
    default:
      break;
    }
    return result;
  }

  private Bundle getExtras() {
    String val = valueEdit.getText().toString();
    Bundle b = new Bundle();
    b.putDouble("value", value);
    b.putString("valueEdit", val);
    b.putChar("op", op);
    b.putString("calcValue", calc.getText().toString());
    return b;
  }
}
