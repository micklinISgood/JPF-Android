package com.example.calculator;

import gov.nasa.jpf.annotation.Checkpoint;
import android.content.Intent;
import android.os.Bundle;

public class ScientificActivity extends CalculatorActivity {

	@Checkpoint("createScientificActivity")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scientific_activity);
		setupGUI();
		
		// restore state
		restoreState();
	}

	@Checkpoint("startSimpleActivity")
	protected void startNextActivity() {
		// move to scientific activity
		Intent i = new Intent(ScientificActivity.this,
				com.example.calculator.SimpleActivity.class);
		i.putExtras(getState());
		startActivity(i);

	}

}
