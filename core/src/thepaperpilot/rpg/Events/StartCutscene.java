package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Systems.PlayerSystem;

public class StartCutscene extends Event {
    @Override
    public void run(Context context) {
        if (context.engine.getSystem(PlayerSystem.class) != null)
            context.engine.getSystem(PlayerSystem.class).setProcessing(false);
        runNext(context);
    }
}
