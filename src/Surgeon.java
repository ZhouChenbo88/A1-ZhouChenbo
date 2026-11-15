import java.time.DayOfWeek;
import java.util.Set;

/**
 * A surgeon, for example an orthopaedic or cardiothoracic surgeon.
 *
 * <p>Shares the prescribing capability with the GP and the specialist, but performs a
 * genuinely different job, so it adds surgical state and a surgical behaviour rather than
 * reusing the specialist's.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class Surgeon extends HealthProfessional implements Prescribable {

    /** Maximum number of procedures accepted by validation, used as a sanity bound. */
    private static final int MAX_PROCEDURES_PERFORMED = 20_000;

    /** The operating theatre this surgeon is rostered to, for example "Theatre 3". */
    private final String theatreRoom;

    /** Completed procedures, used only to make the printed output meaningful. */
    private final int proceduresPerformed;

    /**
     * Creates a surgeon on the standard Monday-to-Friday roster with no recorded procedures.
     *
     * @param id          digits-only identifier
     * @param name        the surgeon's full name
     * @param theatreRoom the rostered theatre, for example "Theatre 3"
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(String id, String name, String theatreRoom) {
        this(id, name, java.util.EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                theatreRoom, 0);
    }

    /**
     * Creates a surgeon with a caller-supplied roster and procedure count.
     *
     * @param id                  digits-only identifier
     * @param name                the surgeon's full name
     * @param availableDays       working days; must contain at least one day
     * @param theatreRoom         the rostered theatre
     * @param proceduresPerformed completed procedures, 0-20000
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(String id, String name, Set<DayOfWeek> availableDays,
                   String theatreRoom, int proceduresPerformed) {
        super(id, name, availableDays);
        this.theatreRoom = requireText(theatreRoom, "Theatre room");
        this.proceduresPerformed = requireRange(proceduresPerformed, 0, MAX_PROCEDURES_PERFORMED,
                "Procedures performed");
    }

    /**
     * @return the rostered operating theatre
     */
    public String getTheatreRoom() {
        return theatreRoom;
    }

    /**
     * @return the number of completed procedures
     */
    public int getProceduresPerformed() {
        return proceduresPerformed;
    }

    @Override
    public String getProfessionalType() {
        return "Surgeon";
    }

    @Override
    public String describeServices() {
        return "Surgical procedures, pre-operative assessment and post-operative review";
    }

    /**
     * Books a procedure into this surgeon's theatre.
     *
     * @param procedure the procedure being scheduled; must not be blank
     * @return a printable confirmation naming the theatre
     * @throws IllegalArgumentException if the procedure is null or blank
     */
    public String scheduleProcedure(String procedure) {
        requireText(procedure, "Procedure");
        return String.format("%s (%s) scheduled %s in %s.",
                getName(), getProfessionalType(), procedure.trim(), theatreRoom);
    }

    @Override
    public String prescribe(String medication, String dosage) {
        requireText(medication, "Medication");
        requireText(dosage, "Dosage");
        return String.format("Prescription issued by %s - %s: %s, %s.",
                getName(), getProfessionalType(), medication.trim(), dosage.trim());
    }

    @Override
    public String toString() {
        return super.toString()
                + detailLine("Theatre", theatreRoom)
                + detailLine("Procedures", proceduresPerformed + " performed");
    }
}
