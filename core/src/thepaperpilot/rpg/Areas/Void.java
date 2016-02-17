package thepaperpilot.rpg.Areas;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.UI.Dialogue;

public class Void extends Context.ContextPrototype {
    public Void() {
        /* Dialogues */
        Dialogue.Line line1 = new Dialogue.Line("Thank you for playing this! This is a work in progress game. I'm pretty bad at writing, so I've mostly been working on the engine. The dialogue is especially terrible. Sorry. But hopefully you'll like at least part of the story.", "thepaperpilot");
        Dialogue.Line line2 = new Dialogue.Line("The art is licensed CC0 from kenney.itch.io. The music is licensed CC BY from soundcloud.com/eric-skiff", "thepaperpilot");
        Dialogue.Line line3 = new Dialogue.Line("Also, feedback is welcome and appreciated!", "thepaperpilot");
        line3.events = new Event[]{new Event(Event.Type.CHANGE_CONTEXT, "intro")};
        Dialogue welcomeDialogue = new Dialogue("welcome", new Dialogue.Line[]{line1, line2, line3});

        dialogues = new Dialogue[]{welcomeDialogue};
    }

    public Context getContext() {
        Context context = new Context(this);
        new Event(Event.Type.DIALOGUE, "welcome").run(context);
        return context;
    }
}
