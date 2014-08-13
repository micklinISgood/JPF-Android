SECTION default {
	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
}

SECTION com.example.calculator.SimpleActivity {
	$button[0-9].onClick()
	$button<Minus|Mul|Div>.onClick()
	$button[0-9].onClick()
	$buttonEquals.onClick()

}