package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import thepaperpilot.rpg.Screens.Area;

public class AreaComponent implements Component {
    public Area area;

    public AreaComponent(Area area) {
        this.area = area;
    }
}
