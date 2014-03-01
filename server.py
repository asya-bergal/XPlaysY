from firebasin import Firebase
import ctypes
import time
import win32api
import win32con

# win32 direct input instead of ctypes
def PressKey(hexKeyCode):
    win32api.keybd_event(hexKeyCode, 0, 0, 0)
    time.sleep(0.2)
    win32api.keybd_event(hexKeyCode,0 ,win32con.KEYEVENTF_KEYUP ,0)

def onMsgReceive(msgData):
    # keycodes found in 
    # http://msdn.microsoft.com/en-us/library/windows/desktop/dd375731%28v=vs.85%29.aspx
    msg = msgData.val()['text'].lower()
    if msgData.val()['time'] < loadingTime:
        return
#    print msgData.val()['name'] + ": " + msg
    print msgData.val()
    if msg == u'left':
        PressKey(ord('A'))
    elif msg == u'right':
        PressKey(ord('D'))
    elif msg == u'up':
        PressKey(ord('W'))
    elif msg == u'down':
        PressKey(ord('S'))
    elif msg == u'a':
#        PressKey(0x41) # a key
        PressKey(ord('Z')) # z key
    elif msg == u'b':
#        PressKey(0x42) # b key
        PressKey(ord('X')) # x key
    elif msg == u'select':
        PressKey(ord('B'))
    elif msg == u'start':
        PressKey(ord('N'))

numUsers = 0
#while True:
msglist = Firebase('https://amber-fire-9230.firebaseio.com/msg')
#msglist.child('msg').on('child_added', onMsgReceive)
loadingTime = long(time.time() * 1000)
msglist.limit(40).on('child_added', onMsgReceive)
msglist.waitForInterrupt()
#    time.sleep(10)
