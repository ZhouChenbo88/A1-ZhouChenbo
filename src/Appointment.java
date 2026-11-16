import java.time.LocalTime;
import java.util.Objects;

/**
 * One booking: a patient, the health professional they are seeing, and the time slot.
 *
 * <p>The class stores a reference to a {@link HealthProfessional}, not a copy of one. Any
 * of the five roles can be booked, because the field's type is the abstract base class
 * rather than a concrete one - which is the whole reason Part 1 built that base class.</p>
 *
 * <p><b>Why the appointment implements {@link Comparable}.</b> Part 4 asks for the ability
 * to list a day's appointments in time order. Rather than having the manager know how to
 * compare appointments, each appointment knows how it orders against another, so any
 * collection of them can be sorted with {@code Collections.sort} or {@code stream().sorted()}
 * without the caller supplying a comparator.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    private final Patient patient;
    private final HealthProfessional professional;
    private final LocalTime timeSlot;

    /**
     * Creates an appointment.
     *
     * <p>All three parts are mandatory, so there is no constructor that leaves one out and
     * no setter that could blank one afterwards: an appointment that exists is always
     * complete. Whether the time is one of the clinic's slots is a booking rule rather than
     * a property of the appointment itself, so that check belongs to the manager.</p>
     *
     * @param patient      the patient being seen
     * @param professional the professional they are seeing, of any role
     * @param timeSlot     the start time of the appointment
     * @throws IllegalArgumentException if any part is missing
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime timeSlot) {
        if (patient == null) {
            throw new IllegalArgumentException("An appointment requires patient details.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("An appointment requires a health professional.");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("An appointment requires a time slot.");
        }
        this.patient = patient;
        this.professional = professional;
        this.timeSlot = timeSlot;
    }

    /**
     * @return the patient being seen
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * @return the professional being seen, referred to through the abstract base type
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * @return the start time of the appointment
     */
    public LocalTime getTimeSlot() {
        return timeSlot;
    }

    /**
     * Orders appointments earliest first, breaking ties by professional ID.
     *
     * <p>The tie-break keeps the output stable: two appointments at 09:00 with different
     * professionals may appear in either order as far as the brief is concerned, but a
     * demonstration that prints in a different order every run is hard to verify. Ordering
     * by ID also keeps {@code compareTo} consistent with {@code equals}, because two
     * appointments compare as equal only when they are the same booking.</p>
     *
     * @param other the appointment to compare against
     * @return a negative number, zero or a positive number as this appointment is earlier,
     *         the same, or later
     */
    @Override
    public int compareTo(Appointment other) {
        int byTime = timeSlot.compareTo(other.timeSlot);
        if (byTime != 0) {
            return byTime;
        }
        return professional.getId().compareTo(other.professional.getId());
    }

    /**
     * Two appointments are the same booking when the same professional is seeing someone at
     * the same time. The patient is deliberately not part of this: the clinic cannot seat
     * two patients with one professional in one slot, so the pair (professional, slot)
     * identifies the booking.
     *
     * @param other the object to compare with
     * @return {@code true} if both appointments occupy the same professional's slot
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Appointment otherAppointment)) {
            return false;
        }
        return professional.equals(otherAppointment.professional)
                && timeSlot.equals(otherAppointment.timeSlot);
    }

    /**
     * @return a hash consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(professional, timeSlot);
    }

    /**
     * Returns one aligned line, so a list of appointments prints as a readable table.
     *
     * <p>The professional's details come from its own accessors rather than from copying
     * fields, and because {@code getProfessionalType()} is overridden per role, a
     * specialist's line names the specialty while a nurse's names the ward role.</p>
     *
     * @return a single-line description of the appointment
     */
    @Override
    public String toString() {
        return String.format("%s   %-16s %-16s %s - %s [%s]",
                timeSlot,
                patient.name(),
                patient.phoneNumber(),
                professional.getName(),
                professional.getProfessionalType(),
                professional.getId());
    }
}
