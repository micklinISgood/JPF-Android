[Checklists]

//update succeeds
updateSuccess:  update, checkRunning, !alreadyRunning, 
	 checkBattery, !batteryLow, checkWifi, !WifiDown 
	=> runUpdate, finishUpdate;

//update does not succeed
updateWifiDown: update, checkWifi, WifiDown => !runUpdate, notifyUser;

updateBatteryLow: update, checkBattery, batteryLow => !runUpdate, notifyUser;

updateAlreadyRunning: update, checkRunning, alreadyRunning => !runUpdate, notifyUser;

//updates 
getFeedUpdates : parseFeed  => storeInDB, notifyTimelineActivity, loadFromDB, updateListView;








