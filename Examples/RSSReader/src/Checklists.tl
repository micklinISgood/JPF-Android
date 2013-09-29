// name : condition => verify_list

[Checklists]
updateInitiated: update, !alreadyRunning, !WifiDown, !batteryLow, getUpdates =>  finishUpdate;
getFeedUpdates : parseFeed, storeInDB, updateListView =>  loadFromDB;

updateWifiDown: update, WifiDown => !runUpdate, notifyWifiDown;
updateBatteryLow: update , batteryLow =>!runUpdate, notifyBatteryLow;
updateAlreadyRunning: update , alreadyRunning =>!runUpdate, notifyAlreadyRunning;

wifiUpBroadcast: receiveNetworkUpdate, networkConnected => setTimer, triggerTimer;
timer: triggerTimer => update, setNextTimer ;