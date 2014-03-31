SECTION default {

	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
}

SECTION com.example.calculator.SimpleActivity {
	$button1.onClick()
	$buttonMinus.onClick()
	$buttonNext.onClick()
}

SECTION com.example.calculator.ScientificActivity  {
	$button<Sin|Cos|Tan>.onClick()
	$buttonOpenParenthesis.onClick()
	$buttonPI.onClick()
	$buttonCloseParenthesis.onClick()
	$buttonEquals.onClick()
}