SECTION default {
	@intent1.setComponent("com.example.MainActivity")
	startActivity(@intent1)
}

SECTION com.example.MainActivity {
	$buttonStart.onClick()
}