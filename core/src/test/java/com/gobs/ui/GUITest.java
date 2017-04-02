package com.gobs.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gobs.Config;
import com.gobs.assets.TileFactory;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class GUITest {
    private GdxGUI gui;

    @Before
    public void setUp() {
        Config config = new Config(null);
        gui = new GdxGUI(new DisplayManager(1024, 768, 32), new TileFactory(config, 32), new SpriteBatch());
    }

    @Test
    public void testLayout() {
        gui.setMargin(2, 3);
        gui.setSpacing(10);

        gui.setFont("small");
        System.out.println(gui.getLabelHeight("AAA") + "/" + gui.getLabelWidth("AAA"));

        gui.createSection("Line1", GUILayout.FlowDirection.HORIZONTAL);
        {
            gui.createSection("Column11", GUILayout.FlowDirection.VERTICAL);
            {
                gui.createSection("Line111", GUILayout.FlowDirection.HORIZONTAL);
                {
                    gui.createSection("Column1111", GUILayout.FlowDirection.VERTICAL);
                    gui.Label("AAA");
                    gui.Label("AAA");
                    gui.Label("AAAAAA");
                    gui.Label("AAA");
                    gui.endSection();

                    gui.createSection("Column1112", GUILayout.FlowDirection.VERTICAL);
                    gui.Label("BBB");
                    gui.Label("BB");
                    gui.Label("BBB");
                    gui.Label("BBB");
                    gui.endSection();
                }
                gui.endSection();

                gui.createSection("Line112", GUILayout.FlowDirection.HORIZONTAL);
                gui.Label("CCC");
                gui.endSection();

                gui.createSection("Line113", GUILayout.FlowDirection.HORIZONTAL);
                gui.Label("DDD");
                gui.Label("DDD");
                gui.Label("DDD");
                gui.Label("DDD");
                gui.endSection();
            }
            gui.endSection(); // Column11

            gui.createSection("Column12", GUILayout.FlowDirection.VERTICAL);
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.Label("EEE");
            gui.endSection();
        }
        gui.endSection(); // Line1
    }
}
