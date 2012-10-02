SECTION default {
	@intent1.setComponent("com.example.com.SampleProjectActivity")
	startActivity(@intent1)
}

SECTION com.example.com.SampleProjectActivity {
	$buttonText3.onClick()
}

SECTION com.example.vdm.SampleProjectActivity {
}

SECTION com.example.com.MylistView {
}

SECTION com.example.com.ResultActivity {
	backButton
}

