package xyz.oreodev.npc.action;

import xyz.oreodev.npc.NPCPlayer;

public class ActionWait implements Action {
    long delay;

    public ActionWait(long delay) {
        this.delay = delay;
    }

    @Override
    public void perform(NPCPlayer player) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.WAIT;
    }

    public long getDelay() {
        return delay;
    }
}
