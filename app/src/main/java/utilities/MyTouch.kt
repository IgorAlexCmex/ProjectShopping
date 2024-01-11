package utilities

import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet.Motion

class MyTouch: View.OnTouchListener {
    var positionX=0.0f
    var positionY=0.0f
    override fun onTouch(view: View, event: MotionEvent?): Boolean {
      when(event?.action){
          MotionEvent.ACTION_DOWN->{
            positionX=view.x-event.rawX
            positionY=view.y-event.rawY
          }
          MotionEvent.ACTION_MOVE->{
             view.x=positionX+event.rawX
              view.y=positionY+event.rawY
          }
      }
        return true
    }

}