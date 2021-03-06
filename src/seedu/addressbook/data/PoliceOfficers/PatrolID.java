package seedu.addressbook.data.PoliceOfficers;
//@@author muhdharun -unused
import seedu.addressbook.data.exception.IllegalValueException;

/**
 * Represents a Patrol resource's ID in EX-SI-53.
 * Guarantees: immutable
 */

public class PatrolID {
    public static final String EXAMPLE = "PO1";
    public static final String MESSAGE_ID_CONSTRAINTS = "Patrol ID has to be a number excluding 0, and must not be an existing ID";
    public static final String PATROL_ID_PREFIX = "PO";
    //public static final int CONSTRAINT = 0;

    public final int ID;
    public final String patrolID;

    public PatrolID (int identification){

        this.ID = identification;
        this.patrolID = PATROL_ID_PREFIX + Integer.toString(identification);
    }

    public String getPatrolID() {return patrolID;}
    /**
     * Validates given ID.
     *
     * @throws IllegalValueException if given ID is invalid.
     */

    //public static boolean isValidID(int test) {return test > CONSTRAINT; }

    @Override
    public String toString() {
        return patrolID;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PatrolID // instanceof handles nulls
                && this.patrolID.equals(((PatrolID) other).patrolID)); // state check
    }

    @Override
    public int hashCode() {
        return patrolID.hashCode();
    }

}
