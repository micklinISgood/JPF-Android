SECTION default {
	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
}

SECTION com.example.calculator.SimpleActivity {
	$button1.onClick()
	$button<Plus|Div|Next>.onClick()
	$button0.onClick()
	$buttonEquals.onClick()

}

SECTION com.example.calculator.ScientificActivity  {
	$buttonSin.onClick()
	$buttonEquals.onClick()
	
}