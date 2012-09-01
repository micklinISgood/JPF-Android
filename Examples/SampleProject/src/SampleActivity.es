SECTION default {
	@intent1.setComponent("com.example.com.SampleProjectActivity")
	startActivity(@intent1)
}
SECTION com.example.com.SampleProjectActivity {
	ANY {$buttonPrint3.onClick(), $buttonText2.onClick(), $buttonPrint4.onClick()}
	$buttonText3.onClick()
}

SECTION com.example.vdm.SampleProjectActivity {
	$button1.onClick()
	$button2.onClick()
}

SECTION com.example.com.MylistView {

}

