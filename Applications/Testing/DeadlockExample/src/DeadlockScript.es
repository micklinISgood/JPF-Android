SECTION default {
	@intent1.setComponent("com.example.jpf.DeadlockActivity")
	startActivity(@intent1)
}

SECTION com.example.jpf.DeadlockActivity {
	$button1.onClick()
	//$button2.onClick()
}

