# SystemInfoMonitor
This is a android app to monite phone system info (cpu,memory,battery...) in background

# Features
### Record SystemInfo
App can record phone system infomation in background with given interval.This can be used for phone stability test or stress test.
Support Info:
-  CPU (get every cpu core's usage and frequency)
-  Memory 
  For System :include "Buffers","SwapCached","SwapTotal","SwapFree","Total RAM", "Free RAM", "Used RAM",
			"Lost RAM", "ZRAM","Cached PSS(KB)", "Used PSS","APP Memory", "Cached Free",
			"Free", "Free Memory","Firmware Memory","Graphics","GL"
  For process : "PSS","Process","Pid"
-  Battery level
-  Temperature

### Export data
App can export the data with .csv format,so user can analyze data easily
