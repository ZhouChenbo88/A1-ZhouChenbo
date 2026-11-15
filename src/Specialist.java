import java.time.DayOfWeek;
import java.util.Set;

/**
 * A specialist, for example a paediatrician, cardiologist or radiologist.
 *
 * <p>Demonstrates that a subclass may add data that changes the object's own
 * description: {@link #getProfessionalType()} returns "Specialist (Paediatrics)" for one
 * object and "Specialist (Cardiology)" for another, so the same overridden method
 * produces role-correct output for every instance.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class Specialist extends HealthProfessional implements Prescribable {

    /** The field this specialist practises in, for example "Paediatrics". */
    private final String specialty;

    /** Whether the specialist accepts referrals directly from a GP. */
    private final boolean acceptsReferrals;

    /**
     * Creates a specialist on the standard Monday-to-Friday roster who accepts referrals.
     *
     * @param id        digits-only identifier
     * @param name      the specialist's full name
     * @param specialty the field of practice, for example "Cardiology"
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Specialist(String id, String name, String specialty) {
        this(id, name, java.util.EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                specialty, true);
    }

    /**
     * Creates a specialist with a caller-supplied roster and referral policy.
     *
     * @param id              digits-only identifier
     * @param name            the specialist's full name
     * @param availableDays   working days; must contain at least one day
     * @param specialty       the field of practice
     * @param acceptsReferrals whether direct GP referrals are accepted
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Specialist(String id, String name, Set<DayOfWeek> availableDays,
                      String specialty, boolean acceptsReferrals) {
        super(id, name, availableDays);
        this.specialty = requireText(specialty, "Specialty");
        this.acceptsReferrals = acceptsReferrals;
    }

    /**
     * @return the field of practice
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * @return {@code true} if direct GP referrals are accepted
     */
    public boolean isAcceptingReferrals() {
        return acceptsReferrals;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The specialty is folded into the role name so that printing a mixed collection
     * immediately shows which kind of specialist each object is.</p>
     */
    @Override
    public String getProfessionalType() {
        return "Specialist (" + specialty + ")";
    }

    @Override
    public String describeServices() {
        return "Focused specialist assessment, diagnostic testing and treatment planning";
    }

    /**
     * Requests a diagnostic investigation, for example an MRI or a blood panel.
     *
     * @param investigation the investigation being requested; must not be blank
     * @return a printable confirmation of the request
     * @throws IllegalArgumentException if the investigation is null or blank
     */
    public String requestInvestigation(String investigation) {
        requireText(investigation, "Investigation");
        return String.format("%s (%s) requested %s.", getName(), getProfessionalType(),
                investigation.trim());
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
                + detailLine("Referrals", acceptsReferrals ? "direct GP referrals accepted" : "GP referral required");
    }
}
