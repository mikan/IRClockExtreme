# coding: UTF-8

import sys
import urllib
import urllib2

argvs = sys.argv
argc = len(argvs)
response = {}
url = "http://localhost:10000/"
headers = {
    "pragma":"no-cache",
}

if argc < 2:
    print "Usage: cmd.py home localhost:10000"
    quit()
if argc == 3:
    if ":" in argvs[2]:
        url = "http://" + argvs[2] + "/"
    else:
        url = "http://" + argvs[2] + ":10000/"
print "URL=" + url
try:
    params = urllib.urlencode({"command":argvs[1]})
    req = urllib2.Request(url, params, headers)
    res = urllib2.urlopen(req)
    response["body"] = res.read()
    response["headers"] = res.info().dict
except urllib2.URLError, e:
    print e.reason
    quit()

print response["body"]
