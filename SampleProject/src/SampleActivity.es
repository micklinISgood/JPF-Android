
@intent1.setComponent("com.example.com.SampleProjectActivity")
startActivity(@intent1)

SECTION com.example.com.SampleProjectActivity {

ANY { $buttonPrint3.onClick(), $buttonPrint2.onClick() }

$buttonPrint1.onClick()
}

SECTION com.example.vdm.SampleProjectActivity {
 $button1.onClick()
 $button2.onClick()


}

