SECTION default {
	@intent1.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@intent1)
}

SECTION com.example.calculator.SimpleActivity {
	$button1.onClick()
	$buttonDiv.onClick()
	$button0.onClick()
	$buttonEquals.onClick()
}

