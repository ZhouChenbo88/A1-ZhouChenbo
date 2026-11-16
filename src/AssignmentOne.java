import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

/**
 * Demonstration driver for the health service appointment system.
 *
 * <p>The assessment asks for a scripted simulation in {@code main} rather than a user
 * interface: the code below builds objects and exercises their behaviour, printing the
 * results, in place of a real receptionist using a real screen.</p>
 *
 * <p><b>Run order.</b> Each block of the demonstration is isolated in its own private
 * method and prints its own heading, so the console reads as a numbered walkthrough:</p>
 * <ol>
 *   <li>Part 3 - create and display every health professional</li>
 *   <li>Part 6 - book, list, filter, sort, cancel, and every way those can fail</li>
 * </ol>
 *
 * @author Zhou Chenbo (25259780)
 * @version 1.0
 */
public class AssignmentOne {

    /** Used in the banner so the submission is self-identifying. */
    private static final String STUDENT_NAME = "Zhou Chenbo";

    /** Student number, printed with every run so a marker can match output to a person. */
    private static final String STUDENT_ID = "25259780";

    /** Horizontal rule used to separate blocks of console output. */
    private static final String DIVIDER = "-".repeat(78);

    /**
     * Column headings for an appointment list, built with the same format string as
     * {@link Appointment#toString()} so the headings always line up with the rows.
     */
    private static final String APPOINTMENT_HEADER =
            String.format("%s   %-16s %-16s %s", "TIME", "PATIENT", "PHONE", "HEALTH PROFESSIONAL");

    /**
     * Entry point: builds the professional roster and demonstrates Parts 3 and 6.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        printBanner();

        List<HealthProfessional> professionals = buildHealthProfessionals();
        runPartThree(professionals);
        runPartSix(professionals);
    }

    /* ================================================================== */
    /* Part 3 - working with health professional objects                   */
    /* ================================================================== */

    /**
     * Demonstrates that the professional objects created in Parts 1 and 2 can be held,
     * printed and driven through their common base type.
     *
     * @param professionals the roster to work with
     */
    private static void runPartThree(List<HealthProfessional> professionals) {
        printSection("PART 3.1  All health professionals, printed through the base type");
        displayProfessionals(professionals);

        printSection("PART 3.2  One loop, a different answer from every role (dynamic dispatch)");
        showDynamicDispatch(professionals);

        printSection("PART 3.3  Capabilities that cut across the hierarchy (interfaces)");
        showCapabilities(professionals);

        printSection("PART 3.4  The weekly roster - shared state and behaviour from Part 1");
        showAvailability(professionals);

        printSection("PART 3.5  Guard rails - professionals that cannot be created");
        showValidationGuards();
    }

    /**
     * Creates the roster: three general practitioners and one or two of every other role,
     * all stored in a single list of the base type.
     *
     * <p>The list is declared as {@code List<HealthProfessional>}, not as a list of
     * concrete types. Adding a sixth role later means adding one line here and nothing
     * else - no loop changes, no new variables, no new printing code.</p>
     *
     * @return a mutable list containing every professional in the clinic
     */
    private static List<HealthProfessional> buildHealthProfessionals() {
        List<HealthProfessional> professionals = new ArrayList<>();

        // IDs are digits only, as required by the brief. The leading digit identifies the
        // role by convention (1xxx GPs, 2xxx specialists, and so on), but the rule that
        // matters to the system is simply that no two professionals share an ID.

        // --- General practitioners -------------------------------------
        // A full roster, an experienced GP with closed books, and one created with the
        // convenience constructor so the default roster and experience are exercised.
        professionals.add(new GeneralPractitioner("1001", "Dr Alice Nguyen",
                days(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY), 12, true));
        professionals.add(new GeneralPractitioner("1002", "Dr Marcus Bell",
                days(MONDAY, WEDNESDAY, FRIDAY), 6, false));
        professionals.add(new GeneralPractitioner("1003", "Dr Priya Raman"));

        // --- Specialists -------------------------------------------------
        professionals.add(new Specialist("2001", "Dr Elena Fischer",
                days(MONDAY, TUESDAY, THURSDAY), "Paediatrics", true));
        professionals.add(new Specialist("2002", "Dr Samuel Okafor",
                days(TUESDAY, WEDNESDAY), "Cardiology", false));

        // --- Surgeon -----------------------------------------------------
        professionals.add(new Surgeon("3001", "Dr Hannah Cole",
                days(WEDNESDAY, THURSDAY), "Theatre 3", 412));

        // --- Dietitian ---------------------------------------------------
        professionals.add(new Dietitian("4001", "Ms Grace Lim",
                days(MONDAY, TUESDAY, WEDNESDAY), "Diabetes and renal nutrition", 45));

        // --- Nurses ------------------------------------------------------
        professionals.add(new Nurse("5001", "Daniel Wu",
                days(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY), "Ward 2B", 8));
        professionals.add(new Nurse("5002", "Aisha Khan",
                days(THURSDAY, FRIDAY), "Outpatient clinic", 3));

        return professionals;
    }

    /**
     * Prints every professional, using the object's own {@code toString()}.
     *
     * <p>The loop never asks what type it is holding. It calls {@code System.out.print}
     * with a {@code HealthProfessional} reference and the JVM invokes the version of
     * {@code toString()} belonging to the actual object, so each role prints its shared
     * details followed by its own.</p>
     *
     * @param professionals the roster to print
     */
    private static void displayProfessionals(List<HealthProfessional> professionals) {
        int index = 1;
        for (HealthProfessional professional : professionals) {
            System.out.printf("(%d of %d)%n", index++, professionals.size());
            System.out.print(professional);
            System.out.println();
        }
        System.out.printf("Total health professionals on file: %d%n", professionals.size());
    }

    /**
     * Prints the role-specific services of every professional through a single call.
     *
     * <p>{@code describeServices()} is abstract in the base class, so this call cannot be
     * satisfied by a generic answer - the runtime type decides the text. That is
     * polymorphism in its most direct form, and it is why the base class declares the
     * method abstract rather than returning a default string.</p>
     *
     * @param professionals the roster to describe
     */
    private static void showDynamicDispatch(List<HealthProfessional> professionals) {
        for (HealthProfessional professional : professionals) {
            System.out.printf("%-20s %-32s %s%n",
                    professional.getName(),
                    professional.getProfessionalType(),
                    professional.describeServices());
        }
    }

    /**
     * Shows that a capability can be detected and used without knowing the concrete class.
     *
     * <p>Each professional is tested against the two capability interfaces rather than
     * against five concrete classes. Using a pattern-matching {@code instanceof} keeps the
     * loop free of casts, and because the check is against the interface, a role added
     * later participates automatically.</p>
     *
     * @param professionals the roster to test
     */
    private static void showCapabilities(List<HealthProfessional> professionals) {
        System.out.println("Professionals who can prescribe:");
        for (HealthProfessional professional : professionals) {
            if (professional instanceof Prescribable prescriber) {
                System.out.println("  * " + prescriber.prescribe("Amoxicillin", "500 mg three times daily"));
            }
        }

        System.out.println();
        System.out.println("Professionals who can collect a blood sample:");
        for (HealthProfessional professional : professionals) {
            if (professional instanceof BloodSamplable collector) {
                System.out.println("  * " + collector.takeBloodSample("Ethan Park"));
            }
        }
    }

    /**
     * Uses the behaviour inherited from Part 1 on every object, today's date being the
     * only input.
     *
     * <p>{@code isAvailableOn} is defined once in {@link HealthProfessional} and inherited
     * by all five roles, so a single call works for every object in the list. The grid also
     * makes the effect of the {@code EnumSet} visible: each professional owns a fixed,
     * ordered set of days, and nothing outside the class can add to it.</p>
     *
     * @param professionals the roster to check
     */
    private static void showAvailability(List<HealthProfessional> professionals) {
        StringBuilder header = new StringBuilder(String.format("%-20s", "Professional"));
        for (DayOfWeek day : DayOfWeek.values()) {
            header.append(String.format(" %-3s", day.name().substring(0, 3)));
        }
        System.out.println(header);

        for (HealthProfessional professional : professionals) {
            StringBuilder row = new StringBuilder(String.format("%-20s", professional.getName()));
            for (DayOfWeek day : DayOfWeek.values()) {
                row.append(String.format(" %-3s", professional.isAvailableOn(day) ? "on" : "-"));
            }
            System.out.println(row);
        }

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        long rosteredToday = professionals.stream()
                .filter(professional -> professional.isAvailableOn(today))
                .count();
        System.out.printf("%nToday is %s: %d of %d professionals are rostered.%n",
                today, rosteredToday, professionals.size());
        if (rosteredToday == 0) {
            System.out.println("The clinic runs Monday to Friday, so nothing can be booked in today.");
        }
    }

    /**
     * Shows that the validation in Part 1 actually holds, by attempting to build objects
     * that would break the system's rules and reporting what happens.
     *
     * <p>This is not decoration. The assignment asks for objects that can "never be put
     * into an invalid state"; the only way to demonstrate a rule is to try to break it.
     * Each attempt is supplied as a lambda and handed to {@link #reject}, so the four cases
     * share one piece of reporting code instead of four copies of try/catch.</p>
     */
    private static void showValidationGuards() {
        reject("ID containing letters", () -> new GeneralPractitioner("G1001", "Dr Test"));
        reject("blank name", () -> new Nurse("9001", "   ", "Ward 1"));
        reject("no working days", () -> new Dietitian("9002", "Ms Test",
                EnumSet.noneOf(DayOfWeek.class), "Renal nutrition", 30));
        reject("impossible experience", () -> new GeneralPractitioner("9003", "Dr Test",
                days(MONDAY), 99, true));
    }

    /**
     * Attempts to create an object and reports whether the constructor refused.
     *
     * @param description what the attempt is trying to do, used in the output
     * @param attempt     the construction to try
     */
    private static void reject(String description, Supplier<HealthProfessional> attempt) {
        try {
            HealthProfessional created = attempt.get();
            System.out.printf("  [ACCEPTED]   %-26s -> %s (unexpected)%n",
                    description, created.getName());
        } catch (IllegalArgumentException exception) {
            System.out.printf("  [REJECTED]   %-26s -> %s%n", description, exception.getMessage());
        }
    }

    /* ================================================================== */
    /* Part 6 - working with appointments                                  */
    /* ================================================================== */

    /**
     * Drives the appointment system end-to-end: book, list, filter, sort, cancel, and every
     * way those operations can fail.
     *
     * <p>The order matters. Everything that succeeds runs first so the book is populated and
     * the filtering has something to find; the cancellations come next so the before/after
     * view proves the collection changed; the refusals come last, because they are attempts
     * to do something the rules forbid and they leave the book unchanged.</p>
     *
     * @param professionals the roster to book from
     */
    private static void runPartSix(List<HealthProfessional> professionals) {
        AppointmentManager manager = new AppointmentManager();

        HealthProfessional drNguyen = findProfessional(professionals, "1001");
        HealthProfessional drBell = findProfessional(professionals, "1002");
        HealthProfessional drFischer = findProfessional(professionals, "2001");
        HealthProfessional drCole = findProfessional(professionals, "3001");
        HealthProfessional msLim = findProfessional(professionals, "4001");
        HealthProfessional mrWu = findProfessional(professionals, "5001");

        Patient ethan = new Patient("Ethan Park", "0412 345 678");
        Patient mei = new Patient("Mei Lin", "0433 998 211");
        Patient oskar = new Patient("Oskar Jensen", "0455 102 334");

        printSection("PART 6.1  The clinic timetable");
        System.out.println("   " + manager.getSchedule().describe());
        List<String> slotLabels = manager.getSchedule().getSlots().stream()
                .map(LocalTime::toString)
                .toList();
        System.out.println("   " + String.join(", ", slotLabels));

        printSection("PART 6.2  Booking across the different professional types");
        say(manager.addAppointment(ethan, drNguyen, LocalTime.of(9, 0)));
        say(manager.addAppointment(mei, drFischer, LocalTime.of(9, 0)));
        System.out.println("      (09:00 is now taken for 1001 but still free for 2001:"
                + " the rule is per professional.)");
        say(manager.addAppointment(oskar, drNguyen, LocalTime.of(10, 30)));
        say(manager.addAppointment(ethan, msLim, LocalTime.of(11, 0)));
        say(manager.addAppointment(mei, mrWu, LocalTime.of(14, 0)));
        say(manager.addAppointment(ethan, drBell, LocalTime.of(14, 0)));
        say(manager.addAppointment(oskar, drCole, LocalTime.of(15, 0)));

        printSection("PART 6.3  Every appointment, sorted earliest to latest");
        printAppointments("   The book after booking:", manager.allAppointments());

        printSection("PART 6.4  Filtering by health professional ID");
        printAppointments("   Professional 1001 (Dr Alice Nguyen):",
                manager.appointmentsForProfessional("1001"));
        printAppointments("   Professional 2001 (Dr Elena Fischer):",
                manager.appointmentsForProfessional("2001"));
        printAppointments("   Professional 5001 (Daniel Wu):",
                manager.appointmentsForProfessional("5001"));
        printAppointments("   Professional 2002 (Dr Samuel Okafor, nobody has booked):",
                manager.appointmentsForProfessional("2002"));

        printSection("PART 6.5  Filtering by patient phone number");
        printAppointments("   Searching for \"0433 998 211\" (typed with spaces):",
                manager.appointmentsForPatient("0433 998 211"));
        printAppointments("   Searching for \"0433998211\" (typed without spaces):",
                manager.appointmentsForPatient("0433998211"));

        printSection("PART 6.6  Cancelling by professional ID and time slot");
        say(manager.cancelByProfessionalAndTime("1001", LocalTime.of(10, 30)));
        printAppointments("   Professional 1001 after the cancellation:",
                manager.appointmentsForProfessional("1001"));
        say(manager.addAppointment(new Patient("Nina Alvarez", "0422 876 501"),
                drNguyen, LocalTime.of(10, 30)));
        System.out.println("      (the cancelled slot can be booked again immediately.)");

        printSection("PART 6.7  Cancelling by patient phone number");
        say(manager.cancelByPatientPhone("0433998211"));
        printAppointments("   The whole book after the cancellation:", manager.allAppointments());

        printSection("PART 6.8  Operations that cannot be completed");
        say(manager.addAppointment(ethan, drNguyen, LocalTime.of(9, 0)));
        say(manager.addAppointment(ethan, drBell, LocalTime.of(9, 15)));
        say(manager.addAppointment(ethan, drBell, LocalTime.of(17, 0)));
        say(manager.addAppointment(ethan, null, LocalTime.of(12, 0)));
        say(manager.cancelByProfessionalAndTime("2001", LocalTime.of(15, 0)));
        say(manager.cancelByPatientPhone("0499 000 111"));

        printSection("PART 6.9  Final state of the book");
        printAppointments("   " + manager.size() + (manager.size() == 1 ? " appointment" : " appointments")
                + " remain:", manager.allAppointments());
    }

    /**
     * Prints an appointment list with a heading, a column header and a count.
     *
     * @param title        the heading to print
     * @param appointments the appointments to print; an empty list prints a clear message
     */
    private static void printAppointments(String title, List<Appointment> appointments) {
        System.out.println(title);
        if (appointments.isEmpty()) {
            System.out.println("   (no appointments found)");
            System.out.println();
            return;
        }
        System.out.println("   " + APPOINTMENT_HEADER);
        for (Appointment appointment : appointments) {
            System.out.println("   " + appointment);
        }
        System.out.printf("   %d appointment%s shown.%n%n",
                appointments.size(), appointments.size() == 1 ? "" : "s");
    }

    /**
     * Prints the message an operation returned, indented under its section heading.
     *
     * @param message the message returned by the appointment manager
     */
    private static void say(String message) {
        System.out.println("   " + message);
    }

    /**
     * Looks a professional up by ID.
     *
     * @param professionals the roster to search
     * @param id            the ID to find
     * @return the matching professional
     * @throws IllegalArgumentException if no professional has that ID
     */
    private static HealthProfessional findProfessional(List<HealthProfessional> professionals, String id) {
        return professionals.stream()
                .filter(professional -> professional.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No professional with ID " + id));
    }

    /* ================================================================== */
    /* Shared formatting helpers                                           */
    /* ================================================================== */

    /**
     * Builds a {@link Set} of days from varargs.
     *
     * @param days one or more days of the week
     * @return an {@link EnumSet} containing exactly those days
     */
    private static Set<DayOfWeek> days(DayOfWeek... days) {
        return EnumSet.copyOf(List.of(days));
    }

    /**
     * Prints the submission banner.
     */
    private static void printBanner() {
        System.out.println("=".repeat(78));
        System.out.println(" PROG2004 Object Oriented Programming - Assessment 1");
        System.out.println(" Health Service Appointment System");
        System.out.printf(" %s  (%s)%n", STUDENT_NAME, STUDENT_ID);
        System.out.println("=".repeat(78));
    }

    /**
     * Prints a block heading followed by a rule.
     *
     * @param title the heading text
     */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(DIVIDER);
    }
}
