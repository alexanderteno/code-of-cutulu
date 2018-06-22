import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Survive the wrath of Kutulu
 * Coded fearlessly by JohnnyYuge & nmahoude (ok we might have been a bit scared by the old god...but don't say anything)
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
        }
        int sanityLossLonely = in.nextInt(); // how much sanity you lose every turn when alone, always 3 until wood 1
        int sanityLossGroup = in.nextInt(); // how much sanity you lose every turn when near another player, always 1 until wood 1
        int wandererSpawnTime = in.nextInt(); // how many turns the wanderer take to spawn, always 3 until wood 1
        int wandererLifeTime = in.nextInt(); // how many turns the wanderer is on map after spawning, always 40 until wood 1

        List<Entity> prevEntities;

        // game loop
        while (true) {
            List<Entity> entities = new ArrayList<Entity>();
            int entityCount = in.nextInt(); // the first given entity corresponds to your explorer
            for (int i = 0; i < entityCount; i++) {
                String entityType = in.next();
                int id = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int param0 = in.nextInt();
                int param1 = in.nextInt();
                int param2 = in.nextInt();
                entities.add(new Entity(id, entityType, x, y, param0, param1, param2));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("WAIT"); // MOVE <x> <y> | WAIT

            prevEntities = entities;
        }
    }
}

class Path {

}

class Point {

    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

}

enum EntityType {
    WANDERER,
    EXPLORER
}

enum WandererState {
    SPAWNING,
    WANDERING
}

class Entity {

    int id;
    EntityType entityType;
    Point position;
    int param0;
    int param1;
    int param2;

    public Entity(int id, String entityType, int x, int y, int param0, int param1, int param2) {
        this.id = id;
        this.position = new Point(x, y);
        this.param0 = param0;
        this.param1 = param1;
        this.param2 = param2;
        switch (entityType) {
            case "WANDERER":
                this.entityType = EntityType.WANDERER;
                break;
            case "EXPLORER":
                this.entityType = EntityType.EXPLORER;
                break;
            default:
                System.out.println("Invalid EntityType: " + entityType);
        }
    }

    public boolean isEffect() {
        return this.id == -1;
    }

    public int getSanity() {
        return this.param0;
    }

    public int getTimeBeforeSpawn() {
        return this.param0;
    }

    public int getTimeBeforeRecalled() {
        return this.param0;
    }

    public boolean isSpawning() {
        return this.param1 == WandererState.SPAWNING.ordinal();
    }

    public boolean isWandering() {
        return this.param1 == WandererState.WANDERING.ordinal();
    }

    public int target() {
        return this.param2;
    }

}