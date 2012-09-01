package com.example.calculator;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author Heila van der Merwe
 * 
 */
public class SimpleActivity extends Activity implements android.view.View.OnClickListener {
  EditText valueEdit;
  char op = 0;
  float value = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    valueEdit = (EditText) findViewById(R.id.editValue);

    Bundle exstra = getIntent().getExtras();
    if (exstra != null) {
      value = exstra.getFloat("value");
      op = exstra.getChar("op");
      valueEdit.setText(exstra.getString("valueEdit"));
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
      if (!val.equals("") && Float.parseFloat(val) == 0) {
        valueEdit.setText(text);
      } else {
        valueEdit.setText(val + "" + text);
      }
    } else if (text.matches("[+-/*]")) {
      if (op != 0) {
        value = eval(value, op, Float.parseFloat(valueEdit.getText().toString()));
      } else
        value = Float.parseFloat(val);
      valueEdit.setText("");
      op = text.charAt(0);
    } else if (text.equals("=")) {

      value = eval(value, op, Float.parseFloat(valueEdit.getText().toString()));
      valueEdit.setText(String.valueOf(value));
      op = 0;

    } else if (text.equals("C")) {
      value = 0;
      op = 0;
      valueEdit.setText("0");
    } else if (text.equals("->")) {
      Intent i = new Intent(SimpleActivity.this, com.example.calculator.ScientificActivity.class);
      i.putExtras(getExtras());
      startActivity(i);
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
      System.out.println(v2);
      if (v2 == 0)
        throw new ArithmeticException("DivisionByZero");
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

  /**
   * The main entry point to the application
   * 
   * @param args
   */
  public static void main(String[] args) {
    ActivityThread.main(null);
  }

}
