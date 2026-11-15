# A1-ZhouChenbo — Two-Day HD Delivery Plan

**Unit:** PROG2004 Object Oriented Programming — Assessment 1 (40%, individual)
**Student:** Zhou Chenbo — 25259780
**Due:** Monday 16 November 2026, 11:59 pm
**Work window:** Day 1 = Sun 15 Nov (tonight), Day 2 = Mon 16 Nov

The whole mark is in six rubric rows, so the plan below is built backwards from them:
design depth first (Parts 1–2 are worth 30%), then the system behaviour (Parts 4–5 are
worth 30%), then evidence of process (GitHub / WakaTime, 10%) and the video (10%).

---

## Day 1 (tonight) — Parts 1–3: the health professional model

**Goal at the end of Day 1:** a compiling project with a real inheritance hierarchy,
five concrete roles, two contracts, and a console demonstration of Part 3 — plus a
GitHub repository that already shows incremental progress.

| # | Task | Output | Rubric row |
|---|------|--------|-----------|
| 1 | Create `A1-ZhouChenbo` project, `src/`, `.gitignore`, `git init`, first commit | repo skeleton | Professional practice |
| 2 | **Part 1** `HealthProfessional`: `id` (digits only) + `name` + fixed working-week set, two constructors, `toString`, `equals/hashCode`, no setters | `HealthProfessional.java` | Modelling a health professional (10%) |
| 3 | **Part 2** abstract subclassing: `GeneralPractitioner`, `Specialist`, `Surgeon`, `Dietitian`, `Nurse` + `Prescribable`, `BloodSamplable` interfaces | 7 files | Specialising health professionals (20%) |
| 4 | **Part 3** `AssignmentOne.main`: 9 objects stored through the base type, printed, plus dynamic dispatch and interface demos | `AssignmentOne.java` | Working with health professional objects (10%) |
| 5 | Compile clean, run, capture console output, commit per Part and push | 4 commits | Professional practice |

**Design decisions locked in tonight (these are the ones the video will defend):**

1. `HealthProfessional` is **abstract**, not a plain class — a professional with no role is
   meaningless, so the base type refuses to be instantiated and *forces* each subclass to
   answer `getProfessionalType()` and `describeServices()`.
2. Working days are an `EnumSet<DayOfWeek>`, not a `String[]` — a fixed collection of known
   values, ordered, no duplicates, and it cannot hold "Funday".
3. **Encapsulation as an invariant, not just getters:** every field is `private final`, the
   constructor is the only place validation happens, and `getAvailableDays()` hands back an
   unmodifiable view. There is no setter that can create an invalid professional.
4. **Two abstraction tools used for two different jobs:** *inheritance* for "is-a"
   (a surgeon **is** a health professional), *interfaces* for "can-do" (a surgeon and a GP
   can both prescribe, but they are not related to each other). This is the single strongest
   signal of design maturity in the rubric.
5. **Constructor chaining:** every subclass offers a short and a full constructor, both
   ending in `super(...)`, so shared state is initialised in exactly one place.

**Day 1 definition of done**

- [ ] `javac` compiles the whole `src/` with no errors and no warnings
- [ ] `java AssignmentOne` prints 9 professionals with role, days, services, role-specific detail
- [ ] `git log --oneline` shows 4 descriptive commits from today
- [ ] Repository is **private** on GitHub; WakaTime plugin installed and logging

---

## Day 2 (Monday) — Parts 4–6 and submission

**Goal at the end of Day 2:** booking engine working end-to-end, evidence pack assembled,
video recorded, zip uploaded before 11:59 pm.

| # | Time | Task | Output | Rubric row |
|---|------|------|--------|-----------|
| 1 | 09:00–09:40 | **Part 4** `Patient` (record) + `Appointment` (`LocalTime`, `Comparable<Appointment>`, full validation, holds any `HealthProfessional`) | 2 files | Modelling an appointment (15%) |
| 2 | 09:40–11:10 | **Part 5** `AppointmentManager`: `ArrayList<Appointment>`, `Schedule` of 08:00–16:00 slots in 30-min steps, add with double-booking rejection, filter by professional ID / phone, sort by time, cancel by ID+time / phone, every operation returns a message | 2 files | Managing the collection (15%) |
| 3 | 11:10–12:10 | **Part 6** extend `main`: happy path + every error path (double booking, unknown professional, slot outside schedule, cancel that does not exist, malformed phone) | `AssignmentOne.java` | Working with appointments (10%) |
| 4 | 12:10–13:00 | `README.md`: design rationale, class diagram, pillar map, run instructions | docs | Professional practice |
| 5 | 14:00–15:30 | `docs/OOP-PILLARS.md`: four pillars → exact file + line + *why* | docs | Video (10%) |
| 6 | 15:30–17:00 | Record the ≤5 min video, rehearse twice, upload to SCU OneDrive, copy share link | link | Video (10%) |
| 7 | 17:00–18:00 | WakaTime report → `A1-ZhouChenbo-WakaTime.pdf`, final commits, `A1-ZhouChenbo.zip` | zip + pdf | Professional practice |
| 8 | 18:00–19:00 | Submit on Blackboard: zip, repo link, WakaTime, video link, GenAI declaration. Verify the upload actually appears. | submission | — |
| 9 | Buffer | Re-read rubric row by row against the code; fix anything that is only "Credit" level | — | all |

---

## HD checklist — what the top band actually demands

| Rubric row | Weight | What "exceptional" looks like | Where it is proven |
|---|---|---|---|
| Part 1 modelling | 10% | Not just fields + getters: validation, immutability, overloaded constructors, meaningful `toString`, `equals/hashCode` | `HealthProfessional.java` |
| Part 2 specialising | 20% | Abstract base + 5 roles + 2 interfaces; no duplicated code; each type adds real state *and* behaviour; `super.toString()` reuse | 5 subclasses + 2 interfaces |
| Part 3 working with objects | 10% | One collection of the base type, one loop, dynamic dispatch, no `if/else` on type | `AssignmentOne` Part 3 section |
| Part 4 appointment | 15% | Composition (`Patient` inside `Appointment`), `LocalTime` for correct ordering, `Comparable`, cannot exist in an invalid state | `Appointment.java` |
| Part 5 managing | 15% | Defensible data structure choice, schedule as data, `equals` by ID, double-booking rejected, every failure returns a message | `AppointmentManager.java` |
| Part 6 demonstration | 10% | Both normal **and** error cases, filtering by two different keys, cancelling by two different keys, before/after view | `main` sections 4–8 |
| Video | 10% | Four pillars named, explained in own words, pointed at, and justified | `docs/OOP-PILLARS.md` script |
| Professional practice | 10% | Frequent descriptive commits, continuous WakaTime, Javadoc everywhere | `git log`, WakaTime report |

---

## Evidence of process (do not skip — 10% and "may fail the assignment" if missing)

- **GitHub:** commits must land on **both** days. Day 1 gives 4, Day 2 should give 8–12
  (one per part, plus doc and zip commits). Every message says *what* and *why*.
- **WakaTime:** VS Code → Extensions → `WakaTime` → install → paste API key from
  `wakatime.com/api-key` → reload. Verify by opening a `.java` file and waiting ~60 s, then
  checking the dashboard shows the project. Screenshot the dashboard **last**, so it covers
  both days.
- **Video:** ≤5 minutes, screen + voice, four pillars in order. Structure is in
  `docs/OOP-PILLARS.md`. Upload to SCU OneDrive, set "anyone with the link can view".
- **GenAI declaration:** declare the tools you used in the Blackboard comments. Marks are
  not lost for declaring; they are lost for declaring falsely.

## Risk register

| Risk | Mitigation |
|---|---|
| Day 2 overruns | Parts 4–6 are the biggest block (40%); if time is short, ship filtering by ID + phone and cancelling by both keys first, then polish |
| Video over 5 min | Script is pre-timed at 4:30 with a per-pillar budget |
| Commit history looks like one dump | Commit as you finish each part, not at the end |
| `equals` by ID forgotten | Part 5 hinges on it — already implemented in Part 1 |
