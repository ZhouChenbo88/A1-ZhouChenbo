# A1-ZhouChenbo — Health Service Appointment System

**PROG2004 Object Oriented Programming — Assessment 1**
Zhou Chenbo — 25259780

A Java console application that models the backbone of a health service appointment
system: health professionals of different roles, patients, appointments, and the rules
that keep the appointment book consistent. There is no user interface, no login and no
persistence, as the brief specifies — the demonstration runs from `main`.

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
| `HealthProfessional` | abstract base | `id`, `name`, `availableDays` | `getProfessionalType()`, `describeServices()` abstract; `isAvailableOn()`, `toString()`, `equals()` shared |
| `GeneralPractitioner` | first point of contact | experience, accepting new patients | `prescribe()` |
| `Specialist` | paediatrics, cardiology, … | specialty, referral policy | `prescribe()`, `requestInvestigation()` |
| `Surgeon` | operating theatre | theatre, procedures | `prescribe()`, `scheduleProcedure()` |
| `Dietitian` | medical nutrition | focus, consultation length | `designMealPlan()` |
| `Nurse` | ward and outpatient care | ward, years of service | `takeBloodSample()` |

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
copies the caller's set, so no external reference can mutate the roster afterwards. There
is no setter that could produce an invalid professional. Section 3.5 of the demonstration
proves the guards work by trying to break them.

**Two constructors per class.** Each role offers a short constructor (sensible defaults)
and a full one, both ending in `super(...)`, so shared state is initialised in one place
and callers are never forced to supply everything.

**Equality by ID.** `equals`/`hashCode` compare the identifier, because two objects
representing the same clinician must be treated as the same person when checking whether a
booking would double-book them (Part 5).

---

## Demonstration output

`AssignmentOne.main` prints a numbered walkthrough. Implemented so far (Day 1):

- **3.1** every professional printed through the base type
- **3.2** one loop, role-specific answer from each object (dynamic dispatch)
- **3.3** capability interfaces — who can prescribe, who can collect blood
- **3.4** the weekly roster as a grid, plus who is rostered today
- **3.5** guard rails — four attempts to create invalid objects, all rejected

Parts 4–6 (appointments, the booking manager and the end-to-end demonstration) are the
next stage; see `PLAN.md`.

---

## Constraints enforced so far

| Rule from the brief | Where |
|---|---|
| ID consists of numbers only | `HealthProfessional.requireNumericId()` |
| Controlled access, no invalid state | `private final` fields, constructor-only validation, unmodifiable views |
| Every professional identifiable and named | `HealthProfessional` state |
| Fixed weekly working days stored appropriately | `EnumSet<DayOfWeek>` |
| At least four distinct roles, one being a GP | five roles: GP, specialist, surgeon, dietitian, nurse |
| Each role states what kind of professional it is | `getProfessionalType()` |
| Printing shows all details readably | `toString()` chain, `detailLine()` |
