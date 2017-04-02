package com.gobs.assets;

import com.gobs.map.Layer;
import com.badlogic.gdx.Gdx;
import com.gobs.map.LayerCell.LayerCellType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class DungeonFactory {
    public static Layer loadMap(int worldWidth, int worldHeight, String res) throws IOException {
        Layer mapLayer = new Layer(worldWidth, worldHeight, Layer.LayerType.MAP_LAYER);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Gdx.files.internal(res).read(), Charset.forName("UTF-8")))) {
            String line;

            List<String> lines = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                lines.add(line);
            }
            Collections.reverse(lines);

            for (int j = 0; j < lines.size(); j++) {
                int i = 0;
                for (char c : lines.get(j).toCharArray()) {
                    if (c == 'w') {
                        mapLayer.setCell(i, j, LayerCellType.WALL, true);
                    } else if (c == '.') {
                        mapLayer.setCell(i, j, LayerCellType.FLOOR, false);
                    } else if (c == '@') {
                        mapLayer.setCell(i, j, LayerCellType.STAIRS, false);
                    }
                    i += 1;
                }
            }
        }

        return mapLayer;
    }
}
