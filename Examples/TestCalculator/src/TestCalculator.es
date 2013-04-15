SECTION default {
	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
}

SECTION com.example.calculator.SimpleActivity {
	$button[0-2].onClick()
	$button<Plus|Minus|More|Div>.onClick()
	$button[0-2].onClick()
	$buttonEquals.onClick()	

}

SECTION com.example.calculator.ScientificActivity  {
	$button<Sin|Cos|Sqrt>.onClick()
}
