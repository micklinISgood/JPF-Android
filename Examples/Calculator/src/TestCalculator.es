SECTION default {
	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
}

SECTION com.example.calculator.SimpleActivity {
	$button0.onClick()
	$buttonPlus.onClick()
	$button1.onClick()
	$buttonEquals.onClick()

	

}

SECTION com.example.calculator.ScientificActivity  {
	$buttonSin.onClick()
}