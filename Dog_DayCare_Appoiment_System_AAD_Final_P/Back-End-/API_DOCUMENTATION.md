# Dog Daycare Backend API Documentation

This document describes the current API in `Back-End-`.

Base URL:

- `http://localhost:8080/api/v1`

Authentication:

- JWT Bearer token for protected routes
- Header: `Authorization: Bearer <token>`

---

## 1) Roles and Access

Roles (`UserRole`):

- `PET_OWNER`
- `ADMIN`
- `CARETAKER`
- `DOCTOR`

Public routes:

- `POST /auth/register`
- `POST /auth/login`
- `GET /dogs/{dogId}`
- `GET /users/public/by-role?role=DOCTOR|CARETAKER`

Protected route rules (from `SecurityConfig`):

- `/users/**` -> `ADMIN` only (except `/users/public/by-role`)
- `POST|PUT|DELETE /dogs/**` -> `PET_OWNER` or `ADMIN`
- `GET /dogs/**` -> any authenticated user
- `POST /daycare-appointments` -> `PET_OWNER` or `ADMIN`
- `PATCH /daycare-appointments/**` -> `ADMIN`
- `GET /daycare-appointments/**` -> `ADMIN`, `CARETAKER`, `PET_OWNER`
- `POST /vaccinations` -> `ADMIN`, `CARETAKER`
- `GET /vaccinations/**` -> authenticated
- `POST /health-reports` -> `ADMIN`, `CARETAKER`
- `GET /health-reports/**` -> authenticated
- `/doctor-appointments/**` -> `PET_OWNER`, `DOCTOR`, `ADMIN`
- `/notifications/**` -> `PET_OWNER`, `ADMIN`

---

## 2) Common Error Format

From `GlobalExceptionHandler`:

`400` (business / validation):

```json
{
  "message": "Validation failed",
  "field": "error message"
}
```

`401`:

```json
{ "message": "Invalid email or password" }
```

`404`:

```json
{ "message": "Resource not found ..." }
```

---

## 3) Auth API

### `POST /auth/register`

Register a new pet owner.

Body:

```json
{
  "name": "Kamal",
  "email": "kamal@mail.com",
  "phone": "0771234567",
  "password": "Test@123"
}
```

Response (`AuthResponse`):

```json
{
  "token": "jwt-token",
  "userId": 1,
  "name": "Kamal",
  "email": "kamal@mail.com",
  "role": "PET_OWNER"
}
```

### `POST /auth/login`

Body:

```json
{
  "email": "kamal@mail.com",
  "password": "Test@123"
}
```

Response: same as register.

---

## 4) User API

### `POST /users` (ADMIN)

Create user (admin/caretaker/doctor).

Body (`CreateUserRequest`):

```json
{
  "name": "Dr. Silva",
  "email": "doctor@mail.com",
  "phone": "0710000000",
  "password": "Doctor@123",
  "role": "DOCTOR"
}
```

### `GET /users` (ADMIN)

Optional query: `role`.

Examples:

- `/users`
- `/users?role=PET_OWNER`

### `GET /users/{id}` (ADMIN)

Get one user.

### `GET /users/public/by-role` (PUBLIC)

Only `DOCTOR` or `CARETAKER` are accepted.

Examples:

- `/users/public/by-role?role=DOCTOR`
- `/users/public/by-role?role=CARETAKER`

---

## 5) Dog API

### `POST /dogs` (`PET_OWNER`, `ADMIN`)

Body (`CreateDogRequest`):

```json
{
  "name": "Rocky",
  "breed": "Labrador",
  "dateOfBirth": "2022-05-10",
  "ownerId": 1
}
```

Response:

```json
{
  "id": 10,
  "name": "Rocky",
  "breed": "Labrador",
  "dateOfBirth": "2022-05-10",
  "ownerId": 1,
  "qrCodeBase64": "..."
}
```

### `GET /dogs/{dogId}` (PUBLIC)

Get single dog by ID.

### `GET /dogs?ownerId={ownerId}` (AUTH)

Get dogs by owner.

### `PUT /dogs/{dogId}` (`PET_OWNER`, `ADMIN`)

Body (`UpdateDogRequest`):

```json
{
  "name": "Rocky Updated",
  "breed": "Golden Retriever",
  "dateOfBirth": "2022-05-10"
}
```

### `DELETE /dogs/{dogId}` (`PET_OWNER`, `ADMIN`)

Response:

```json
{
  "message": "Dog deleted successfully",
  "dogId": 10
}
```

---

## 6) Daycare Appointment API

### `POST /daycare-appointments` (`PET_OWNER`, `ADMIN`)

Body (`BookDaycareAppointmentRequest`):

```json
{
  "dogId": 10,
  "caretakerId": 5,
  "startTime": "2026-03-25T09:00:00",
  "endTime": "2026-03-25T11:00:00"
}
```

### `PATCH /daycare-appointments/{appointmentId}/status` (`ADMIN`)

Body (`UpdateAppointmentStatusRequest`):

```json
{
  "status": "APPROVED"
}
```

Status enum (`AppointmentStatus`):

- `PENDING`
- `APPROVED`
- `CANCELLED`

### `GET /daycare-appointments?dogId={dogId}`

### `GET /daycare-appointments?caretakerId={caretakerId}`

Both are for `ADMIN`, `CARETAKER`, `PET_OWNER`.

---

## 7) Vaccination API

### `POST /vaccinations` (`ADMIN`, `CARETAKER`)

Body (`CreateVaccinationRequest`):

```json
{
  "dogId": 10,
  "vaccineName": "Rabies",
  "givenDate": "2026-03-01",
  "expiryDate": "2026-03-28"
}
```

### `GET /vaccinations?dogId={dogId}` (AUTH)

Response item:

```json
{
  "id": 1,
  "dogId": 10,
  "vaccineName": "Rabies",
  "givenDate": "2026-03-01",
  "expiryDate": "2026-03-28",
  "alertSent": false
}
```

---

## 8) Health Report API

### `POST /health-reports` (`ADMIN`, `CARETAKER`)

Body (`CreateHealthReportRequest`):

```json
{
  "dogId": 10,
  "createdById": 5,
  "behaviour": "ACTIVE",
  "healthStatus": "GOOD",
  "notes": "Normal"
}
```

Enums:

- `BehaviourType`: `ACTIVE`, `DANGEROUS`, `FRIENDLY`
- `HealthStatus`: `GOOD`, `BAD`

### `GET /health-reports?dogId={dogId}` (AUTH)

---

## 9) Doctor Appointment API

### `POST /doctor-appointments` (`PET_OWNER`, `DOCTOR`, `ADMIN`)

Body (`CreateDoctorAppointmentRequest`):

```json
{
  "dogId": 10,
  "ownerId": 1,
  "doctorId": 8,
  "appointmentTime": "2026-03-30T14:30:00",
  "notes": "Follow-up"
}
```

### `PATCH /doctor-appointments/{appointmentId}/status` (`PET_OWNER`, `DOCTOR`, `ADMIN`)

Body (`UpdateDoctorAppointmentStatusRequest`):

```json
{
  "status": "CONFIRMED"
}
```

### `GET /doctor-appointments?dogId={dogId}` (`PET_OWNER`, `DOCTOR`, `ADMIN`)

### `GET /doctor-appointments?ownerId={ownerId}` (`PET_OWNER`, `DOCTOR`, `ADMIN`)

### `GET /doctor-appointments?doctorId={doctorId}` (`PET_OWNER`, `DOCTOR`, `ADMIN`)

Response includes `status` (from `DoctorAppointmentStatus`):

- `REQUESTED`
- `CONFIRMED`
- `CANCELLED`

---

## 10) Notification API

### `GET /notifications/owner/{ownerId}` (`PET_OWNER`, `ADMIN`)

Response item:

```json
{
  "id": 1,
  "ownerId": 1,
  "dogId": 10,
  "type": "VACCINATION_ALERT",
  "message": "Vaccination 'Rabies' for dog Rocky will expire on 2026-03-28",
  "readFlag": false,
  "createdAt": "2026-03-23T08:00:00"
}
```

`NotificationType`:

- `VACCINATION_ALERT`
- `HEALTH_ALERT`

---

## 11) Email/Startup Mail Behavior

From `NotificationServiceImpl` and `StartupTestMailRunner`:

- Notification create tries to send plain-text email if mail sender bean is available.
- Email sender is configurable via:

```ini
app.mail.from=manuthkausilu20031018@gmail.com
```

Fallback:

- `app.mail.from` not set -> uses `spring.mail.username`.

Startup test email config:

```ini
app.mail.startup-test.enabled=true
app.mail.startup-test.to=rayff60@gmail.com
app.mail.startup-test.subject=PawCare Startup Test Email
```

---

## 12) Quick cURL Examples

```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@gmail.com","password":"123456"}'
```

```bash
curl "http://localhost:8080/api/v1/users/public/by-role?role=DOCTOR"
```

```bash
curl -X POST "http://localhost:8080/api/v1/dogs" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Rocky","breed":"Labrador","dateOfBirth":"2022-05-10","ownerId":1}'
```

