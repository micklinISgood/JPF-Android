SECTION default {

	registerChecklist("main1", {"MainOnCreate","checkNetworkState")
	registerChecklist("main2", {"ConnectionChange", "!ConnectionChange"});
	registerChecklist("main3", {"NormalReceiver", "!NormalReceiver"});
	
	@startIntent.setComponent("za.vdm.main.MainActivity")
	startActivity(@startIntent)
}

SECTION za.vdm.main.MainActivity {
	//test inner class BR registered dynamically component not specified

	
	sendBroadcast(@WifiConnectedIntent)
	$button1.onClick()	
	unregisterChecklist("main1")


	sendBroadcast(@WifiDisconnectedIntent)
	unregisterChecklist("main2");
	unregisterChecklist("main3");
	$button1.onClick()	
	
}

SECTION za.vdm.main.SecondActivity {
	$button1.onClick()	
	$button1.onClick()	
	$backButton.onClick()
}


