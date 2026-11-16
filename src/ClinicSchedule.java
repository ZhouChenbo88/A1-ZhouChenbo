import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The fixed daily timetable the clinic books against: opening time, closing time, and the
 * length of one consultation slot.
 *
 * <p><b>Why the schedule is an object rather than constants in the booking code.</b> Part 5
 * asks for a fixed 08:00-16:00 day in 30-minute steps, but also asks how the design would
 * cope if that changed. If the booking logic contained {@code LocalTime.of(8, 0)} and
 * {@code 30} scattered through it, changing the clinic hours would mean hunting every one
 * of them and hoping none was missed. Here the numbers live in one constructor, the set of
 * legal slots is derived from them once, and every booking question - "is 09:15 a real
 * slot?" - is answered by that derived set. Under a new timetable the only change is
 * {@link #standard()} or one {@code new ClinicSchedule(...)} call.</p>
 *
 * <p>The object is immutable: the list of slots is built once and handed out unmodifiable,
 * so no caller can insert a 09:15 appointment into the timetable.</p>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public final class ClinicSchedule {

    /** Opening time of the standard clinic day. */
    private static final LocalTime DEFAULT_OPENING = LocalTime.of(8, 0);

    /** Closing time of the standard clinic day. */
    private static final LocalTime DEFAULT_CLOSING = LocalTime.of(16, 0);

    /** Length of one slot in the standard clinic day. */
    private static final int DEFAULT_SLOT_MINUTES = 30;

    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotMinutes;
    private final List<LocalTime> slots;

    /**
     * Creates the timetable described in the brief: 08:00 to 16:00 in 30-minute steps.
     *
     * @return the standard clinic schedule
     */
    public static ClinicSchedule standard() {
        return new ClinicSchedule(DEFAULT_OPENING, DEFAULT_CLOSING, DEFAULT_SLOT_MINUTES);
    }

    /**
     * Creates a timetable with explicit hours.
     *
     * <p>Slots are generated from the opening time while a full slot still fits before
     * closing, which is why a 08:00-16:00 day with 30-minute slots produces sixteen start
     * times (08:00 to 15:30) and not seventeen: an appointment that starts at 16:00 would
     * finish after the clinic has closed.</p>
     *
     * @param openingTime the first bookable start time
     * @param closingTime the time the clinic closes; the last slot must end by then
     * @param slotMinutes the length of one slot, 5 to 240 minutes
     * @throws IllegalArgumentException if the hours are missing, reversed or the slot
     *                                  length is out of range
     */
    public ClinicSchedule(LocalTime openingTime, LocalTime closingTime, int slotMinutes) {
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("Opening and closing times are required.");
        }
        if (!closingTime.isAfter(openingTime)) {
            throw new IllegalArgumentException("Closing time must be after opening time.");
        }
        if (slotMinutes < 5 || slotMinutes > 240) {
            throw new IllegalArgumentException("Slot length must be between 5 and 240 minutes.");
        }
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotMinutes = slotMinutes;
        this.slots = Collections.unmodifiableList(buildSlots());
    }

    /**
     * Derives every legal start time from the three configured values.
     *
     * @return an ordered, unmodifiable list of slot start times
     */
    private List<LocalTime> buildSlots() {
        List<LocalTime> generated = new ArrayList<>();
        LocalTime candidate = openingTime;
        while (!candidate.plusMinutes(slotMinutes).isAfter(closingTime)) {
            generated.add(candidate);
            candidate = candidate.plusMinutes(slotMinutes);
        }
        return generated;
    }

    /**
     * Tests whether a time is one of the clinic's real slots.
     *
     * <p>This single question rejects both "outside opening hours" and "not on the grid" -
     * a 17:00 booking and a 09:15 booking fail for the same reason, so the booking code
     * needs one check rather than two.</p>
     *
     * @param time the time to test; {@code null} yields {@code false}
     * @return {@code true} if an appointment may start at that time
     */
    public boolean isSlot(LocalTime time) {
        return time != null && slots.contains(time);
    }

    /**
     * @return the first bookable start time
     */
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    /**
     * @return the time the clinic closes
     */
    public LocalTime getClosingTime() {
        return closingTime;
    }

    /**
     * @return the length of one slot in minutes
     */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /**
     * @return an unmodifiable list of every legal start time, earliest first
     */
    public List<LocalTime> getSlots() {
        return slots;
    }

    /**
     * Describes the timetable in one line, for example
     * {@code "08:00-16:00, 30-minute slots, 16 slots per day"}.
     *
     * @return a printable summary of the timetable
     */
    public String describe() {
        return String.format("%s-%s, %d-minute slots, %d slots per day",
                openingTime, closingTime, slotMinutes, slots.size());
    }

    /**
     * Returns the next legal slot at or after a given time, which is useful when a
     * requested time is already taken.
     *
     * @param time the requested time
     * @return the next bookable start time
     * @throws NoSuchElementException if there is no slot left that day
     */
    public LocalTime nextSlotAfter(LocalTime time) {
        return slots.stream()
                .filter(slot -> !slot.isBefore(time))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("The clinic day has no slots left."));
    }
}
