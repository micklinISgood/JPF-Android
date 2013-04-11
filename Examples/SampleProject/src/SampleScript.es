SECTION default {
	@intent1.setComponent("com.example.com.SampleProjectActivity")
	startActivity(@intent1)
}

SECTION com.example.com.SampleProjectActivity {
	$buttonPrintHallo.onClick()
	$buttonResult.onClick()
	
	$buttonPrintHallo.onClick()
	$buttonResult.onClick()
	
	$backButton.onClick()

}

SECTION com.example.vdm.SampleProjectActivity {
	$button1.onClick()
	$button2.onClick()
	$backButton.onClick()	
	$backButton.onClick()

}