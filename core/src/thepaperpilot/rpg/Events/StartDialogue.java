package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Context;

public class StartDialogue extends Event {
    private String dialogue = "";

    public StartDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    public void run(Context context) {
        if (!context.dialogues.containsKey(dialogue)) return;
        thepaperpilot.rpg.UI.Dialogue dialogue = context.dialogues.get(this.dialogue);
        dialogue.chain = chain;
        dialogue.open(context);
    }
}
