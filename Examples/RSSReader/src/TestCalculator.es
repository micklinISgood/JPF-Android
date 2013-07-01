SECTION default {
	
	@urlInputStreamIntent.putExtraString("url","http://feeds.feedburner.com/Mobilecrunch.rss")
	@urlInputStreamIntent.putExtraString("file","src/input.rss")
	sendBroadcast(@urlInputStreamIntent)	
	
	@startIntent.setComponent("za.android.vdm.rssreader.TimelineActivity")
	startActivity(@startIntent)	
}

SECTION za.android.vdm.rssreader.TimelineActivity {
		registerChecklist("Update - wifi re-connected", {"networkStatusChange", "runUpdate","parsingFeed", "storeInDB", "updateListView"})
		sendBroadcast(@WifiOnIntent)
}