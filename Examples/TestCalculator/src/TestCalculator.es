SECTION default {
@startIntent.setComponent("com.example.calculator.SimpleActivity")
startActivity(@startIntent)


}

SECTION com.example.calculator.SimpleActivitys {
$button4.onClick()
ANY {$button[0-9].onClick()}


}

SECTION com.example.calculator.SimpleActivity {
	ANY { 
		REPEAT 1 {ANY {  	
					REPEAT 1{$button1.onClick()}, 
					REPEAT 1{$button2.onClick()},
		 			REPEAT 1{$button3.onClick()}
		}
	ANY {  	
					REPEAT 1{$button1.onClick()}, 
					REPEAT 1{$button2.onClick()},
		 			REPEAT 1{$button3.onClick()}
		}
}
		
		REPEAT 1 {
			$button0.onClick()
		}
	}
	$button9.onClick()
	
}

