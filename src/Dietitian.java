import java.time.DayOfWeek;
import java.util.Set;

/**
 * An accredited dietitian delivering medical nutrition therapy.
 *
 * <p>Deliberately does <em>not</em> implement {@link Prescribable}. A dietitian is a
 * health professional and belongs in the same hierarchy, but the clinic's rules do not
 * let this role prescribe, so the capability interface is left off. This is the point of
 * separating inheritance from capabilities: adding the role costs no change to
 * {@code HealthProfessional}, and leaving a capability off is a compile-time guarantee
 * rather than a comment.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class Dietitian extends HealthProfessional {

    /** Minimum accepted consultation length in minutes. */
    private static final int MIN_CONSULTATION_MINUTES = 15;

    /** Maximum accepted consultation length in minutes. */
    private static final int MAX_CONSULTATION_MINUTES = 120;

    /** The clinical area this dietitian focuses on. */
    private final String dietaryFocus;

    /** Standard appointment length for this dietitian. */
    private final int consultationMinutes;

    /**
     * Creates a dietitian on the standard Monday-to-Friday roster with a 30-minute
     * consultation.
     *
     * @param id           digits-only identifier
     * @param name         the dietitian's full name
     * @param dietaryFocus the clinical focus, for example "Diabetes and renal nutrition"
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, String dietaryFocus) {
        this(id, name, java.util.EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                dietaryFocus, 30);
    }

    /**
     * Creates a dietitian with a caller-supplied roster and consultation length.
     *
     * @param id                  digits-only identifier
     * @param name                the dietitian's full name
     * @param availableDays       working days; must contain at least one day
     * @param dietaryFocus        the clinical focus
     * @param consultationMinutes standard consultation length, 15-120 minutes
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, Set<DayOfWeek> availableDays,
                     String dietaryFocus, int consultationMinutes) {
        super(id, name, availableDays);
        this.dietaryFocus = requireText(dietaryFocus, "Dietary focus");
        this.consultationMinutes = requireRange(consultationMinutes, MIN_CONSULTATION_MINUTES,
                MAX_CONSULTATION_MINUTES, "Consultation length");
    }

    /**
     * @return the clinical focus of this dietitian
     */
    public String getDietaryFocus() {
        return dietaryFocus;
    }

    /**
     * @return the standard consultation length in minutes
     */
    public int getConsultationMinutes() {
        return consultationMinutes;
    }

    @Override
    public String getProfessionalType() {
        return "Dietitian";
    }

    @Override
    public String describeServices() {
        return "Nutrition assessment, medical nutrition therapy and meal planning";
    }

    /**
     * Produces a meal plan for a stated goal.
     *
     * @param goal the nutritional goal, for example "reduce sodium intake"
     * @return a printable confirmation of the plan
     * @throws IllegalArgumentException if the goal is null or blank
     */
    public String designMealPlan(String goal) {
        requireText(goal, "Goal");
        return String.format("%s (%s) prepared a %s plan aimed to %s.",
                getName(), getProfessionalType(), dietaryFocus, goal.trim());
    }

    @Override
    public String toString() {
        return super.toString()
                + detailLine("Focus", dietaryFocus)
                + detailLine("Consult time", consultationMinutes + " minutes");
    }
}
