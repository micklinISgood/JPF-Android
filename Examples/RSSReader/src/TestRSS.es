SECTION default {
	
	@urlInputStreamIntent.putExtraString("url","http://feeds.feedburner.com/Mobilecrunch.rss")
	@urlInputStreamIntent.putExtraString("file","src/input.rss")
	sendBroadcast(@urlInputStreamIntent)
	
	@startIntent.setComponent("za.android.vdm.rssreader.TimelineActivity")
	startActivity(@startIntent)	
}

SECTION za.android.vdm.rssreader.TimelineActivity {
	ANY{ 
		GROUP{
			device.setWifi("ON")
    		device.setBattery("100%")
		},
		GROUP {
			device.setWifi("OFF")
			device.setBattery("100%")
		},
		GROUP {
		device.setWifi("ON")
		device.setBattery("1%")
		}
	}
	$buttonRefresh.onClick()
	//$buttonRefresh.onClick()		
}