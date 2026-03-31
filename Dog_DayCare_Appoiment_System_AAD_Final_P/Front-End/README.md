# Dog Daycare Front-End (jQuery + AJAX)

This frontend now includes a unified role-aware dashboard that connects to the full backend controller layer.

## Main files

- `pos/pages/index.html` - welcome page
- `pos/pages/register.html` - pet owner register
- `pos/pages/login.html` - login page
- `pos/pages/dashboard.html` - unified dashboard for all roles
- `pos/js/auth.js` - session manager + API client wrappers
- `pos/js/auth-pages.js` - auth page actions + routing
- `pos/js/dashboard.js` - dashboard modules and UI bindings
- `css/theme.css` - theme + dashboard layout styles

## Role behavior

After login, users are routed to `pos/pages/dashboard.html` and modules are shown by role.

- `ADMIN`: users, daycare manage, vaccination/health create, doctor appointments, notifications
- `PET_OWNER`: dog CRUD, daycare booking/search, vaccination/health view, doctor appointments, notifications
- `CARETAKER`: daycare search, vaccination/health create
- `DOCTOR`: doctor appointments, notifications

## Local storage keys

- `dogDaycare.session`
- `dogDaycare.token`
- `dogDaycare.userId`
- `dogDaycare.role`
- `dogDaycare.name`
- `dogDaycare.email`
- `dogDaycare.apiBaseUrl` (optional override)

Default API base URL is `http://localhost:8080`.

## Backend endpoints wired in frontend

- Auth: `/api/v1/auth/register`, `/api/v1/auth/login`
- Users: `/api/v1/users`
- Dogs: `/api/v1/dogs`, `/api/v1/dogs/{dogId}`
- Daycare: `/api/v1/daycare-appointments`, `/api/v1/daycare-appointments/{appointmentId}/status`
- Vaccinations: `/api/v1/vaccinations`
- Health Reports: `/api/v1/health-reports`
- Doctor Appointments: `/api/v1/doctor-appointments`
- Notifications: `/api/v1/notifications/owner/{ownerId}`

## Quick run

From the `Front-End` folder, serve static files (any static server works):

```powershell
Set-Location "C:\Users\ASUS\Downloads\Dog_DayCare_Appoiment_System_AAD_Final_P\Front-End"
python -m http.server 5500
```

Then open:

- `http://localhost:5500/pos/pages/index.html`

