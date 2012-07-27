
@intent1.setComponent("com.example.com.SampleProjectActivity")
startActivity(@intent1)

SECTION com.example.com.SampleProjectActivity {
 $buttonPrint1.onClick()
 REPEAT 2 {
	ANY{$buttonPrint2.onClick(),$buttonPrint1.onClick()}
	}
}


SECTION com.example.vdm.SampleProjectActivity {
 $button1.onClick()
}

