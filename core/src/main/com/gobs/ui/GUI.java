package com.gobs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.GameState;
import com.gobs.input.InputMap;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 */
public class GUI implements Disposable {
    public enum AlignH {
        NONE, LEFT, RIGHT, CENTER
    }

    public enum AlignV {
        NONE, TOP, BOTTOM, CENTER
    }

    public enum FlowDirection {
        LEFT, RIGHT, UP, DOWN
    }

    public enum FontSize {
        SMALL, MEDIUM, LARGE
    }

    private class GUIParam {
        private GUIParam() {
            this.flow = FlowDirection.RIGHT;
            this.alignh = AlignH.NONE;
            this.alignv = AlignV.NONE;

            this.marginX = 0;
            this.marginY = 0;
            this.spacing = 0;
            this.posX = 0;
            this.posY = 0;
        }

        private GUIParam(GUIParam copy) {
            this.flow = copy.flow;
            this.alignh = copy.alignh;
            this.alignv = copy.alignv;
            this.posX = copy.posX;
            this.posY = copy.posY;
            this.marginX = copy.marginX;
            this.marginY = copy.marginY;
            this.spacing = copy.spacing;
        }

        private FlowDirection flow;
        private AlignH alignh;
        private AlignV alignv;

        private float posX, posY;
        private int marginX, marginY;
        private int spacing;
    }

    private GUIParam param;
    private Deque<GUIParam> states;

    private Batch batch;
    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;
    private FontSize fontSize;
    private Color color;
    private TextureRegion frame;

    public GUI(Batch batch) {
        this.batch = batch;

        smallFont = GameState.getFontManager().getFont(16);
        mediumFont = GameState.getFontManager().getFont(24);
        largeFont = GameState.getFontManager().getFont(30);

        color = Color.GREEN;
        fontSize = FontSize.SMALL;

        frame = GameState.getTileManager().getFrame();

        param = new GUIParam();

        states = new ArrayDeque<>();
        states.addFirst(param);
    }

    public boolean AcceptInput(InputMap inputMap) {
        return false;
    }
    
    public boolean Button() {
        return false;
    }

    public void Label(String text) {
        Label(text, fontSize);
    }

    public void Label(String text, FontSize size) {
        BitmapFont font = getFont(size);

        font.setColor(color);

        GlyphLayout glayout = new GlyphLayout(font, text);

        float h = glayout.height;
        float w = glayout.width;

        font.draw(batch, glayout, getX(w), h + getY(h));

        updatePosition(w, h);
    }

    public void Box(float w, float h) {
        batch.draw(frame, getX(w), getY(h), w, h);

        updatePosition(w, h);
    }

    private BitmapFont getFont(FontSize size) {
        switch (size) {
            case SMALL:
                return smallFont;
            case MEDIUM:
                return mediumFont;
            case LARGE:
                return largeFont;
        }

        return smallFont;
    }

    private float getX(float width) {
        float dx = 0;

        switch (param.alignh) {
            case LEFT:
                dx = param.marginX;
                break;
            case RIGHT:
                dx = -param.marginX - width;
                break;
            case CENTER:
                dx = -width / 2;
                break;
        }

        return param.posX + dx;
    }

    private float getY(float height) {
        float dy = 0;

        switch (param.alignv) {
            case BOTTOM:
                dy = param.marginY;
                break;
            case TOP:
                dy = -param.marginY - height;
                break;
            case CENTER:
                dy = -height / 2;
        }

        return param.posY + dy;
    }

    private void updatePosition(float width, float height) {
        switch (param.flow) {
            case UP:
                param.posY += param.spacing + height;
                break;
            case DOWN:
                param.posY -= param.spacing + height;
                break;
            case LEFT:
                param.posX -= param.spacing + width;
                break;
            case RIGHT:
                param.posX += param.spacing + width;
                break;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public void setFlow(FlowDirection flow) {
        param.flow = flow;
    }

    public void setAlignV(AlignV alignv, float h) {
        param.alignv = alignv;

        switch (alignv) {
            case BOTTOM:
                param.posY = 0;
                break;
            case TOP:
                param.posY = GameState.getOverlayViewport().getWorldHeight();
                break;
            case CENTER:
                param.posY = GameState.getOverlayViewport().getWorldHeight() / 2;
                break;
            default:
                break;
        }
        param.posY += h;
    }

    public void setAlignH(AlignH alignh, float w) {
        param.alignh = alignh;

        switch (alignh) {
            case LEFT:
                param.posX = 0;
                break;
            case RIGHT:
                param.posX = GameState.getOverlayViewport().getWorldWidth();
                break;
            case CENTER:
                param.posX = GameState.getOverlayViewport().getWorldWidth() / 2;
                break;
            default:
                break;
        }
        param.posX += w;
    }

    public void setAlign(AlignH alignh, AlignV alignv, float w, float h) {
        setAlignV(alignv, h);
        setAlignH(alignh, w);
    }

    public void setAlign(AlignH alignh, AlignV alignv) {
        setAlign(alignh, alignv, 0, 0);
    }

    public void setMargin(int margin) {
        param.marginX = margin;
        param.marginY = margin;
    }

    public void setMargin(int marginX, int marginY) {
        param.marginX = marginX;
        param.marginY = marginY;
    }

    public void setSpacing(int spacing) {
        param.spacing = spacing;
    }

    public float getPosX() {
        return param.posX;
    }

    public float getPosY() {
        return param.posY;
    }

    public void setPos(float x, float y) {
        setAlign(AlignH.NONE, AlignV.NONE);
        param.posX = x;
        param.posY = y;
    }

    public void translatePos(float dx, float dy) {
        param.posX += dx;
        param.posY += dy;
    }

    public void pushState() {
        param = new GUIParam(param);
        states.addFirst(param);
    }

    public void popState() {
        states.removeFirst();
        param = states.getFirst();
    }

    public void showLayout(boolean b) {
        if (b) {
            showLayoutH();
        } else {
            showLayoutV();
        }
    }

    public void showLayoutV() {
        setAlign(AlignH.LEFT, AlignV.TOP);
        setFlow(FlowDirection.DOWN);
        Label("TL1");
        Label("TL2");
        Label("TL3");

        setAlign(AlignH.LEFT, AlignV.CENTER);
        setFlow(FlowDirection.UP);
        Label("CL1");
        Label("CL2");
        Label("CL3");

        setAlign(AlignH.RIGHT, AlignV.TOP);
        setFlow(FlowDirection.DOWN);
        Label("TR1");
        Label("TR2");
        Label("TR3");

        setAlign(AlignH.CENTER, AlignV.TOP);
        setFlow(FlowDirection.DOWN);
        Label("TC1");
        Label("TC2");
        Label("TC3");

        setAlign(AlignH.RIGHT, AlignV.CENTER);
        setFlow(FlowDirection.UP);
        Label("CR1");
        Label("CR2");
        Label("CR3");

        setAlign(AlignH.LEFT, AlignV.BOTTOM);
        setFlow(FlowDirection.UP);
        Label("BL1");
        Label("BL2");
        Label("BL3");

        setAlign(AlignH.CENTER, AlignV.BOTTOM);
        setFlow(FlowDirection.UP);
        Label("BC1");
        Label("BC2");
        Label("BC3");

        setAlign(AlignH.RIGHT, AlignV.BOTTOM);
        setFlow(FlowDirection.UP);
        Label("BR1");
        Label("BR2");
        Label("BR3");

        setAlign(AlignH.CENTER, AlignV.CENTER);
        setFlow(FlowDirection.UP);
        Label("C1");
        Label("C2");
        Label("C3");

    }

    public void showLayoutH() {
        setAlign(AlignH.LEFT, AlignV.TOP);
        setFlow(FlowDirection.RIGHT);
        Label("TL1");
        Label("TL2");
        Label("TL3");

        setAlign(AlignH.LEFT, AlignV.CENTER);
        setFlow(FlowDirection.RIGHT);
        Label("CL1");
        Label("CL2");
        Label("CL3");

        setAlign(AlignH.RIGHT, AlignV.TOP);
        setFlow(FlowDirection.LEFT);
        Label("TR1");
        Label("TR2");
        Label("TR3");

        setAlign(AlignH.CENTER, AlignV.TOP);
        setFlow(FlowDirection.LEFT);
        Label("TC1");
        Label("TC2");
        Label("TC3");

        setAlign(AlignH.RIGHT, AlignV.CENTER);
        setFlow(FlowDirection.LEFT);
        Label("CR1");
        Label("CR2");
        Label("CR3");

        setAlign(AlignH.LEFT, AlignV.BOTTOM);
        setFlow(FlowDirection.RIGHT);
        Label("BL1");
        Label("BL2");
        Label("BL3");

        setAlign(AlignH.CENTER, AlignV.BOTTOM);
        setFlow(FlowDirection.RIGHT);
        Label("BC1");
        Label("BC2");
        Label("BC3");

        setAlign(AlignH.RIGHT, AlignV.BOTTOM);
        setFlow(FlowDirection.LEFT);
        Label("BR1");
        Label("BR2");
        Label("BR3");

        setAlign(AlignH.CENTER, AlignV.CENTER);
        setFlow(FlowDirection.LEFT);
        Label("C1");
        Label("C2");
        Label("C3");
    }

    public void showBoxes() {
        setFlow(GUI.FlowDirection.RIGHT);
        setAlign(GUI.AlignH.LEFT, GUI.AlignV.TOP);
        Box(200, 100);

        setAlign(GUI.AlignH.LEFT, GUI.AlignV.BOTTOM);
        Box(200, 100);

        setAlign(GUI.AlignH.LEFT, GUI.AlignV.CENTER);
        Box(200, 100);

        setFlow(GUI.FlowDirection.LEFT);
        setAlign(GUI.AlignH.RIGHT, GUI.AlignV.TOP);
        Box(200, 100);

        setAlign(GUI.AlignH.RIGHT, GUI.AlignV.BOTTOM);
        Box(200, 100);

        setAlign(GUI.AlignH.RIGHT, GUI.AlignV.CENTER);
        Box(200, 100);

        setFlow(GUI.FlowDirection.DOWN);
        setAlign(GUI.AlignH.CENTER, GUI.AlignV.TOP);
        Box(200, 100);

        setFlow(GUI.FlowDirection.LEFT);
        setAlign(GUI.AlignH.CENTER, GUI.AlignV.CENTER);
        Box(200, 100);

        setFlow(GUI.FlowDirection.UP);
        setAlign(GUI.AlignH.CENTER, GUI.AlignV.BOTTOM);
        Box(200, 100);
    }

    public void showCenters() {
        ShapeRenderer render = new ShapeRenderer();

        Gdx.gl20.glLineWidth(1);
        render.setProjectionMatrix(GameState.getOverlayCamera().combined);
        render.begin(ShapeRenderer.ShapeType.Line);
        render.setColor(Color.RED);
        render.line(Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
        render.line(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
        render.end();
        render.dispose();
    }

    @Override
    public void dispose() {
        smallFont.dispose();
        mediumFont.dispose();
        largeFont.dispose();
    }
}
