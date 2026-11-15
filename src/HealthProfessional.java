import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The state and behaviour that every health professional in the clinic shares,
 * regardless of their specialty.
 *
 * <p><b>Why this class is abstract.</b> A health professional with no role is not a
 * thing the clinic can roster or book, so allowing {@code new HealthProfessional(...)}
 * would let the system hold a meaningless object. Declaring the class {@code abstract}
 * makes that impossible at compile time and simultaneously forces every concrete role
 * (Part 2) to answer {@link #getProfessionalType()} and {@link #describeServices()}.
 * Making the class abstract is therefore two design decisions in one line: a rule
 * ("no plain professionals") and a contract ("every role must describe itself").</p>
 *
 * <p><b>Why the fields are {@code final}.</b> A professional's identity - who they are
 * and which days they work - is fixed once they join the clinic. Keeping the fields
 * {@code final} and validating only in the constructor means an object can never be
 * observed in an invalid state: there is no setter that could set an empty name or an
 * ID containing letters. Callers get the data through accessors that return either an
 * immutable value or an unmodifiable view.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public abstract class HealthProfessional {

    /**
     * Availability used when the caller does not supply working days: the standard
     * Monday-to-Friday week. Wrapped as unmodifiable so the shared constant can never
     * be altered by accident; every object still receives its own copy.
     */
    private static final Set<DayOfWeek> DEFAULT_AVAILABILITY =
            Collections.unmodifiableSet(EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

    /** Digits-only identifier, unique across the clinic. */
    private final String id;

    /** Full name as it should appear on an appointment. */
    private final String name;

    /**
     * The fixed set of days this professional works each week. An {@link EnumSet} is used
     * because the days of the week are a known, fixed collection of values: it rejects
     * anything that is not a real day, cannot hold duplicates, and always iterates in
     * Monday-to-Sunday order.
     */
    private final Set<DayOfWeek> availableDays;

    /**
     * Creates a professional who works the standard Monday-to-Friday week.
     *
     * <p>This is the first of two constructors. A caller who does not care about the
     * roster should not have to build a set of days by hand, so this constructor supplies
     * a sensible default and delegates to the full constructor. Delegation via
     * {@code this(...)} keeps all validation in exactly one place.</p>
     *
     * @param id   digits-only identifier, for example {@code "G1001"}
     * @param name the professional's full name
     * @throws IllegalArgumentException if the ID is blank or not digits only, or the name is blank
     */
    protected HealthProfessional(String id, String name) {
        this(id, name, DEFAULT_AVAILABILITY);
    }

    /**
     * Creates a professional with an explicit weekly roster.
     *
     * <p>This is the only constructor that touches the fields, so it is the single place
     * where the object's invariants are enforced.</p>
     *
     * @param id            digits-only identifier, for example {@code "N5001"}
     * @param name          the professional's full name
     * @param availableDays the days worked each week; must contain at least one day
     * @throws IllegalArgumentException if any argument would leave the object invalid
     */
    protected HealthProfessional(String id, String name, Set<DayOfWeek> availableDays) {
        this.id = requireNumericId(id);
        this.name = requireText(name, "Health professional name");
        this.availableDays = copyAvailability(availableDays);
    }

    /**
     * Returns the kind of professional this object is, for example
     * {@code "General Practitioner"} or {@code "Specialist (Paediatrics)"}.
     *
     * <p>Each subclass must answer this itself, which is what allows a mixed collection
     * to describe every member correctly through a single loop.</p>
     *
     * @return a human-readable role name; never {@code null}
     */
    public abstract String getProfessionalType();

    /**
     * Describes the services this professional offers.
     *
     * <p>Declared abstract rather than as a default implementation because a generic
     * description inherited by everyone would defeat the purpose: the clinic needs to
     * know what each role actually does.</p>
     *
     * @return a short description of the role's services; never {@code null}
     */
    public abstract String describeServices();

    /**
     * @return the digits-only identifier
     */
    public String getId() {
        return id;
    }

    /**
     * @return the professional's full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the days this professional works.
     *
     * <p>The returned set is unmodifiable, so reading the roster cannot be used to change
     * it. A caller who needs to modify a copy can do so with {@code EnumSet.copyOf(...)}.</p>
     *
     * @return an unmodifiable, Monday-first set of working days; never empty
     */
    public Set<DayOfWeek> getAvailableDays() {
        return availableDays;
    }

    /**
     * Tests whether this professional works on a given day.
     *
     * @param day the day to test; {@code null} yields {@code false}
     * @return {@code true} if the professional is rostered on that day
     */
    public boolean isAvailableOn(DayOfWeek day) {
        return day != null && availableDays.contains(day);
    }

    /**
     * Renders the roster as a short comma-separated list, for example {@code "Mon, Wed, Fri"}.
     *
     * @return a printable summary of the working week
     */
    protected String formatWorkingDays() {
        return availableDays.stream()
                .map(day -> day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.joining(", "));
    }

    /**
     * Builds one aligned "label : value" line so that base and subclass output share the
     * same layout instead of every class inventing its own spacing.
     *
     * @param label the left-hand label
     * @param value the value to show
     * @return a formatted line ending with a line separator
     */
    protected static String detailLine(String label, Object value) {
        return String.format("   %-13s: %s%n", label, value);
    }

    /**
     * Returns a printable summary of the details shared by every professional.
     *
     * <p>Subclasses extend this by concatenating {@code super.toString()} with their own
     * extra lines through {@link #detailLine(String, Object)}. That way the shared part of
     * the output is written once, and adding a new role cannot break the existing format.</p>
     *
     * @return a multi-line description ending with a line separator
     */
    @Override
    public String toString() {
        return String.format("[%s] %s%n", id, name)
                + detailLine("Role", getProfessionalType())
                + detailLine("Working days", formatWorkingDays())
                + detailLine("Services", describeServices());
    }

    /**
     * Compares professionals by identifier.
     *
     * <p>Two objects are the same professional when the clinic has given them the same ID,
     * not when they are the same object in memory. Part 5 depends on this: deciding
     * whether an appointment would double-book someone means comparing the professional
     * <em>stored in</em> the appointment with the one being requested.</p>
     *
     * @param other the object to compare with
     * @return {@code true} if {@code other} is a health professional with the same ID
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional otherProfessional)) {
            return false;
        }
        return id.equals(otherProfessional.id);
    }

    /**
     * @return a hash consistent with {@link #equals(Object)}, based on the ID only
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* ------------------------------------------------------------------ */
    /* Validation helpers - shared by this class and every subclass        */
    /* ------------------------------------------------------------------ */

    /**
     * Validates a required text field.
     *
     * @param value     the candidate value
     * @param fieldName the field name used in the error message
     * @return the trimmed value
     * @throws IllegalArgumentException if the value is {@code null} or blank
     */
    protected static String requireText(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        return trimmed;
    }

    /**
     * Validates that an identifier contains digits only.
     *
     * @param id the candidate identifier
     * @return the trimmed identifier
     * @throws IllegalArgumentException if the identifier is missing, blank or not digits only
     */
    protected static String requireNumericId(String id) {
        String trimmed = requireText(id, "Health professional ID");
        if (!trimmed.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "Health professional ID must contain numbers only, but was \"" + trimmed + "\".");
        }
        return trimmed;
    }

    /**
     * Validates that a numeric detail falls inside an allowed range.
     *
     * @param value     the candidate value
     * @param min       the smallest allowed value
     * @param max       the largest allowed value
     * @param fieldName the field name used in the error message
     * @return the validated value
     * @throws IllegalArgumentException if the value is outside the range
     */
    protected static int requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be between %d and %d, but was %d.", fieldName, min, max, value));
        }
        return value;
    }

    /**
     * Copies the supplied roster into a private, unmodifiable {@link EnumSet}.
     *
     * <p>Copying is what stops external code from keeping a reference to the caller's set
     * and editing the professional's roster afterwards - the classic encapsulation leak.</p>
     *
     * @param days the caller's set of days
     * @return an unmodifiable copy containing at least one day
     * @throws IllegalArgumentException if the set is {@code null}, empty or contains {@code null}
     */
    private static Set<DayOfWeek> copyAvailability(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("A health professional must work on at least one day per week.");
        }
        EnumSet<DayOfWeek> copy = EnumSet.noneOf(DayOfWeek.class);
        for (DayOfWeek day : days) {
            if (day == null) {
                throw new IllegalArgumentException("Working days cannot contain a null value.");
            }
            copy.add(day);
        }
        return Collections.unmodifiableSet(copy);
    }
}
