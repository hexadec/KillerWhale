# KillerWhale
A collection of vendor specific battery optimizations on Android, and their component names

This project is aimed to provide a mostly-complete list of important brands that provide an additional battery optimization service for their users. As these are a pain for us, developers, and a workaround is often impossible, we need to have a list of packages and component names to start when a vendor as such is detected. If you ever detected a sudden or surprising stop of your background service, alarms, or broadcastreceivers on some devices or other undocumented behaviour, adding your app to the list of the vendors by starting the activities and asking the user to do the necessary steps may fix this issue.

The source file contains all important data to start a proper intent for the device.
0 in MinAPI or MaxAPI means unknown. The additional field (true/false) means the intent was tested on a physical device in the given API range and was confirmed to be working as intended. 

Compare vendor names using equalsIgnoreCase!

If you have anything to add or correct please open an issue!
