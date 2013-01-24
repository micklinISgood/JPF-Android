SECTION default {
	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
	


		
}

SECTION com.example.calculator.SimpleActivity {
	$button[0-9].onClick()
	$button<Plus|Minus|Mul|Div|More>.onClick()
	$button[0-9].onClick()
	$buttonEquals.onClick()	

}

SECTION com.example.calculator.ScientificActivity  {
	$button<Sin|Cos|Sqrt>.onClick()
}
