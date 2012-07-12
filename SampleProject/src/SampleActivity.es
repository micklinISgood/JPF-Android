@intent1.setComponent("com.example.com.SampleProjectActivity")

startActivity(@intent1)

REPEAT 2 {
	ANY{NONE,$buttonPrint1.onClick()}
}


$buttonPrint2.onClick()


$button2.onClick()
