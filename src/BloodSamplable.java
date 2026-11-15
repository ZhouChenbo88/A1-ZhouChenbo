/**
 * A capability: the object is qualified to collect blood samples.
 *
 * <p>Like {@link Prescribable}, this is a role-independent capability rather than a
 * position in the class hierarchy. It exists so that the clinic can ask "who can take a
 * blood sample?" of any professional in the system - nurses today, perhaps a phlebotomist
 * or a general practitioner tomorrow - without changing either the question or the
 * collection that holds them.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public interface BloodSamplable {

    /**
     * Collects a blood sample from a patient.
     *
     * @param patientName the patient the sample is taken from; must not be blank
     * @return a printable confirmation including a specimen reference
     * @throws IllegalArgumentException if the patient name is null or blank
     */
    String takeBloodSample(String patientName);
}
