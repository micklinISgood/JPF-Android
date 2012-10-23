SECTION default {
@startIntent.setComponent("com.example.calculator.SimpleActivity")
startActivity(@startIntent)
ANY { 
	REPEAT 1 {$button1.onClick(), $button2.onClick()}, 
	REPEAT 1 {$button3.onClick(), $button4.onClick()}
}

}

