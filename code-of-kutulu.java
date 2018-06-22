import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.Collectors;

/**
 * Survive the wrath of Kutulu
 * Coded fearlessly by JohnnyYuge & nmahoude (ok we might have been a bit scared by the old god...but don't say anything)
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        List<String> map = new ArrayList<String>();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            System.err.println(line);
            map.add(line);
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

            Entity myExplorer = entities.stream().findFirst().get();

            Entity firstEnemy = entities.stream()
                    .filter((Entity entity) -> entity.entityType == EntityType.WANDERER)
                    .findFirst()
                    .orElse(null);

            if (firstEnemy != null) {
                List<Point> pathToFirstEnemy = AStar.getPath(myExplorer, firstEnemy, map);

                if (pathToFirstEnemy != null) {
                    for (Point point : pathToFirstEnemy) {
                        System.err.println(point);
                    }
                }
            }

            System.out.println("MOVE 2 2"); // MOVE <x> <y> | WAIT

            prevEntities = entities;
        }
    }
}

class Point {

    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "Point: (" + this.x + ", " + this.y + ")";
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

class AStar {

    private static final int DISTANCE_TO_NEIGHBOUR = 1;

    private static final Point[] directions = new Point[]{
            new Point(1, 0),
            new Point(0, -1),
            new Point(-1, 0),
            new Point(0, 1)
    };

    private static Integer heuristicCostEstimate(Point start, Point end) {
        return Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
    }

    private static List<Point> getNeighbours(Point current, List<String> map) {
        return Arrays.stream(AStar.directions)
                .filter((Point direction) -> {
                    int nextX = current.x + direction.x;
                    boolean withinWidth = 0 <= nextX && nextX < map.get(0).length();
                    int nextY = current.y + direction.y;
                    boolean withinHeight = 0 <= nextY && nextY < map.size();
                    if (withinWidth && withinHeight) {
                        return map.get(nextY).charAt(nextX) != '#';
                    }
                    return false;
                })
                .map((Point direction) -> new Point(current.x + direction.x, current.y + direction.y))
                .collect(Collectors.toList());
    }

    public static List<Point> getPath(Entity startEntity, Entity endEntity, List<String> map) {

        List<Point> closedSet = new ArrayList<Point>();

        List<Point> openSet = new ArrayList<Point>() {{
            add(startEntity.position);
        }};

        Map<Point, Point> cameFrom = new HashMap<Point, Point>();

        Map<Point, Integer> gScore = new HashMap<Point, Integer>();

        gScore.put(startEntity.position, 0);

        Map<Point, Integer> fScore = new HashMap<Point, Integer>();

        fScore.put(startEntity.position, AStar.heuristicCostEstimate(startEntity.position, endEntity.position));

        while (!openSet.isEmpty()) {

            Point current = openSet.stream().min(Comparator.comparingInt(fScore::get)).get();

            if (current.equals(endEntity.position)) {
                return reconstructPath(cameFrom, current);
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Point neighbour : AStar.getNeighbours(current, map)) {

                if (closedSet.contains(neighbour)) {
                    continue;
                }

                if (!openSet.contains(neighbour)) {
                    openSet.add(neighbour);
                }

                Integer currentGScore = gScore.get(current);
                Integer tentativeGScore = currentGScore == null ?
                        Integer.MAX_VALUE : currentGScore + DISTANCE_TO_NEIGHBOUR;

                Integer neighbourGScore = gScore.get(neighbour) != null ? gScore.get(neighbour) : Integer.MAX_VALUE;
                if (tentativeGScore >= neighbourGScore) {
                    continue;
                }

                cameFrom.put(neighbour, current);
                gScore.put(neighbour, tentativeGScore);
                fScore.put(neighbour, neighbourGScore + heuristicCostEstimate(neighbour, endEntity.position));

            }

        }

        return null;

    }

    private static List<Point> reconstructPath(Map<Point, Point> cameFrom, Point finalPoint) {
        Point current = finalPoint;
        List<Point> totalPath = new ArrayList<Point>() {{
            add(finalPoint);
        }};
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        return totalPath;
    }

}