package letshangllc.allfitness.ClassObjects;

/**
 * Created by cvburnha on 4/23/2016.
 */
public class Routine {
    private int routineId;
    private String routineName;

    public Routine(int routineId, String routineName) {
        this.routineId = routineId;
        this.routineName = routineName;
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }
}

