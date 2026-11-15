import java.time.DayOfWeek;
import java.util.Set;

/**
 * A nurse, for example a ward nurse or an outpatient nurse.
 *
 * <p>Implements {@link BloodSamplable} because collecting specimens is part of the nursing
 * scope of practice, and does <em>not</em> implement {@link Prescribable}. Two roles
 * therefore behave differently when treated through the same base type, which is exactly
 * what Part 3 sets out to show.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class Nurse extends HealthProfessional implements BloodSamplable {

    /** Maximum accepted length of service, used as a sanity bound. */
    private static final int MAX_YEARS_IN_SERVICE = 50;

    /**
     * Counts the specimens collected by every nurse in this run.
     *
     * <p>A static field is used because the number is a property of the clinic's
     * laboratory workflow, not of any single nurse: two nurses must not both hand out
     * "specimen 1". Each instance reads it through {@link #nextSpecimenReference()}.</p>
     */
    private static int specimensCollected = 0;

    /** The ward or clinic area this nurse works in. */
    private final String ward;

    /** Completed years of service. */
    private final int yearsInService;

    /**
     * Creates a nurse on the standard Monday-to-Friday roster with one year of service.
     *
     * @param id   digits-only identifier
     * @param name the nurse's full name
     * @param ward the ward or clinic area, for example "Ward 2B"
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, String ward) {
        this(id, name, java.util.EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), ward, 1);
    }

    /**
     * Creates a nurse with a caller-supplied roster and length of service.
     *
     * @param id             digits-only identifier
     * @param name           the nurse's full name
     * @param availableDays  working days; must contain at least one day
     * @param ward           the ward or clinic area
     * @param yearsInService completed years of service, 0-50
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, Set<DayOfWeek> availableDays, String ward,
                 int yearsInService) {
        super(id, name, availableDays);
        this.ward = requireText(ward, "Ward");
        this.yearsInService = requireRange(yearsInService, 0, MAX_YEARS_IN_SERVICE,
                "Years in service");
    }

    /**
     * @return the ward or clinic area this nurse works in
     */
    public String getWard() {
        return ward;
    }

    /**
     * @return the number of completed years of service
     */
    public int getYearsInService() {
        return yearsInService;
    }

    @Override
    public String getProfessionalType() {
        return "Nurse";
    }

    @Override
    public String describeServices() {
        return "Patient observation, wound care, specimen collection and clinical support";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Generates a sequential specimen reference so that the confirmation identifies
     * the physical sample as well as the people involved.</p>
     */
    @Override
    public String takeBloodSample(String patientName) {
        requireText(patientName, "Patient name");
        return String.format("%s - %s, %s: collected a blood sample from %s [specimen %s].",
                getName(), getProfessionalType(), ward, patientName.trim(), nextSpecimenReference());
    }

    /**
     * Produces the next lab reference for this run.
     *
     * @return a reference of the form {@code BS-0001}
     */
    private static String nextSpecimenReference() {
        specimensCollected++;
        return String.format("BS-%04d", specimensCollected);
    }

    @Override
    public String toString() {
        return super.toString()
                + detailLine("Ward", ward)
                + detailLine("Experience", yearsInService == 1
                        ? "1 year"
                        : yearsInService + " years");
    }
}
