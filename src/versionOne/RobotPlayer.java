package versionOne;
import java.util.ArrayList;

import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    public static MapLocation[] archonLocations;
    public static boolean king = false;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        switch (rc.getType()) {
            case ARCHON:
            	runArchon();
                break;
        }
    }

    public static void runArchon() throws GameActionException {
        while (true) {
            try {
            	if(rc.getRoundNum() == 1){
            		createArchonList();
            		boolean hasHired = true;
            		while(hasHired) {
            			Direction dir = findDirection(Direction.NORTH);
            			if(rc.canHireGardener(dir)) {
            				rc.hireGardener(dir);
            				hasHired = false;
            			}
            		}
            	}
            	grouper();
            	rc.broadcast(0, Math.round(rc.getLocation().x));
            	rc.broadcast(1, Math.round(rc.getLocation().y));
            	if(king) {
	            		System.out.println(checkerArchon());
	            	}
            	Clock.yield();
            }
            catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    public static void createArchonList() {
    	 archonLocations = rc.getInitialArchonLocations(rc.getTeam());
    }
    
    public static void grouper() throws GameActionException { //groups them together
    try {    
    	if(rc.getLocation().equals(archonLocations[0])){
    		king = true;
    		System.out.println("I am the KING!");
    		return;
    	}
    	forwardish((rc.getLocation().directionTo(archonLocations[0])));
    } catch (Exception e) {
        System.out.println("Grouper Exception");
        e.printStackTrace();
        }
    }

    public static boolean checkerArchon() { //checks if they are group together
    	int archonCount = 0;
    	while(archonCount < archonLocations.length - 1){
        	archonCount = 0;
        	RobotInfo[] archonsNearby = new RobotInfo[rc.senseNearbyRobots(RobotType.ARCHON.sensorRadius, rc.getTeam()).length];
        	archonsNearby = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadius, rc.getTeam());
        	for(RobotInfo info : archonsNearby){
        		if(info.getType().equals(rc.getType())){
        			archonCount++;
        		}
        	}
        	Clock.yield();
    	}
    	return true;
    }

    public static void circleArchon() throws GameActionException {
        MapLocation targetLocation = new MapLocation(rc.readBroadcast(2), rc.readBroadcast(1));
        do	{
            if (rc.getLocation().distanceTo(targetLocation) < 10)
            {
                forwardish(rc.getLocation().directionTo(targetLocation).rotateLeftDegrees(180f));
            }
        } while (rc.getLocation().distanceTo(targetLocation) > 10 && rc.getLocation().distanceTo(targetLocation) < 15);
        do	{
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

    
	public static void forwardish(Direction dir) throws GameActionException {
	    try {    
		    float degreeOffset = 20;
		    int checksPerSide = 9;
		        if (rc.canMove(dir)) {
		        	rc.move(dir);
		        	return;
		        }
		        int currentCheck = 1;
		        while(currentCheck <= checksPerSide) {
		            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
		                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
		                return;
		            }
		            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
		                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
		                return;
		            }
		            currentCheck++;
		        }
	    } catch (Exception e) {
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
                while(currentCheck <= checksPerSide) {
                    if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                        return dir.rotateLeftDegrees(degreeOffset*currentCheck);
                    }
                    if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                        return dir.rotateRightDegrees(degreeOffset*currentCheck);
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
}
