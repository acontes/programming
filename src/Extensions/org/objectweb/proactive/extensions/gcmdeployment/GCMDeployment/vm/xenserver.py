#!/usr/bin/python

'''
Created on 5 mai 2009

@author: jmguilla
'''
import sys
import proactivelib
import XenAPI
import re
import traceback
from subprocess import *

#you have to set the following info to be able to
#bootstrap proactive runtime
xenServerAddress = "http://192.168.1.166"
xenServerUserID = "root"
XenServerUserPWD = "root123"

class XenServer_Runtime( proactivelib.Abstract_Runtime ):

    #Here are developpers data
    __proacRTKey = "proac."
    __proacBootstrapURL = "burl"
    __proacHardwareAddress = "ha"

    def __getMacAddress(self):
        """getMacAddress returns an array filled with every
        detected NIC's mac address on the current computer"""
        proc = None
        if sys.platform == 'win32':
            proc = Popen( args = "ipconfig /all", stdout = PIPE, stderr = PIPE)
        else:
            proc = Popen( args = "/sbin/ifconfig", stdout = PIPE, stderr = PIPE)
        output = proc.communicate()[0]
        res = re.findall("(?:[0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}",output)
        return res

    def __getBoostrapURL(self):
        """getBoostrapURL tries to connect to the XenServer manager
        to get the bootstrap url from the boostrapServlet that will
        be used to get every useful information to bootstrap
        proactive runtime"""
        session = XenAPI.Session(xenServerAddress)
        session.xenapi.login_with_password(xenServerUserID, XenServerUserPWD)
        vms = session.xenapi.VM.get_all()
        macs = self.__getMacAddress()
        print("vms:",vms)
        print("macs:",macs)
        url = None
        for i in range(len(vms)):
            vm = vms[i]
            print("vm:",vm)
            data = session.xenapi.VM.get_xenstore_data(vm)
            for j in range(len(macs)):
                mac = macs[j]
                print("mac:",mac)
                print("data:",data)
                key = self.__proacRTKey + self.__proacHardwareAddress
                try:
                    remoteMac = data[key].lower().strip()
                    mac = mac.lower().strip()
                    if remoteMac.startswith(mac) and remoteMac.endswith(mac):
                        try:
                            url = data[self.__proacRTKey + self.__proacBootstrapURL]
                            return url
                        except KeyError:
                            traceback.extract_stack()
                            print ("unable to get bootstrapURL")
                            return None
                except KeyError:
                    print ("invalid key: ", key, " supplied.")
        return url

    def isOk(self):
        """This method is used to check if the current environment
        matches xenserver requirements to be used. If it is the case, an
        instance of VMware_Runtime will be returned"""
        try :
            url = self.__getBoostrapURL()
            if url != None :
                return self
            else :
                return None
        except :
            print ("An exception occured while testing xenserver env")
            return None

    def start(self):
        """Exctracts bootstrapURL and calls
        _start from Abstract_Runtime"""
        output = self.__getBoostrapURL()
        self._start(output)
