package thepaperpilot.rpg.Components.Triggers;

import com.badlogic.gdx.math.Rectangle;
import thepaperpilot.rpg.Screens.Area;

public class LeaveZoneComponent extends TriggerComponent {
    public final Rectangle bounds = new Rectangle();
    public final Area area;
    public boolean inside = false;
    public boolean repeatable = false;

    public LeaveZoneComponent(Area area) {
        this.area = area;
    }
}
