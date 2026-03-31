(function (window, $) {
    "use strict";

    var SESSION_KEY = "dogDaycare.session";
    var TOKEN_KEY = "dogDaycare.token";
    var USER_ID_KEY = "dogDaycare.userId";
    var ROLE_KEY = "dogDaycare.role";
    var NAME_KEY = "dogDaycare.name";
    var EMAIL_KEY = "dogDaycare.email";
    var API_BASE_KEY = "dogDaycare.apiBaseUrl";
    var DEFAULT_API_BASE_URL = "http://localhost:8080";

    function getApiBaseUrl() {
        return localStorage.getItem(API_BASE_KEY) || DEFAULT_API_BASE_URL;
    }


    function buildUrl(path) {
        if (/^https?:\/\//i.test(path)) {
            return path;
        }
        return getApiBaseUrl().replace(/\/$/, "") + path;
    }

    function getSession() {
        var raw = localStorage.getItem(SESSION_KEY);
        if (!raw) {
            return buildSessionFromDiscreteKeys();
        }
        try {
            var parsed = JSON.parse(raw);
            if (parsed && parsed.token && parsed.userId != null) {
                return parsed;
            }
            return buildSessionFromDiscreteKeys();
        } catch (e) {
            localStorage.removeItem(SESSION_KEY);
            return buildSessionFromDiscreteKeys();
        }
    }

    function buildSessionFromDiscreteKeys() {
        var token = localStorage.getItem(TOKEN_KEY);
        var userId = localStorage.getItem(USER_ID_KEY);
        if (!token || userId == null) {
            return null;
        }
        return {
            token: token,
            userId: Number(userId),
            role: localStorage.getItem(ROLE_KEY),
            name: localStorage.getItem(NAME_KEY),
            email: localStorage.getItem(EMAIL_KEY)
        };
    }

    function saveSession(authResponse) {
        var session = {
            token: authResponse.token,
            userId: Number(authResponse.userId),
            name: authResponse.name,
            email: authResponse.email,
            role: authResponse.role
        };
        localStorage.setItem(SESSION_KEY, JSON.stringify(session));
        localStorage.setItem(TOKEN_KEY, session.token);
        localStorage.setItem(USER_ID_KEY, String(session.userId));
        localStorage.setItem(ROLE_KEY, session.role || "");
        localStorage.setItem(NAME_KEY, session.name || "");
        localStorage.setItem(EMAIL_KEY, session.email || "");
        return session;
    }

    function clearSession() {
        localStorage.removeItem(SESSION_KEY);
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_ID_KEY);
        localStorage.removeItem(ROLE_KEY);
        localStorage.removeItem(NAME_KEY);
        localStorage.removeItem(EMAIL_KEY);
    }

    function getToken() {
        var session = getSession();
        return session ? session.token : null;
    }

    function authHeaders() {
        var token = getToken();
        return token ? { Authorization: "Bearer " + token } : {};
    }

    function request(method, path, data) {
        var options = {
            url: buildUrl(path),
            method: method,
            headers: authHeaders()
        };

        if (method === "GET" || method === "DELETE") {
            if (data) {
                options.data = data;
            }
        } else if (data !== undefined) {
            options.contentType = "application/json";
            options.data = JSON.stringify(data);
        }

        return $.ajax(options);
    }

    function register(payload) {
        return $.ajax({
            url: buildUrl("/api/v1/auth/register"),
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload)
        }).then(function (response) {
            return saveSession(response);
        });
    }

    function login(payload) {
        return $.ajax({
            url: buildUrl("/api/v1/auth/login"),
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload)
        }).then(function (response) {
            return saveSession(response);
        });
    }

    function requestForgotPasswordOtp(payload) {
        return $.ajax({
            url: buildUrl("/api/v1/auth/forgot-password/request-otp"),
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload)
        });
    }

    function resetPasswordWithOtp(payload) {
        return $.ajax({
            url: buildUrl("/api/v1/auth/forgot-password/reset"),
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload)
        });
    }

    function getDogs(ownerId) {
        return $.ajax({
            url: buildUrl("/api/v1/dogs"),
            method: "GET",
            headers: authHeaders(),
            data: ownerId != null ? { ownerId: ownerId } : undefined
        });
    }

    function getDogsByOwner(ownerId) {
        return getDogs(ownerId);
    }

    function createDog(payload) {
        return $.ajax({
            url: buildUrl("/api/v1/dogs"),
            method: "POST",
            headers: authHeaders(),
            contentType: "application/json",
            data: JSON.stringify(payload)
        });
    }

    function updateDog(dogId, payload) {
        return $.ajax({
            url: buildUrl("/api/v1/dogs/" + dogId),
            method: "PUT",
            headers: authHeaders(),
            contentType: "application/json",
            data: JSON.stringify(payload)
        });
    }

    function deleteDog(dogId) {
        return request("DELETE", "/api/v1/dogs/" + dogId);
    }

    function createUser(payload) {
        return request("POST", "/api/v1/users", payload);
    }

    function getUsers(role) {
        return request("GET", "/api/v1/users", role ? { role: role } : null);
    }

    function updateUser(userId, payload) {
        return request("PUT", "/api/v1/users/" + userId, payload);
    }

    function deleteUser(userId) {
        return request("DELETE", "/api/v1/users/" + userId);
    }

    function getPublicUsersByRole(role) {
        return $.ajax({
            url: buildUrl("/api/v1/users/public/by-role?role=" + role),
            method: "GET"
        });
    }

    function getDaycareByDog(dogId) {
        return request("GET", "/api/v1/daycare-appointments", { dogId: dogId });
    }

    function getDaycareByCaretaker(caretakerId) {
        return request("GET", "/api/v1/daycare-appointments", { caretakerId: caretakerId });
    }

    function bookDaycare(payload) {
        return request("POST", "/api/v1/daycare-appointments", payload);
    }

    function updateDaycareStatus(appointmentId, status) {
        return request("PATCH", "/api/v1/daycare-appointments/" + appointmentId + "/status", {
            status: status
        });
    }

    function createVaccination(payload) {
        return request("POST", "/api/v1/vaccinations", payload);
    }

    function getVaccinationsByDog(dogId) {
        return request("GET", "/api/v1/vaccinations", { dogId: dogId });
    }

    function createHealthReport(payload) {
        return request("POST", "/api/v1/health-reports", payload);
    }

    function getHealthReportsByDog(dogId) {
        return request("GET", "/api/v1/health-reports", { dogId: dogId });
    }

    function createDoctorAppointment(payload) {
        return request("POST", "/api/v1/doctor-appointments", payload);
    }

    function updateDoctorAppointmentStatus(appointmentId, status) {
        return request("PATCH", "/api/v1/doctor-appointments/" + appointmentId + "/status", {
            status: status
        });
    }

    function getDoctorAppointmentsByOwner(ownerId) {
        return request("GET", "/api/v1/doctor-appointments", { ownerId: ownerId });
    }

    function getDoctorAppointmentsByDog(dogId) {
        return request("GET", "/api/v1/doctor-appointments", { dogId: dogId });
    }

    function getNotificationsByOwner(ownerId) {
        return request("GET", "/api/v1/notifications/owner/" + ownerId);
    }

    window.DogDaycareAuth = {
        getApiBaseUrl: getApiBaseUrl,
        buildUrl: buildUrl,
        getSession: getSession,
        saveSession: saveSession,
        clearSession: clearSession,
        getToken: getToken,
        authHeaders: authHeaders,
        request: request,
        register: register,
        login: login,
        requestForgotPasswordOtp: requestForgotPasswordOtp,
        resetPasswordWithOtp: resetPasswordWithOtp,
        getDogs: getDogs,
        getDogsByOwner: getDogsByOwner,
        createDog: createDog,
        updateDog: updateDog,
        deleteDog: deleteDog,
        createUser: createUser,
        getUsers: getUsers,
        updateUser: updateUser,
        deleteUser: deleteUser,
        getPublicUsersByRole: getPublicUsersByRole,
        getDaycareByDog: getDaycareByDog,
        getDaycareByCaretaker: getDaycareByCaretaker,
        bookDaycare: bookDaycare,
        updateDaycareStatus: updateDaycareStatus,
        createVaccination: createVaccination,
        getVaccinationsByDog: getVaccinationsByDog,
        createHealthReport: createHealthReport,
        getHealthReportsByDog: getHealthReportsByDog,
        createDoctorAppointment: createDoctorAppointment,
        updateDoctorAppointmentStatus: updateDoctorAppointmentStatus,
        getDoctorAppointmentsByOwner: getDoctorAppointmentsByOwner,
        getDoctorAppointmentsByDog: getDoctorAppointmentsByDog,
        getNotificationsByOwner: getNotificationsByOwner
    };
})(window, jQuery);
