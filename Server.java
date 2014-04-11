import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.firebase.client.*;

public class Server {
	
	public static void main(String args[])
	{
		final long startTime = System.currentTimeMillis(); //start time of current session
		final HashMap<String, Integer> keyMap = new HashMap<String, Integer>();  //map of inputs to key presses

		keyMap.put("left",	KeyEvent.VK_A); //these mappings can be changed arbitrarily but should match VBA-M mappings
		keyMap.put("right",	KeyEvent.VK_D);
		keyMap.put("up",	KeyEvent.VK_W);
		keyMap.put("down",	KeyEvent.VK_S);
		keyMap.put("a",		KeyEvent.VK_R);
		keyMap.put("b",		KeyEvent.VK_T);
		keyMap.put("start",	KeyEvent.VK_V);
		keyMap.put("select",KeyEvent.VK_B);
		
		String url = "https://x-plays-y.firebaseio.com/msg";
		Firebase chatsRef = new Firebase(url); //access the firebase at this url
		
		Thread game = new GameThread();
		game.start();
		
		chatsRef.addChildEventListener(new ChildEventListener() {

			public void onChildAdded(DataSnapshot msgData, String arg1) {
				GenericTypeIndicator<Map<String, Object>> t = new GenericTypeIndicator<Map<String, Object>>() {};
				Map<String, Object> data = msgData.getValue(t);
				
				long time = (Long)(data.get("time")); //only use chat inputs after the start time of the session
				if(time < startTime)
					return;
				
				String msg = (String)(data.get("text")); //get chat input and clean it up
				System.out.println(msg);
				msg = msg.toLowerCase();
				msg = msg.trim();
				
				if(keyMap.containsKey(msg)){ //if the input is a valid key press, add it to the queue
					try {
                        GameThread.keyQueue.put(keyMap.get(msg));
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
				}
			}

			public void onChildChanged(DataSnapshot arg0, String arg1) {} //obligatory firebase listener methods we don't care about
			public void onChildMoved(DataSnapshot arg0, String arg1) {}
			public void onChildRemoved(DataSnapshot arg0) {}
			public void onCancelled(FirebaseError arg0) {}

		});		
	}
}

class GameThread extends Thread //goes through the queue, pressing the keys
{
	public static BlockingQueue<Integer> keyQueue = new LinkedBlockingQueue<Integer>(); //queue of key presses
	
	public void run()
	{
		Robot robot = null;
		try {
			robot = new Robot(); //make new key-pressing robot
		} 
		catch (AWTException e1) { e1.printStackTrace();}	
		
		while (true) //keep processing key presses out of the queue
		{	
//			try{
//				Thread.sleep(10);
//			}catch (InterruptedException e) { e.printStackTrace(); }
			
			if (keyQueue.size() == 0) //do nothing if there's nothing in the queue
				continue;
		
			int keyEvent = 0;
            try {
                keyEvent = keyQueue.take(); //get the first key event out of the queue
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }	

			robot.keyPress(keyEvent); //press corresponding key
			//System.out.println("key pressed");
			
			try {
				Thread.sleep(Math.max(1,100 - keyQueue.size())); //wait less between key presses if the size of the queue is larger
			} catch (InterruptedException e) {}
			
			robot.keyRelease(keyEvent); //release corresponding key after some wait time
		}
	}
}