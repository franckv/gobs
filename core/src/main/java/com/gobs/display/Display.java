package com.gobs.display;

import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class Display {
     protected int width;
     protected int height;
     protected float ratio;
     protected Viewport viewPort;
     
     public Display(int width, int height) {
         this.width = width;
         this.height = height;
     }
     
     public int getWidth() {
         return width;
     }
     
     public int getHeight() {
         return height;
     }
     
     public Viewport getViewPort() {
         return viewPort;
     }
     
     public void update(int width, int height) {
         viewPort.update(width, height);
     }
}
