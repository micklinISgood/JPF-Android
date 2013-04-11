SECTION default {
	@startIntent.setComponent("com.example.calculator.SimpleActivity")
	startActivity(@startIntent)
	


		
}

SECTION com.example.calculator.SimpleActivity {
	$backButton.onClick()	

}