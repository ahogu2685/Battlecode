package versionOne;
import java.util.ArrayList;

import battlecode.common.*;

public strictfp class RobotPlayer
{
    static RobotController rc;
    public static MapLocation[] archonLocations;
    public static boolean king = false;
    public static int plantCooldown;
    public static boolean gardenerKing = false;
    public static int gardenerChannel = 0;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException
    {
        RobotPlayer.rc = rc;
        switch (rc.getType())
        {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
        }
    }

    public static void runArchon() throws GameActionException
    {
        while (true)
        {
            try
            {
                MapLocation myLocation = new MapLocation(Math.round(rc.getLocation().x), Math.round(rc.getLocation().y));
                if (rc.canMove(myLocation))
                {
                    rc.move(myLocation);
                    Clock.yield();
                }
                if (rc.getRoundNum() == 1)
                {
                    createArchonList();
                    boolean hasHired = true;
                    while (hasHired)
                    {
                        Direction dir = findDirection(Direction.NORTH);
                        if (rc.canHireGardener(dir))
                        {
                            rc.hireGardener(dir);
                            hasHired = false;
                        }
                    }
                }
                grouper();
                rc.broadcast(0, Math.round(rc.getLocation().x));
                rc.broadcast(1, Math.round(rc.getLocation().y));
                if (king)
                {
                    System.out.println(checkerArchon());
                }
                Clock.yield();
            }
            catch (Exception e)
            {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    public static void createArchonList()
    {
        archonLocations = rc.getInitialArchonLocations(rc.getTeam());
    }

    public static void grouper() throws GameActionException
    { //groups them together
        try
        {
            if (rc.getLocation().equals(archonLocations[0]))
            {
                king = true;
                System.out.println("I am the KING!");
                return;
            }
            forwardish((rc.getLocation().directionTo(archonLocations[0])));
        }
        catch (Exception e)
        {
            System.out.println("Grouper Exception");
            e.printStackTrace();
        }
    }

    public static boolean checkerArchon()
    { //checks if they are group together
        int archonCount = 0;
        while (archonCount < archonLocations.length - 1)
        {
            archonCount = 0;
            RobotInfo[] archonsNearby = new RobotInfo[rc.senseNearbyRobots(RobotType.ARCHON.sensorRadius, rc.getTeam()).length];
            archonsNearby = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadius, rc.getTeam());
            for (RobotInfo info: archonsNearby)
            {
                if (info.getType().equals(rc.getType()))
                {
                    archonCount++;
                }
            }
            Clock.yield();
        }
        return true;
    }

    public static void circleArchon() throws GameActionException
    {
        MapLocation targetLocation = new MapLocation(rc.readBroadcast(2), rc.readBroadcast(1));
        do {
            if (rc.getLocation().distanceTo(targetLocation) < 10)
            {
                forwardish(rc.getLocation().directionTo(targetLocation).rotateLeftDegrees(180f));
            }
        } while (rc.getLocation().distanceTo(targetLocation) > 10 && rc.getLocation().distanceTo(targetLocation) < 15);
        do {
            if (rc.getLocation().distanceTo(targetLocation) > 15)
            {
                forwardish(rc.getLocation().directionTo(targetLocation));
            }
        } while (rc.getLocation().distanceTo(targetLocation) > 10 && rc.getLocation().distanceTo(targetLocation) < 15);
        while (rc.getLocation().distanceTo(targetLocation) > 10 && rc.getLocation().distanceTo(targetLocation) < 15);
        {
            forwardish(rc.getLocation().directionTo(targetLocation).rotateLeftRads(1.45f));
        }
        circleArchon();
    }

    public static void forwardish(Direction dir) throws GameActionException
    {
        try
        {
            float degreeOffset = 20;
            int checksPerSide = 9;
            if (rc.canMove(dir))
            {
                rc.move(dir);
                return;
            }
            int currentCheck = 1;
            while (currentCheck <= checksPerSide)
            {
                if (rc.canMove(dir.rotateLeftDegrees(degreeOffset * currentCheck)))
                {
                    rc.move(dir.rotateLeftDegrees(degreeOffset * currentCheck));
                    return;
                }
                if (rc.canMove(dir.rotateRightDegrees(degreeOffset * currentCheck)))
                {
                    rc.move(dir.rotateRightDegrees(degreeOffset * currentCheck));
                    return;
                }
                currentCheck++;
            }
        }
        catch (Exception e)
        {
            System.out.println("Grouper Exception");
            e.printStackTrace();
        }
    }

    public static Direction findDirection(Direction dir) throws GameActionException
    {
        while (true)
        {
            try
            {
                float degreeOffset = 20;
                int checksPerSide = 9;
                int currentCheck = 1;
                while (currentCheck <= checksPerSide)
                {
                    if (rc.canMove(dir.rotateLeftDegrees(degreeOffset * currentCheck)))
                    {
                        return dir.rotateLeftDegrees(degreeOffset * currentCheck);
                    }
                    if (rc.canMove(dir.rotateRightDegrees(degreeOffset * currentCheck)))
                    {
                        return dir.rotateRightDegrees(degreeOffset * currentCheck);
                    }
                    currentCheck++;
                }
            }
            catch (Exception e)
            {
                System.out.println("findDirection Exception");
                e.printStackTrace();
            }
        }
    }

    public static void runGardener()
    {
        while (true)
        {
            try
            {
                MapLocation myLocation = new MapLocation(Math.round(rc.getLocation().x), Math.round(rc.getLocation().y));
                if (rc.canMove(myLocation))
                {
                    rc.move(myLocation);
                    Clock.yield();
                }
                if (rc.readBroadcast(200) == 0 || gardenerKing)
                {
                	setupGardener();
                    rc.broadcast(200, 1);
                    rc.broadcast(201, Math.round(rc.getLocation().x));
                    rc.broadcast(202, Math.round(rc.getLocation().y));
                    gardenerKing = true;
                    System.out.println("I am the leader of the gardeners");
                }
                else
                {
                    if (gardenerChannel == 0)
                    {
                        MapLocation newPlaceToSetup = new MapLocation((float)((double)(rc.readBroadcast(201) + 6)), (rc.readBroadcast(202)));
                        while (!(myLocation.equals(newPlaceToSetup)))
                        {
                            forwardish(rc.getLocation().directionTo(newPlaceToSetup));
                            myLocation = new MapLocation(Math.round(rc.getLocation().x), Math.round(rc.getLocation().y));
                        }
                        for (gardenerChannel = 200; rc.readBroadcast(gardenerChannel) != 0; gardenerChannel++)
                        {}
                    }
                    rc.broadcast(gardenerChannel, 1);
                    System.out.println(gardenerChannel);
                }
                if (gardenerKing || (gardenerChannel - 200) / 3 % 2 == 0)
                {
                    scoutGardener();
                }
                else
                {
                    tankGardener();
                }
                findWaterDirection();
                Clock.yield();
            }
            catch (Exception e)
            {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    public static void setupGardener() throws GameActionException
    {
    	MapLocation onMapX = rc.getLocation(); 
    	MapLocation onMapY = rc.getLocation();
    	while(!rc.onTheMap(onMapX) && !rc.onTheMap(onMapY)) {
	        try
	        {
	        	forwardish(rc.getInitialArchonLocations(rc.getTeam())[0].directionTo(rc.getInitialArchonLocations(rc.getTeam())[0]));
	        	onMapX = new MapLocation(rc.getLocation().x + 6, rc.getLocation().y);
	        	onMapY = new MapLocation(rc.getLocation().x, rc.getLocation().y + 6);
	        }
	        catch (Exception e)
	        {
	            System.out.println("findDirection Exception");
	            e.printStackTrace();
	        }
    	}
    }

    public static void scoutGardener() throws GameActionException
    {
        try
        {
            TreeInfo[] nearbyTrees = rc.senseNearbyTrees((float) 1.75, rc.getTeam());
            if ((nearbyTrees.length < 5) && (rc.getRoundNum() > plantCooldown + 10))
            {
                findPlantDirection(Direction.NORTH);
                plantCooldown = rc.getRoundNum();
            }
            if (nearbyTrees.length >= 5 && rc.canBuildRobot(RobotType.SCOUT, Direction.NORTH))
            {
                rc.buildRobot(RobotType.SCOUT, Direction.NORTH);
            }
        }
        catch (Exception e)
        {
            System.out.println("findDirection Exception");
            e.printStackTrace();
        }
    }

    public static void tankGardener() throws GameActionException
    {
        try
        {
            TreeInfo[] nearbyTrees = rc.senseNearbyTrees((float) 1.75, rc.getTeam());
            if ((nearbyTrees.length < 4) && (rc.getRoundNum() > plantCooldown + 10))
            {
                findPlantDirection(Direction.NORTH.rotateLeftDegrees(30));
                plantCooldown = rc.getRoundNum();
            }
            if (nearbyTrees.length >= 4 && rc.canBuildRobot(RobotType.TANK, Direction.NORTH))
            {
                rc.buildRobot(RobotType.TANK, Direction.NORTH);
            }
        }
        catch (Exception e)
        {
            System.out.println("findDirection Exception");
            e.printStackTrace();
        }
    }

    public static void findPlantDirection(Direction dir) throws GameActionException
    {
        while (true)
        {
            try
            {
                float degreeOffset = 60;
                int checksPerSide = 5;
                int currentCheck = 1;
                while (currentCheck <= checksPerSide)
                {
                    if (rc.canPlantTree(dir.rotateLeftDegrees(degreeOffset * currentCheck)))
                    {
                        rc.plantTree(dir.rotateLeftDegrees(degreeOffset * currentCheck));
                        Clock.yield();
                        return;
                    }
                    currentCheck++;
                }
            }
            catch (Exception e)
            {
                System.out.println("findDirection Exception");
                e.printStackTrace();
            }
        }
    }

    public static void findWaterDirection() throws GameActionException
    {
        try
        {
            TreeInfo[] nearbyTreesToWater = rc.senseNearbyTrees((float) 1.75, rc.getTeam());
            for (TreeInfo tree: nearbyTreesToWater)
            {
                if (tree.health <= 45)
                {
                    rc.water(tree.ID);
                    return;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("findDirection Exception");
            e.printStackTrace();
        }
    }
}
