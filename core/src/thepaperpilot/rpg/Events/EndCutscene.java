package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Systems.PlayerControlledSystem;

public class EndCutscene extends Event {
    public void run(Context context) {
        if (context.engine.getSystem(PlayerControlledSystem.class) != null)
            context.engine.getSystem(PlayerControlledSystem.class).setProcessing(true);
        runNext(context);
    }
}
