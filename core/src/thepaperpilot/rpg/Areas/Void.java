package thepaperpilot.rpg.Areas;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.UI.Dialogue;

public class Void extends Context.ContextPrototype {
    public Void() {
        /* Dialogues */
        Dialogue.DialoguePrototype welcomeDialogue = new Dialogue.DialoguePrototype();
        welcomeDialogue.name = "welcome";
        Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
        line1.name = "thepaperpilot";
        line1.message = "Thank you for playing this! This is a work in progress game. I'm pretty bad at writing, so I've mostly been working on the engine. The dialogue is especially terrible. Sorry. But hopefully you'll like at least part of the story.";
        Dialogue.LinePrototype line2 = new Dialogue.LinePrototype();
        line2.name = "thepaperpilot";
        line2.message = "Also, feedback is welcome and appreciated!";
        line2.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.CHANGE_CONTEXT, "intro")};
        welcomeDialogue.lines = new Dialogue.LinePrototype[]{line1, line2};

        dialogues = new Dialogue.DialoguePrototype[]{welcomeDialogue};
    }

    public Context getContext() {
        Context context = new Context(this);
        new Event(new Event.EventPrototype(Event.Type.DIALOGUE, "welcome"), context).run();
        return context;
    }
}
