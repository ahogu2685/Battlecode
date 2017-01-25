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

    public static void runArchon()
    {
        while (true)
        {
            if (rc.canHireGardener(Direction.EAST) && rc.getRobotCount() < 3)
            {
                try
                {
                    rc.hireGardener(Direction.EAST);
                }
                catch (GameActionException e)
                {
                    e.printStackTrace();
                }
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
                        while (!(rc.getLocation().equals(newPlaceToSetup)))
                        {
                            if (rc.canMove(newPlaceToSetup))
                            {
                                rc.move(newPlaceToSetup);
                                Clock.yield();
                            }
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
