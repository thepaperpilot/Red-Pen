package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class InventoryComponent implements Component {
    public Label description;
    public Table descTable;
    public Label error;
}
