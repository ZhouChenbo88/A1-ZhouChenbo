/**
 * A capability: the object is allowed to issue prescriptions.
 *
 * <p><b>Why an interface and not another superclass.</b> Prescribing is not a kind of
 * health professional - it is something a professional <em>can do</em>. A general
 * practitioner, a specialist and a surgeon all prescribe, yet none of them is a
 * meaningful superclass of the others, and a nurse or dietitian must not be forced to
 * inherit a {@code prescribe} method they are not permitted to perform. Modelling the
 * capability as an interface separates "what this role is" (inheritance from
 * {@link HealthProfessional}) from "what this role can do" (a contract it opts into).</p>
 *
 * <p>The practical benefit appears in Part 3 and Part 5: a collection can be filtered to
 * the professionals who can prescribe using {@code instanceof Prescribable}, without
 * knowing their concrete classes and without touching them when a new role is added.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public interface Prescribable {

    /**
     * Issues a prescription for a patient.
     *
     * <p>Each implementing role returns its own confirmation message, so the caller can
     * print the result of the action without knowing which role performed it.</p>
     *
     * @param medication the drug being prescribed; must not be blank
     * @param dosage     the dosage instructions, for example {@code "500 mg twice daily"}
     * @return a printable confirmation of the prescription
     * @throws IllegalArgumentException if either argument is null or blank
     */
    String prescribe(String medication, String dosage);
}
