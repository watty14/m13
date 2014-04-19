package com.example.wallt.presenters;

import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

public class GestureListener implements OnTouchListener {

	private float downX, downY;
	private boolean discard = false;

	public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                discard = false;
                return !(v instanceof ListView || v instanceof GridView);
            }
            case MotionEvent.ACTION_MOVE: {
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;
                if (Math.abs(deltaY) > 40) {
                    discard = true;
                }

                if(!discard && Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 90) {
                    if(deltaX < 0) {
                        onSwipeRight();
                        return true;
                    }
                    if(deltaX > 0) {
                        onSwipeLeft();
                        return true;
                    }
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                onTouchUp(event);
                return false;
            }
		default:
			break;
        }
        return false;
    }

	public void onTouchUp(MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onSwipeLeft() {
		// TODO Auto-generated method stub
		
	}

	public void onSwipeRight() {
		// TODO Auto-generated method stub
		
	}
}