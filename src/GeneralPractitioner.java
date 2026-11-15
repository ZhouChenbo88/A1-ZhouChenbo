import java.time.DayOfWeek;
import java.util.Set;

/**
 * A general practitioner: the first point of contact for most patients.
 *
 * <p>Inherits every shared attribute from {@link HealthProfessional} and adds the two
 * things that only a GP has - how long they have been practising and whether they are
 * currently taking on new patients - plus the {@link Prescribable} capability.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional implements Prescribable {

    /** Experience assumed for a newly arrived GP when the caller does not supply it. */
    private static final int DEFAULT_YEARS_OF_EXPERIENCE = 1;

    /** Upper bound used by validation; a working career cannot realistically exceed this. */
    private static final int MAX_YEARS_OF_EXPERIENCE = 60;

    private final int yearsOfExperience;
    private final boolean acceptingNewPatients;

    /**
     * Creates a GP on the standard Monday-to-Friday roster, with one year of experience
     * and open books.
     *
     * <p>This is the "convenience" constructor. It calls {@code super(id, name)} so the
     * shared state is initialised by the base class rather than copied into every
     * subclass, and the role-specific fields simply take their documented defaults.</p>
     *
     * @param id   digits-only identifier
     * @param name the GP's full name
     * @throws IllegalArgumentException if the ID or name is invalid
     */
    public GeneralPractitioner(String id, String name) {
        super(id, name);
        this.yearsOfExperience = DEFAULT_YEARS_OF_EXPERIENCE;
        this.acceptingNewPatients = true;
    }

    /**
     * Creates a GP with a caller-supplied roster, experience and availability status.
     *
     * @param id                   digits-only identifier
     * @param name                 the GP's full name
     * @param availableDays        working days; must contain at least one day
     * @param yearsOfExperience    completed years of practice, 0-60
     * @param acceptingNewPatients whether new patients may currently be booked
     * @throws IllegalArgumentException if any argument is outside its valid range
     */
    public GeneralPractitioner(String id, String name, Set<DayOfWeek> availableDays,
                               int yearsOfExperience, boolean acceptingNewPatients) {
        super(id, name, availableDays);
        this.yearsOfExperience = requireRange(yearsOfExperience, 0, MAX_YEARS_OF_EXPERIENCE,
                "Years of experience");
        this.acceptingNewPatients = acceptingNewPatients;
    }

    /**
     * @return the number of completed years of practice
     */
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    /**
     * @return {@code true} if the GP is currently accepting new patients
     */
    public boolean isAcceptingNewPatients() {
        return acceptingNewPatients;
    }

    @Override
    public String getProfessionalType() {
        return "General Practitioner";
    }

    @Override
    public String describeServices() {
        return "General consultations, chronic disease management, prescriptions and referrals";
    }

    /**
     * {@inheritDoc}
     *
     * <p>The message names the prescriber, so the caller can print the outcome of the
     * action without knowing which concrete role performed it.</p>
     */
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
                + detailLine("Experience", yearsOfExperience == 1
                        ? "1 year"
                        : yearsOfExperience + " years")
                + detailLine("New patients", acceptingNewPatients ? "accepted" : "waitlist only");
    }
}
