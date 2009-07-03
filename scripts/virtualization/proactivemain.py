import sys
import proactivelib
import vmware
import virtualbox
if sys.version_info[0] == 2 :
    import xenserver
import time

if len(sys.argv) >= 2 :
    logFile = sys.argv[1]
    print ("logging on ",logFile)
    out = open(logFile,"a")
    sys.stdout = out
    sys.stderr = out
else :
    print ("logging on standard output")
x = None

#you can register new Abstract_Runtime implementation here
proactivelib.Abstract_Runtime.addProvider(vmware.VMware_Runtime())
proactivelib.Abstract_Runtime.addProvider(virtualbox.Virtualbox_Runtime())
proactivelib.Abstract_Runtime.addProvider(xenserver.XenServer_Runtime())

#iterates registered Abstract_Runtime implementation to find the good
#environment.
while x == None:
    x = proactivelib.Abstract_Runtime.getInstance()
    if x == None :
        print ("getInstance from Abstract_Runtime returned None.")
        print ("Waiting 10s...")
        time.sleep(10)
x.start()
