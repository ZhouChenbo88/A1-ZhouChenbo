# A1-ZhouChenbo — Health Service Appointment System

**PROG2004 Object Oriented Programming — Assessment 1**
Zhou Chenbo — 25259780

A Java console application that models the backbone of a health service appointment
system: health professionals of different roles, patients, appointments, and the rules that
keep the appointment book consistent. There is no user interface, no login and no
persistence, as the brief specifies — the demonstration runs from `main` and prints its
results.

---

## Build and run

Pure JDK, no third-party dependencies (tested on JDK 21).

```bash
# from the project root
mkdir -p out
javac -d out src/*.java
java -cp out AssignmentOne
```

In IntelliJ IDEA: **Open** the project folder, mark `src` as the sources root, and run
`AssignmentOne`.

---

## Class design

### Health professionals (Parts 1–2)

```
                        Prescribable  (interface)
                              ▲
                              │ implements
   ┌──────────────────┬───────┴────────┬──────────────┐
GeneralPractitioner  Specialist      Surgeon      (Nurse)   (Dietitian)
   │                   │               │
   └───────────────────┴───────────────┴───────────────┐
                        │ extends                     │
                 HealthProfessional  (abstract)        │
                        ▲                              │
                        │ implements                   │
                   BloodSamplable  (interface) ◄── Nurse
```

| Class | Role | Role-specific state | Role-specific behaviour |
|---|---|---|---|
| `HealthProfessional` | abstract base | `id`, `name`, `availableDays` | abstract `getProfessionalType()`, `describeServices()`; shared `isAvailableOn()`, `toString()`, `equals()` |
| `GeneralPractitioner` | first point of contact | experience, accepting new patients | `prescribe()` |
| `Specialist` | paediatrics, cardiology, … | specialty, referral policy | `prescribe()`, `requestInvestigation()` |
| `Surgeon` | operating theatre | theatre, procedures | `prescribe()`, `scheduleProcedure()` |
| `Dietitian` | medical nutrition | focus, consultation length | `designMealPlan()` |
| `Nurse` | ward and outpatient care | ward, years of service | `takeBloodSample()` |

### Appointments (Parts 4–5)

```
  ClinicSchedule ──────┐  owns the timetable: opening, closing, slot length
                       │  and therefore the set of legal start times
                       ▼
  AppointmentManager ──── List<Appointment>   (ArrayList, sorted on demand)
         │  ▲
         │  └──────── rejects: missing details, off-grid time, double booking
         ▼
    Appointment  ◆────── Patient              (record: name + phone)
      │          ◆────── HealthProfessional   (any of the five roles)
      │
      └── Comparable<Appointment>   orders by time, tie-break by professional ID
```

| Class | Responsibility |
|---|---|
| `Patient` | record holding name and contact number; validates itself; `phoneKey()` allows formatting-insensitive search |
| `ClinicSchedule` | the fixed 08:00–16:00 / 30-minute timetable; derives the legal slots once and answers `isSlot()` |
| `Appointment` | one booking: patient + professional + `LocalTime`; comparable; cannot exist incomplete |
| `AppointmentManager` | the book: add, list, filter by professional or phone, sort, cancel by slot or phone — every operation returns a message |

### Why this shape

**Abstract base class, not an instantiable one.** A professional with no role cannot be
rostered or booked, so `HealthProfessional` refuses to be instantiated and forces every
subclass to answer `getProfessionalType()` and `describeServices()`. The rule and the
contract are the same line of code.

**Inheritance for "is-a", interfaces for "can-do".** A surgeon *is* a health professional,
so `Surgeon extends HealthProfessional`. Prescribing is not a kind of professional, it is
something a professional *can do*, so it is `Prescribable` — implemented by the GP,
specialist and surgeon, deliberately **not** by the dietitian or nurse. Folding prescribing
into the base class would have given nurses a method they must not perform; a second
superclass would have been impossible. Part 3 exploits this: the roster is filtered with
`instanceof Prescribable` without naming a single concrete class.

**`EnumSet<DayOfWeek>` for the roster.** Days are a fixed, known collection of values, so an
`EnumSet` rejects anything that is not a real day, forbids duplicates, and always iterates
Monday-first — a `String[]` would allow `"Funday"` twice.

**Encapsulation as an invariant.** Every field is `private final`; validation happens only
in the constructor; `getAvailableDays()` returns an unmodifiable view and the constructor
copies the caller's set, so no external reference can mutate the roster afterwards. There is
no setter that could produce an invalid professional. Section 3.5 proves the guards work by
trying to break them.

**Two constructors per class.** Each role offers a short constructor (sensible defaults) and
a full one, both ending in `super(...)`, so shared state is initialised in one place and
callers are never forced to supply everything.

**Equality by ID.** `equals`/`hashCode` on `HealthProfessional` compare the identifier,
because two objects representing the same clinician must count as the same person when
checking whether a booking would double-book them.

**Composition, not inheritance, for appointments.** An appointment *has* a patient, *has* a
professional and *has* a time; it is none of those things. The professional field is typed as
the abstract base class, which is what allows one appointment class to serve all five roles
— the payoff for the Part 1–2 hierarchy.

**`LocalTime` and `Comparable` for ordering.** Times are stored as `LocalTime`, not as text,
so `09:00` sorts before `10:30` without any parsing, and `Appointment` implements
`Comparable<Appointment>` so a whole collection sorts with `sorted()` and no external
comparator. Ties at the same time are broken by professional ID, which keeps the output
stable between runs.

**The timetable as an object.** If `LocalTime.of(8, 0)` and `30` were scattered through the
booking code, changing clinic hours would mean hunting every occurrence. They live in
`ClinicSchedule`, which derives the legal slots once; the booking code only ever asks
`isSlot(time)`. A new timetable is one constructor call.

**`ArrayList`, not a map.** The book is small, ordered, and read more often than it is
searched by key. A linear scan over a few dozen entries costs nothing, and an `ArrayList`
keeps insertion order while allowing explicit "same professional, same slot" reasoning. If
the clinic ever needed thousands of bookings a day, the private field is the only thing that
would change.

**Every operation reports back.** The brief requires a warning when an action cannot be
performed, so mutating methods return a message rather than `void`, and the manager never
prints — output stays the caller's decision.

---

## Demonstration walkthrough

`AssignmentOne.main` prints a numbered walkthrough; each section is its own method.

**Part 3 — health professionals**

| Section | What it shows |
|---|---|
| 3.1 | Nine professionals printed through the base type |
| 3.2 | One loop, role-specific answer from each object (dynamic dispatch) |
| 3.3 | Capability interfaces — who can prescribe, who can collect blood |
| 3.4 | The weekly roster as a grid, plus who is rostered today |
| 3.5 | Guard rails — four attempts to create invalid objects, all rejected |

**Part 6 — appointments**

| Section | What it shows |
|---|---|
| 6.1 | The timetable: hours, slot length, and every legal start time |
| 6.2 | Seven bookings across all five roles; same time for two professionals is allowed |
| 6.3 | The whole book, sorted earliest to latest |
| 6.4 | Filtering by professional ID, including a professional with no bookings |
| 6.5 | Filtering by phone, typed with and without spaces — same result |
| 6.6 | Cancelling by professional ID + time, then re-booking the freed slot |
| 6.7 | Cancelling by phone (two appointments at once) and the book afterwards |
| 6.8 | Six operations that must fail: double booking, 09:15 (off grid), 17:00 (after hours), no professional, cancelling nothing twice |
| 6.9 | Final state of the book |

---

## Constraints enforced

| Rule from the brief | Where |
|---|---|
| ID consists of numbers only | `HealthProfessional.requireNumericId()` |
| Controlled access, no invalid state | `private final` fields, constructor-only validation, unmodifiable views |
| Fixed weekly working days stored appropriately | `EnumSet<DayOfWeek>` |
| At least four distinct roles, one being a GP | five roles: GP, specialist, surgeon, dietitian, nurse |
| Each role states what kind of professional it is | `getProfessionalType()` |
| An appointment works with any professional type | `Appointment` holds a `HealthProfessional` |
| An appointment cannot be incomplete | constructor refuses missing patient, professional or time |
| Ordering a day's appointments by time | `LocalTime` + `Comparable<Appointment>` |
| Fixed 08:00–16:00 schedule in 30-minute steps | `ClinicSchedule.standard()` |
| Same professional never booked twice in a slot | `AppointmentManager.isDoubleBooked()` |
| Filter by professional ID and by patient phone | `appointmentsForProfessional()`, `appointmentsForPatient()` |
| Sort all or a filtered subset | every list method returns `sorted()` |
| Cancel by professional + time, and by phone | `cancelByProfessionalAndTime()`, `cancelByPatientPhone()` |
| A warning when an action cannot be performed | every mutating method returns a message |

### One judgement call

The brief limits bookings to the current day and requires double-booking to be refused, but
does not say what to do when the chosen professional does not normally work today. Rejecting
the booking would make the demonstration unusable on a weekend, so a roster mismatch is
reported instead: the booking succeeds with an appended note that it is recorded as a
waitlist request. The roster rule still exists and is exercised in Part 3.4.
