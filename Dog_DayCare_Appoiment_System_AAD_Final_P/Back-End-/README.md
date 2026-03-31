# Dog Daycare Backend (No Security)

This backend implements the first project milestone **without authentication/authorization**.

## Implemented modules

- User management (Pet Owner, Admin, Caretaker, Doctor)
- Dog registration with QR code generation (Base64 PNG)
- Daycare appointment booking with caretaker capacity rule (max 3 overlapping dogs)
- Vaccination records + scheduled expiry alerts
- Health reports with automatic owner alerts when health is `BAD`
- Doctor appointment requests with doctor availability check
- Owner notification feed

## API base path

`/api`

## Main endpoints

- `POST /api/users`, `GET /api/users?role=PET_OWNER`
- `POST /api/dogs`, `GET /api/dogs?ownerId=1`, `GET /api/dogs/{dogId}`
- `POST /api/daycare-appointments`, `PATCH /api/daycare-appointments/{id}/status`
- `POST /api/vaccinations`, `GET /api/vaccinations?dogId=1`
- `POST /api/health-reports`, `GET /api/health-reports?dogId=1`
- `POST /api/doctor-appointments`, `GET /api/doctor-appointments?ownerId=1`
- `GET /api/notifications/owner/{ownerId}`

## Scheduler

- Vaccination expiry check runs daily at `08:00` server time.
- Expiry window: today to next 7 days.

## Run

```powershell
cd "C:\Users\ASUS\Downloads\Dog_DayCare_Appoiment_System_AAD_Final_P\Back-End-"
.\mvnw.cmd spring-boot:run
```

## Test

```powershell
cd "C:\Users\ASUS\Downloads\Dog_DayCare_Appoiment_System_AAD_Final_P\Back-End-"
.\mvnw.cmd test
```

