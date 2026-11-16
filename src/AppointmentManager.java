import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The clinic's appointment book: the single place where appointments are added, viewed,
 * filtered, sorted and cancelled.
 *
 * <p><b>Why an {@link ArrayList}.</b> The book is a small, ordered, growing collection that
 * is read far more often than it is searched by key: a day holds at most a few dozen
 * bookings. An {@code ArrayList} keeps insertion order, copies nothing on resize in the
 * common case, and - unlike a {@code HashMap} or {@code HashSet} - allows two appointments
 * that are "equal" by the map's rules to be reasoned about explicitly. Lookups by
 * professional or by phone are a linear scan of a handful of entries, so a hash index would
 * cost more in complexity than it saves. If the clinic ever needed thousands of bookings a
 * day, the private field is the only thing that would change.</p>
 *
 * <p><b>Every operation reports back.</b> The brief requires a warning when an action
 * cannot be performed, so the mutating methods return a message describing what happened
 * instead of returning {@code void} or throwing. The manager never prints: the caller
 * decides how output looks, which keeps the class usable from a future user interface
 * without rewriting it.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class AppointmentManager {

    private final List<Appointment> appointments;
    private final ClinicSchedule schedule;

    /**
     * Creates a book using the standard 08:00-16:00 timetable.
     */
    public AppointmentManager() {
        this(ClinicSchedule.standard());
    }

    /**
     * Creates a book for a specific timetable.
     *
     * @param schedule the timetable bookings must fit into
     * @throws NullPointerException if the schedule is missing
     */
    public AppointmentManager(ClinicSchedule schedule) {
        this.appointments = new ArrayList<>();
        this.schedule = Objects.requireNonNull(schedule, "A clinic schedule is required.");
    }

    /**
     * Books a patient with a professional, if the booking is allowed.
     *
     * <p>Four rules are enforced here, in the order a receptionist would hit them: the
     * details must be present, the time must be a real clinic slot, and the professional
     * must not already be seeing someone then. The professional is compared with
     * {@code equals}, which {@link HealthProfessional} defines by ID, so the check works
     * whichever concrete role was booked and however the object was obtained.</p>
     *
     * @param patient      the patient to book
     * @param professional the professional to book, of any role
     * @param timeSlot     the requested start time
     * @return a message confirming the booking, or explaining why it was rejected
     */
    public String addAppointment(Patient patient, HealthProfessional professional, LocalTime timeSlot) {
        if (patient == null) {
            return "Booking rejected: patient details are required.";
        }
        if (professional == null) {
            return "Booking rejected: a health professional must be chosen.";
        }
        if (timeSlot == null) {
            return "Booking rejected: a time slot is required.";
        }
        if (!schedule.isSlot(timeSlot)) {
            return String.format("Booking rejected: %s is not a clinic slot (%s).",
                    timeSlot, schedule.describe());
        }
        if (isDoubleBooked(professional, timeSlot)) {
            return String.format("Booking rejected: %s is already booked at %s.",
                    professional.getName(), timeSlot);
        }

        appointments.add(new Appointment(patient, professional, timeSlot));
        String confirmation = String.format("Booked %s with %s, %s, for %s.",
                timeSlot, professional.getName(), professional.getProfessionalType(), patient.name());
        return confirmation + rosterNote(professional);
    }

    /**
     * Adds a note when the professional does not normally work today.
     *
     * <p>The brief limits bookings to the current day but only requires double-booking to
     * be refused, so a roster mismatch is reported rather than rejected: the clinic records
     * the request and can treat it as a waitlist entry, and the demonstration still works
     * whichever day it is run on.</p>
     *
     * @param professional the professional just booked
     * @return an empty string, or a bracketed note for the caller to append
     */
    private String rosterNote(HealthProfessional professional) {
        LocalDate today = LocalDate.now();
        if (professional.isAvailableOn(today.getDayOfWeek())) {
            return "";
        }
        return String.format(" [note: not normally rostered on %s - recorded as a waitlist request]",
                today.getDayOfWeek());
    }

    /**
     * Tests whether a professional already has an appointment in a slot.
     *
     * @param professional the professional to check
     * @param timeSlot     the slot to check
     * @return {@code true} if that professional is already booked then
     */
    private boolean isDoubleBooked(HealthProfessional professional, LocalTime timeSlot) {
        return appointments.stream()
                .anyMatch(appointment -> appointment.getProfessional().equals(professional)
                        && appointment.getTimeSlot().equals(timeSlot));
    }

    /**
     * Returns every appointment in time order.
     *
     * @return a sorted, unmodifiable copy of the book
     */
    public List<Appointment> allAppointments() {
        return appointments.stream().sorted().toList();
    }

    /**
     * Returns one professional's appointments in time order.
     *
     * @param professionalId the ID to filter on; {@code null} matches nothing
     * @return a sorted list, empty if the professional has no bookings
     */
    public List<Appointment> appointmentsForProfessional(String professionalId) {
        if (professionalId == null) {
            return List.of();
        }
        String wanted = professionalId.trim();
        return appointments.stream()
                .filter(appointment -> appointment.getProfessional().getId().equals(wanted))
                .sorted()
                .toList();
    }

    /**
     * Returns one patient's appointments, found by phone number.
     *
     * <p>The comparison uses {@link Patient#matchesPhone(String)}, so "0433 998 211" and
     * "0433998211" find the same appointments.</p>
     *
     * @param phoneNumber the number to filter on; {@code null} matches nothing
     * @return a sorted list, empty if no appointment matches
     */
    public List<Appointment> appointmentsForPatient(String phoneNumber) {
        if (phoneNumber == null) {
            return List.of();
        }
        return appointments.stream()
                .filter(appointment -> appointment.getPatient().matchesPhone(phoneNumber))
                .sorted()
                .toList();
    }

    /**
     * Cancels the appointment a professional has at a given time.
     *
     * @param professionalId the professional's ID
     * @param timeSlot       the appointment's start time
     * @return a message confirming the cancellation, or explaining why nothing was cancelled
     */
    public String cancelByProfessionalAndTime(String professionalId, LocalTime timeSlot) {
        if (professionalId == null || timeSlot == null) {
            return "Cancellation rejected: a professional ID and a time slot are required.";
        }
        Appointment match = appointments.stream()
                .filter(appointment -> appointment.getProfessional().getId().equals(professionalId.trim())
                        && appointment.getTimeSlot().equals(timeSlot))
                .findFirst()
                .orElse(null);

        if (match == null) {
            return String.format("Cancellation failed: no appointment for professional %s at %s.",
                    professionalId.trim(), timeSlot);
        }
        appointments.remove(match);
        return String.format("Cancelled %s with %s for %s.",
                match.getTimeSlot(), match.getProfessional().getName(), match.getPatient().name());
    }

    /**
     * Cancels every appointment belonging to a patient, identified by phone number.
     *
     * <p>A phone number may legitimately hold more than one booking - a parent booking for
     * two children, for example - so this cancels the whole set and reports how many were
     * removed rather than silently dropping all but the first.</p>
     *
     * @param phoneNumber the patient's contact number
     * @return a message listing what was cancelled, or explaining why nothing was
     */
    public String cancelByPatientPhone(String phoneNumber) {
        if (phoneNumber == null) {
            return "Cancellation rejected: a phone number is required.";
        }
        List<Appointment> matches = appointments.stream()
                .filter(appointment -> appointment.getPatient().matchesPhone(phoneNumber))
                .sorted()
                .toList();

        if (matches.isEmpty()) {
            return String.format("Cancellation failed: no appointments found for %s.", phoneNumber.trim());
        }
        matches.forEach(appointments::remove);
        return String.format("Cancelled %d appointment%s for %s: %s.",
                matches.size(), matches.size() == 1 ? "" : "s",
                matches.get(0).getPatient().name(), describeBriefly(matches));
    }

    /**
     * Joins appointments into a compact "time with name" list.
     *
     * @param list the appointments to summarise
     * @return a comma-separated summary
     */
    private String describeBriefly(List<Appointment> list) {
        return list.stream()
                .map(appointment -> appointment.getTimeSlot() + " with " + appointment.getProfessional().getName())
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
    }

    /**
     * @return the number of appointments currently in the book
     */
    public int size() {
        return appointments.size();
    }

    /**
     * @return the timetable this book is constrained by
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }
}
