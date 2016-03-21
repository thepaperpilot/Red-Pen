package thepaperpilot.rpg.Chapters;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.ChangeContext;
import thepaperpilot.rpg.Events.StartDialogue;

public class Void extends Context.ContextPrototype {
    public Void() {
        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Thank you for playing this! This is a work in progress game. I'm pretty bad at writing, so I've mostly been working on the engine. The dialogue is especially terrible. Sorry. But hopefully you'll like at least part of the story.", "thepaperpilot");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("The art is licensed CC0 from kenney.itch.io. The music is licensed CC BY from soundcloud.com/eric-skiff", "thepaperpilot");
        thepaperpilot.rpg.UI.Dialogue.Line line3 = new thepaperpilot.rpg.UI.Dialogue.Line("Also, feedback is welcome and appreciated!", "thepaperpilot");
        line3.events.add(new ChangeContext("intro"));
        thepaperpilot.rpg.UI.Dialogue welcomeDialogue = new thepaperpilot.rpg.UI.Dialogue("welcome", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3});

        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{welcomeDialogue};
    }

    public Context getContext() {
        Context context = new Context(this);
        context.init();
        context.events.add(new StartDialogue("welcome"));
        return context;
    }
}
