// name : condition => verify_list

[Checklists]
//update succeeds
startUpdate1: updateButtonPressed, !alreadyRunning, !WifiDown, !batteryLow =>  runUpdate;
startUpdate2: updateButtonPressed => updateButtonText;
runUpdate:    runUpdate => setRunning, getUpdate, finishUpdate;

//update does not succeed
updateWifiDown: updateButtonPressed,checkWifi, WifiDown => !runUpdate;
updateBatteryLow: updateButtonPressed, checkBattery, batteryLow => !runUpdate;
updateAlreadyRunning: updateButtonPressed, alreadyRunning =>!runUpdate;

//updating list view
getFeedUpdates1 : parseFeedItems, storeInDB  => notifyTimelineActivity, loadFromDB, updateListView;
getFeedUpdates2 : parseFeedItems, !storeInDB  => !notifyTimelineActivity;

wifiUpBroadcast: receiveNetworkUpdate, networkConnected => setTimer, triggerTimer;
timer: triggerTimer => update, setNextTimer;