/**
 * A patient, represented as the minimum the clinic needs to record a booking: a name and
 * a contact number.
 *
 * <p><b>Why a record.</b> A patient is a pure value here - the clinic does not change a
 * patient's name mid-booking, and two patients with the same name and number are the same
 * patient as far as the appointment book is concerned. A {@code record} states exactly
 * that: the fields are final, the accessors are generated, and {@code equals},
 * {@code hashCode} and {@code toString} are derived from the components rather than
 * hand-written. The compact constructor below is the one thing a record still needs,
 * because validation has to run before an invalid patient can exist.</p>
 *
 * @param name        the patient's full name
 * @param phoneNumber the contact number, stored exactly as the receptionist typed it
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public record Patient(String name, String phoneNumber) {

    /** Fewest digits a usable contact number can have. */
    private static final int MIN_PHONE_DIGITS = 8;

    /** Most digits accepted, matching the E.164 maximum. */
    private static final int MAX_PHONE_DIGITS = 15;

    /**
     * Validates the components before they are stored.
     *
     * <p>Reassigning the parameters is how a compact constructor normalises its input: the
     * trimmed values are what end up in the record's fields, so the object can never hold
     * a name with stray whitespace or a number made of punctuation.</p>
     *
     * @throws IllegalArgumentException if the name is blank or the number is unusable
     */
    public Patient {
        name = requireName(name);
        phoneNumber = requirePhoneNumber(phoneNumber);
    }

    /**
     * Returns the digits of the contact number, with all formatting removed.
     *
     * <p>Receptionists type the same number as "0412 345 678", "0412345678" or
     * "(04) 1234-5678". Comparing the digits instead of the text means a search by phone
     * finds the appointment however the number was originally entered, which is required
     * by the filtering and cancelling rules in Part 5.</p>
     *
     * @return the phone number containing digits only
     */
    public String phoneKey() {
        return phoneNumber.replaceAll("\\D", "");
    }

    /**
     * Tests whether a supplied number refers to this patient.
     *
     * @param candidate the number as searched for; {@code null} yields {@code false}
     * @return {@code true} if both numbers have the same digits
     */
    public boolean matchesPhone(String candidate) {
        return candidate != null && phoneKey().equals(candidate.replaceAll("\\D", ""));
    }

    /**
     * @return the patient's name followed by the contact number
     */
    @Override
    public String toString() {
        return name + " (" + phoneNumber + ")";
    }

    /**
     * Validates a patient name.
     *
     * @param value the candidate name
     * @return the trimmed name
     * @throws IllegalArgumentException if the name is missing or blank
     */
    private static String requireName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Patient name is required.");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be blank.");
        }
        return trimmed;
    }

    /**
     * Validates a contact number: digits, with the usual spacing, dashes, brackets and an
     * optional leading plus sign permitted.
     *
     * @param value the candidate number
     * @return the trimmed number
     * @throws IllegalArgumentException if the number is missing, malformed or the wrong length
     */
    private static String requirePhoneNumber(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Patient phone number is required.");
        }
        String trimmed = value.trim();
        if (!trimmed.matches("\\+?[0-9 ()\\-]{6,24}")) {
            throw new IllegalArgumentException(
                    "Patient phone number may only contain digits, spaces, brackets, dashes and a leading plus: \""
                            + trimmed + "\".");
        }
        String digits = trimmed.replaceAll("\\D", "");
        if (digits.length() < MIN_PHONE_DIGITS || digits.length() > MAX_PHONE_DIGITS) {
            throw new IllegalArgumentException(String.format(
                    "Patient phone number must contain %d to %d digits, but \"%s\" contains %d.",
                    MIN_PHONE_DIGITS, MAX_PHONE_DIGITS, trimmed, digits.length()));
        }
        return trimmed;
    }
}
