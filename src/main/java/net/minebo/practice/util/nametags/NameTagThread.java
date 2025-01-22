package net.minebo.practice.util.nametags;

import lombok.Getter;
import net.minebo.practice.util.nametags.construct.NameTagUpdate;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class NameTagThread extends Thread {

    @Getter private static Map<NameTagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    NameTagThread() {
        super("NameTag Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {

            final Iterator<NameTagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while (pendingUpdatesIterator.hasNext()) {

                final NameTagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    NameTagHandler.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(NameTagHandler.getUpdateInterval() * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}