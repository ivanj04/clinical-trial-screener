# Clinical Trial Eligibility Screener

A production-shaped Spring Boot 3 / Java 17 REST service that lets clinical operations staff define trial inclusion/exclusion criteria and screen patient profiles against them in real time.

---

## BMS Clinical Data Ecosystem Relevance

Bristol Myers Squibb's Clinical Data Insight (CDI) platform aggregates trial protocol data, patient registries, and biomarker results across global studies. This service models core capabilities of that ecosystem:

| CDI Capability | This Service |
|---|---|
| Protocol-driven eligibility rules | `ScreeningEngine` rules evaluated per criterion type |
| Flexible patient data model | MongoDB document store — lab values as `Map<String,Double>`, diagnoses as `List<String>` |
| Batch patient-to-trial matching | `POST /api/screen/batch` screens one patient against all recruiting trials in one call |
| Audit trail | `ScreeningResult` persisted with timestamp for every evaluation |
| Secure API access | Spring Security 6 + JWT; role-based method security |

---

## Architecture

```
┌────────────────────────────────────────────────────────────┐
│  REST Layer  (AuthController / TrialController /           │
│              PatientController / ScreeningController)       │
├────────────────────────────────────────────────────────────┤
│  Service Layer                                              │
│    TrialService  PatientService  AuthService               │
│    ScreeningService ──► ScreeningEngine (rules core)       │
├────────────────────────────────────────────────────────────┤
│  Repository Layer  (Spring Data MongoDB)                    │
│    ClinicalTrialRepository  PatientProfileRepository        │
│    ScreeningResultRepository  UserRepository               │
├────────────────────────────────────────────────────────────┤
│  MongoDB  (Docker / Atlas)                                  │
└────────────────────────────────────────────────────────────┘
```

### Rules Engine Design

`ScreeningEngine.evaluateCriterion(Criterion, PatientProfile)` is a pure, stateless evaluator.  
Each `Criterion` carries a `CriterionType` (AGE_MIN, DIAGNOSIS, LAB_VALUE, …) and an `Operator` (EQUALS, GREATER_THAN, CONTAINS, …).  

Eligibility logic in `ScreeningService`:
- Patient is **eligible** if **ALL** inclusion criteria pass **AND** zero exclusion criteria are triggered.
- Results (matched inclusion IDs, triggered exclusion IDs) are persisted as `ScreeningResult` for audit.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17, Spring Boot 3.3 |
| Persistence | Spring Data MongoDB |
| Security | Spring Security 6, JJWT 0.12 (HS256) |
| API Docs | springdoc-openapi 2 (Swagger UI at `/swagger-ui.html`) |
| Testing | JUnit 5, flapdoodle embedded MongoDB |
| Containerisation | Docker, docker-compose |
| CI | GitHub Actions, Jenkinsfile |

---

## Quick Start

```bash
# Start MongoDB + app
docker-compose up -d

# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"ops1","password":"pass123","email":"ops@example.com"}'

# Login and capture token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"ops1","password":"pass123"}' | jq -r .token)

# Screen pre-seeded patient against pre-seeded trial
curl -X POST http://localhost:8080/api/screen \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"trialId":"NCT-BMS-001","patientId":"PT-10001"}'

# Batch screen patient against all recruiting trials
curl -X POST http://localhost:8080/api/screen/batch \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"patientId":"PT-10001"}'
```

Swagger UI: http://localhost:8080/swagger-ui.html

---

## Running Tests

```bash
mvn verify
```

Tests use flapdoodle embedded MongoDB — no external database required.

---

## Configuration

| Env Var | Default | Description |
|---|---|---|
| `MONGO_URI` | `mongodb://localhost:27017/ctscreener` | MongoDB connection string |
| `JWT_SECRET` | `supersecretkey32charsminimumhere!` | HMAC-SHA256 signing secret (min 32 chars) |
| `SPRING_PROFILES_ACTIVE` | `dev` | Use `dev` to seed sample data on startup |

---

## Sample Data (dev profile)

Two trials are seeded automatically:
- **NCT-BMS-001** — Nivolumab NSCLC, Phase III, RECRUITING
- **NCT-BMS-002** — Ipilimumab Melanoma, Phase II, RECRUITING

Three patients:
- **PT-10001** — eligible for NCT-BMS-001
- **PT-10002** — eligible for NCT-BMS-002
- **PT-10003** — ineligible for both (age > 75, autoimmune exclusion, high ECOG)
