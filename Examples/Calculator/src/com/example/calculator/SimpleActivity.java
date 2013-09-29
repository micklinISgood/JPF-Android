package com.example.calculator;

import gov.nasa.jpf.annotation.Checkpoint;
import android.content.Intent;
import android.os.Bundle;

/**
 * This application is a simple Calculator that has two modes: Simple and
 * Scientific mode. Each mode is represented by an Activity.
 * 
 * The application shows how jpf-android supports ANY structures in its input
 * script to execute multiple event sequences. It also shows how jpf-android can
 * support multiple Activity switching.
 * 
 * The application is used to detect an Arithmetic exception, using jpf-android,
 * that is thrown by the application when we divide by zero or asks for the
 * square root of a negative number. Lastly it can detect a NullPointer
 * de-referencing exception when sending data between Activities.
 * 
 * @author Heila van der Merwe
 * 
 */
public class SimpleActivity extends CalculatorActivity {

	@Checkpoint("createSimpleActivity")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_activity);
		setupGUI();
	}

	@Checkpoint("startScientificActivity")
	protected void startNextActivity() {
		Intent i = new Intent(SimpleActivity.this,
				com.example.calculator.ScientificActivity.class);
		i.putExtras(getState());
		startActivity(i);

	}

}
