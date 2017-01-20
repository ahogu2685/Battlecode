package versionOne;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        switch (rc.getType()) {
            case ARCHON:
                break;
        }
    }

    public static void runArchon() throws GameActionException {
        while (true) {
            try {
            	if(rc.getRoundNum() == 1){
            		boolean hasHired = true;
            		while(hasHired) {
            			Direction dir = findDirection(Direction.NORTH);
            			if(rc.canHireGardener(dir)) {
            				rc.hireGardener(dir);
            				hasHired = false;
            			}
            		}
            	}
            	rc.broadcast(0, Math.round(rc.getLocation().x));
            	rc.broadcast(1, Math.round(rc.getLocation().y));
            	Clock.yield();
            }
            catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
	public static void forwardish(Direction dir) throws GameActionException {
	    float degreeOffset = 20;
	    int checksPerSide = 9;
	        if (rc.canMove(dir)) {
	        rc.move(dir);
	        }
	        int currentCheck = 1;
	        while(currentCheck <= checksPerSide) {
	            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
	                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
	            }
	            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
	                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
	            }
	            currentCheck++;
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
