package thepaperpilot.rpg.Components.Triggers;

import com.badlogic.gdx.math.Rectangle;
import thepaperpilot.rpg.Area;

public class EnterZoneComponent extends TriggerComponent {
    public final Rectangle bounds = new Rectangle();
    public final Area area;
    public boolean inside = false;
    public boolean repeatable = false;

    public EnterZoneComponent(Area area) {
        this.area = area;
    }
}
