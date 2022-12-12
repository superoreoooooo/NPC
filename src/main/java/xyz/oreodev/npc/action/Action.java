package xyz.oreodev.npc.action;

import xyz.oreodev.npc.NPCPlayer;

public interface Action {
    void perform(NPCPlayer player);
    ActionType getType();
}
