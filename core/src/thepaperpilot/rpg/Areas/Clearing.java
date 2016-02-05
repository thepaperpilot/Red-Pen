package thepaperpilot.rpg.Areas;

import thepaperpilot.rpg.*;

public class Clearing extends Area.AreaPrototype {
    private static final Clearing instance = new Clearing();

    public Clearing() {
        /* Events */
        final Event.EventPrototype talk = new Event.EventPrototype();
        talk.type = "DIALOGUE";
        talk.attributes.put("target", "talker");

        final Event.EventPrototype move = new Event.EventPrototype();
        move.type = "MOVE_ENTITY";
        move.attributes.put("target", "talker");
        move.attributes.put("x", "" + 20 * Main.TILE_SIZE);
        move.attributes.put("y", "" + 22 * Main.TILE_SIZE);
        move.wait = 4;

        final Event.EventPrototype look = new Event.EventPrototype();
        look.type = "MOVE_CAMERA";
        look.attributes.put("x", "" + 6 * Main.TILE_SIZE);
        look.attributes.put("y", "" + 3 * Main.TILE_SIZE);
        look.attributes.put("zoom", "" + .75f);

        final Event.EventPrototype allPapers = new Event.EventPrototype();
        allPapers.type = "DIALOGUE";
        allPapers.attributes.put("target", "allPapers");

        final Event.EventPrototype lastPaper = new Event.EventPrototype();
        lastPaper.type = "DIALOGUE";
        lastPaper.attributes.put("target", "lastPaper");

        final Event.EventPrototype removePaper = new Event.EventPrototype();
        removePaper.type = "SET_ENTITY_VISIBILITY";
        removePaper.attributes.put("target", "pile");
        removePaper.attributes.put("visible", "false");

        Event.EventPrototype release = new Event.EventPrototype();
        release.type = "RELEASE_CAMERA";
        release.wait = 2;

        /* Entities */
        Entity.EntityPrototype talkerEntity = new Entity.EntityPrototype("talker", "person9", 6 * Main.TILE_SIZE, 3 * Main.TILE_SIZE, true) {
            public void onTouch(Entity entity) {
                new Event(talk, entity.area).run();
                new Event(move, entity.area).run();
                new Event(look, entity.area).run();
            }
        };

        Entity.EntityPrototype pile = new Entity.EntityPrototype("pile", "pile", 24 * Main.TILE_SIZE, 12 * Main.TILE_SIZE, true) {
            int stones = 132;
            public void onTouch(Entity entity) {
                if (stones == 132) {
                    new Event(allPapers, entity.area).run();
                } else if (stones == 1) {
                    new Event(lastPaper, entity.area).run();
                    new Event(removePaper, entity.area).run();
                } else {
                    Dialogue.DialoguePrototype dialogue = new Dialogue.DialoguePrototype();
                    Dialogue.LinePrototype line = new Dialogue.LinePrototype();
                    line.message = "There are still " + stones + " stones in the pile. Determined, you put another in your pocket.";
                    dialogue.lines = new Dialogue.LinePrototype[]{line};
                    entity.area.ui.addActor(new Dialogue(dialogue, entity.area));
                }
                stones--;
            }
        };

        /* Dialogues */
        Dialogue.DialoguePrototype talkerDialogue = new Dialogue.DialoguePrototype();
        talkerDialogue.name = "talker";
        Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
        line1.message = "Yo bro I finally got something workable! There's no battle system yet, it's still difficult to add new content, and I obviously didn't make any of the assets, but hey, its been, what, a little over 24 hours? Pretty good imo. No jokes or anything, I'm just excited. Woo!";
        line1.name = "ur mum lol";
        Dialogue.LinePrototype line2 = new Dialogue.LinePrototype();
        line2.name = "wew lad";
        line2.message = "in case you missed it, I moved as well. Isn't that fancy?";
        line2.events = new Event.EventPrototype[]{release};
        talkerDialogue.lines = new Dialogue.LinePrototype[]{line1, line2};

        Dialogue.DialoguePrototype welcomeDial = new Dialogue.DialoguePrototype();
        welcomeDial.name = "welcome";
        Dialogue.LinePrototype welcomeLine = new Dialogue.LinePrototype();
        welcomeLine.name = "narrator";
        welcomeLine.face = "person14";
        welcomeLine.message = "Welcome to this really shitty piece of a game! Press e or enter to interact with things!";
        welcomeDial.lines = new Dialogue.LinePrototype[]{welcomeLine};

        Dialogue.DialoguePrototype allPapersDial = new Dialogue.DialoguePrototype();
        allPapersDial.name = "allPapers";
        line1 = new Dialogue.LinePrototype();
        line1.message = "You see a pile of precisely 132 stones. You pick one up and put it in your pocket.";
        allPapersDial.lines = new Dialogue.LinePrototype[]{line1};

        Dialogue.DialoguePrototype lastPaperDial = new Dialogue.DialoguePrototype();
        lastPaperDial.name = "lastPaper";
        line1 = new Dialogue.LinePrototype();
        line1.message = "There's only one stone left. With a smug face you pick up the last one and put it in your now bulging pockets, congratulating yourself on a job well done.";
        lastPaperDial.lines = new Dialogue.LinePrototype[]{line1};

        /* Adding things to area */
        entities = new Entity.EntityPrototype[]{talkerEntity, pile};
        dialogues = new Dialogue.DialoguePrototype[]{talkerDialogue, welcomeDial, allPapersDial, lastPaperDial};
    }

    public static Area getArea() {
        Area area = new Area(instance);
        Event.EventPrototype welcome = new Event.EventPrototype();
        welcome.type = "DIALOGUE";
        welcome.attributes.put("target", "welcome");
        new Event(welcome, area).run();
        return area;
    }
}
