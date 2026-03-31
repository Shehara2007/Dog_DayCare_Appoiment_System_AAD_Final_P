(function ($, Auth) {
    "use strict";

    var session = null;
    var activeDogEditId = null;
    var dogCache = [];

    function setNotice(type, message) {
        var $notice = $("#dashboardNotice");
        $notice.removeClass("notice-success notice-error");
        $notice.addClass(type === "success" ? "notice-success" : "notice-error");
        $notice.text(message).show();
    }

    function clearNotice() {
        $("#dashboardNotice").hide();
    }

    function getErrorMessage(xhr) {
        if (xhr && xhr.responseJSON) {
            var body = xhr.responseJSON;
            if (body.message === "Validation failed") {
                var details = [];
                Object.keys(body).forEach(function (key) {
                    if (key !== "message") {
                        details.push(key + ": " + body[key]);
                    }
                });
                return details.length ? "Validation failed - " + details.join(", ") : "Validation failed.";
            }
            return body.message || body.error || "Request failed.";
        }
        return "Unable to reach backend. Check server status and API URL.";
    }

    function renderTable($target, rows) {
        if (!rows || rows.length === 0) {
            $target.html('<div class="empty-row">No data found.</div>');
            return;
        }

        var columns = Object.keys(rows[0]);
        var thead = "<thead><tr>" + columns.map(function (col) {
            return "<th>" + col + "</th>";
        }).join("") + "</tr></thead>";

        var tbody = "<tbody>" + rows.map(function (row) {
            return "<tr>" + columns.map(function (col) {
                var value = row[col] === null || row[col] === undefined ? "" : row[col];
                return "<td>" + value + "</td>";
            }).join("") + "</tr>";
        }).join("") + "</tbody>";

        $target.html('<div class="table-wrap"><table class="data-table">' + thead + tbody + "</table></div>");
    }

    function toLocalDateTime(inputValue) {
        if (!inputValue) {
            return null;
        }
        return inputValue.length === 16 ? inputValue + ":00" : inputValue;
    }

    function initDogsModule() {
        var $container = $("#moduleContainer");
        $container.html(
            '<div class="module-grid">' +
                '<div class="module-card">' +
                    '<h3>Register / Update Dog</h3>' +
                    '<form id="dogForm" class="compact-form">' +
                        '<input id="dogName" type="text" placeholder="Dog name" required />' +
                        '<input id="dogBreed" type="text" placeholder="Breed" required />' +
                        '<input id="dogDob" type="date" />' +
                        '<div class="btn-row">' +
                            '<button class="btn btn-primary" type="submit" id="dogSaveBtn">Save Dog</button>' +
                            '<button class="btn btn-ghost" type="button" id="dogResetBtn">Clear</button>' +
                        '</div>' +
                    '</form>' +
                '</div>' +
                '<div class="module-card">' +
                    '<h3>My Dogs</h3>' +
                    '<div id="dogsTableArea"></div>' +
                '</div>' +
            '</div>'
        );

        function loadDogs() {
            Auth.getDogsByOwner(session.userId)
                .done(function (dogs) {
                    dogCache = dogs;
                    var rows = dogs.map(function (dog) {
                        return {
                            id: dog.id,
                            name: dog.name,
                            breed: dog.breed,
                            dateOfBirth: dog.dateOfBirth || "",
                            qrCode: dog.qrCodeBase64 ? "Available" : "",
                            actions: '<button class="btn btn-ghost action-btn dog-edit" data-id="' + dog.id + '">Edit</button>' +
                                '<button class="btn btn-ghost action-btn dog-delete" data-id="' + dog.id + '">Delete</button>'
                        };
                    });
                    renderTable($("#dogsTableArea"), rows);
                })
                .fail(function (xhr) {
                    setNotice("error", getErrorMessage(xhr));
                });
        }

        loadDogs();

        $container.on("submit", "#dogForm", function (e) {
            e.preventDefault();
            clearNotice();

            var createPayload = {
                name: $.trim($("#dogName").val()),
                breed: $.trim($("#dogBreed").val()),
                dateOfBirth: $("#dogDob").val() || null,
                ownerId: Number(session.userId)
            };
            var updatePayload = {
                name: createPayload.name,
                breed: createPayload.breed,
                dateOfBirth: createPayload.dateOfBirth
            };

            var request = activeDogEditId
                ? Auth.updateDog(activeDogEditId, updatePayload)
                : Auth.createDog(createPayload);

            request.done(function () {
                setNotice("success", activeDogEditId ? "Dog updated." : "Dog registered.");
                activeDogEditId = null;
                $("#dogForm")[0].reset();
                loadDogs();
            }).fail(function (xhr) {
                setNotice("error", getErrorMessage(xhr));
            });
        });

        $container.on("click", "#dogResetBtn", function () {
            activeDogEditId = null;
            $("#dogForm")[0].reset();
            clearNotice();
        });

        $container.on("click", ".dog-edit", function () {
            var id = Number($(this).data("id"));
            var dog = dogCache.find(function (d) { return Number(d.id) === id; });
            if (!dog) {
                return;
            }
            activeDogEditId = id;
            $("#dogName").val(dog.name);
            $("#dogBreed").val(dog.breed);
            $("#dogDob").val(dog.dateOfBirth || "");
            setNotice("success", "Editing dog #" + id);
        });

        $container.on("click", ".dog-delete", function () {
            var id = Number($(this).data("id"));
            if (!window.confirm("Delete dog #" + id + "?")) {
                return;
            }
            Auth.deleteDog(id)
                .done(function () {
                    setNotice("success", "Dog deleted.");
                    loadDogs();
                })
                .fail(function (xhr) {
                    setNotice("error", getErrorMessage(xhr));
                });
        });
    }

    function initUsersModule() {
        var $container = $("#moduleContainer");
        $container.html(
            '<div class="module-grid">' +
                '<div class="module-card">' +
                    '<h3>Create User (Admin)</h3>' +
                    '<form id="userCreateForm" class="compact-form">' +
                        '<input id="uName" type="text" placeholder="Name" required />' +
                        '<input id="uEmail" type="email" placeholder="Email" required />' +
                        '<input id="uPhone" type="text" placeholder="Phone" required />' +
                        '<input id="uPassword" type="password" placeholder="Password" required />' +
                        '<select id="uRole"><option>ADMIN</option><option>CARETAKER</option><option>DOCTOR</option></select>' +
                        '<button class="btn btn-primary" type="submit">Create User</button>' +
                    '</form>' +
                '</div>' +
                '<div class="module-card">' +
                    '<h3>Users</h3>' +
                    '<div class="btn-row">' +
                        '<select id="userRoleFilter"><option value="">ALL</option><option>ADMIN</option><option>PET_OWNER</option><option>CARETAKER</option><option>DOCTOR</option></select>' +
                        '<button class="btn btn-ghost" id="loadUsersBtn" type="button">Load Users</button>' +
                    '</div>' +
                    '<div id="usersTableArea"></div>' +
                '</div>' +
            '</div>'
        );

        function loadUsers() {
            var role = $("#userRoleFilter").val();
            Auth.getUsers(role || null)
                .done(function (rows) {
                    renderTable($("#usersTableArea"), rows);
                })
                .fail(function (xhr) {
                    setNotice("error", getErrorMessage(xhr));
                });
        }

        loadUsers();

        $container.on("click", "#loadUsersBtn", loadUsers);

        $container.on("submit", "#userCreateForm", function (e) {
            e.preventDefault();
            var payload = {
                name: $.trim($("#uName").val()),
                email: $.trim($("#uEmail").val()),
                phone: $.trim($("#uPhone").val()),
                password: $("#uPassword").val(),
                role: $("#uRole").val()
            };
            Auth.createUser(payload)
                .done(function () {
                    setNotice("success", "User created.");
                    $("#userCreateForm")[0].reset();
                    loadUsers();
                })
                .fail(function (xhr) {
                    setNotice("error", getErrorMessage(xhr));
                });
        });
    }

    function initDaycareModule(mode) {
        var isOwner = mode === "owner";
        var isAdmin = mode === "admin";
        var $container = $("#moduleContainer");
        $container.html(
            '<div class="module-grid">' +
                '<div class="module-card">' +
                    '<h3>Book Daycare</h3>' +
                    '<form id="daycareBookForm" class="compact-form">' +
                        '<input id="dayDogId" type="number" placeholder="Dog ID" required />' +
                        '<input id="dayCaretakerId" type="number" placeholder="Caretaker ID" required />' +
                        '<input id="dayStart" type="datetime-local" required />' +
                        '<input id="dayEnd" type="datetime-local" required />' +
                        '<button class="btn btn-primary" type="submit">Book</button>' +
                    '</form>' +
                '</div>' +
                '<div class="module-card">' +
                    '<h3>Search Appointments</h3>' +
                    '<div class="btn-row">' +
                        '<input id="dayFindDogId" type="number" placeholder="Dog ID" />' +
                        '<button class="btn btn-ghost" id="loadByDogBtn" type="button">By Dog</button>' +
                    '</div>' +
                    '<div class="btn-row">' +
                        '<input id="dayFindCaretakerId" type="number" placeholder="Caretaker ID" ' + (isOwner ? "" : "") + ' />' +
                        '<button class="btn btn-ghost" id="loadByCaretakerBtn" type="button">By Caretaker</button>' +
                    '</div>' +
                    '<div id="daycareTableArea"></div>' +
                '</div>' +
            '</div>' +
            (isAdmin ?
                '<div class="module-card">' +
                    '<h3>Update Appointment Status (Admin)</h3>' +
                    '<form id="dayStatusForm" class="compact-form inline-form">' +
                        '<input id="statusAppointmentId" type="number" placeholder="Appointment ID" required />' +
                        '<select id="statusValue"><option>PENDING</option><option>APPROVED</option><option>CANCELLED</option></select>' +
                        '<button class="btn btn-primary" type="submit">Update</button>' +
                    '</form>' +
                '</div>' : "")
        );

        $container.on("submit", "#daycareBookForm", function (e) {
            e.preventDefault();
            var payload = {
                dogId: Number($("#dayDogId").val()),
                caretakerId: Number($("#dayCaretakerId").val()),
                startTime: toLocalDateTime($("#dayStart").val()),
                endTime: toLocalDateTime($("#dayEnd").val())
            };
            Auth.bookDaycare(payload)
                .done(function () {
                    setNotice("success", "Appointment booked.");
                    $("#daycareBookForm")[0].reset();
                })
                .fail(function (xhr) {
                    setNotice("error", getErrorMessage(xhr));
                });
        });

        $container.on("click", "#loadByDogBtn", function () {
            var dogId = Number($("#dayFindDogId").val());
            if (!dogId) {
                setNotice("error", "Enter dog id.");
                return;
            }
            Auth.getDaycareByDog(dogId)
                .done(function (rows) { renderTable($("#daycareTableArea"), rows); })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });

        $container.on("click", "#loadByCaretakerBtn", function () {
            var caretakerId = Number($("#dayFindCaretakerId").val());
            if (!caretakerId) {
                setNotice("error", "Enter caretaker id.");
                return;
            }
            Auth.getDaycareByCaretaker(caretakerId)
                .done(function (rows) { renderTable($("#daycareTableArea"), rows); })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });

        if (isAdmin) {
            $container.on("submit", "#dayStatusForm", function (e) {
                e.preventDefault();
                Auth.updateDaycareStatus(Number($("#statusAppointmentId").val()), $("#statusValue").val())
                    .done(function () {
                        setNotice("success", "Status updated.");
                        $("#dayStatusForm")[0].reset();
                    })
                    .fail(function (xhr) {
                        setNotice("error", getErrorMessage(xhr));
                    });
            });
        }
    }

    function initMedicalModule(mode) {
        var canCreate = mode !== "view";
        var $container = $("#moduleContainer");
        $container.html(
            '<div class="module-grid">' +
                (canCreate ?
                    '<div class="module-card">' +
                        '<h3>Create Vaccination</h3>' +
                        '<form id="vacForm" class="compact-form">' +
                            '<input id="vacDogId" type="number" placeholder="Dog ID" required />' +
                            '<input id="vacName" type="text" placeholder="Vaccine name" required />' +
                            '<input id="vacGiven" type="date" required />' +
                            '<input id="vacExpiry" type="date" required />' +
                            '<button class="btn btn-primary" type="submit">Save Vaccination</button>' +
                        '</form>' +
                    '</div>' : "") +
                (canCreate ?
                    '<div class="module-card">' +
                        '<h3>Create Health Report</h3>' +
                        '<form id="hrForm" class="compact-form">' +
                            '<input id="hrDogId" type="number" placeholder="Dog ID" required />' +
                            '<input id="hrCreatorId" type="number" placeholder="Created By User ID" required value="' + session.userId + '" />' +
                            '<select id="hrBehaviour"><option>ACTIVE</option><option>DANGEROUS</option><option>FRIENDLY</option></select>' +
                            '<select id="hrStatus"><option>GOOD</option><option>BAD</option></select>' +
                            '<input id="hrNotes" type="text" placeholder="Notes" />' +
                            '<button class="btn btn-primary" type="submit">Save Report</button>' +
                        '</form>' +
                    '</div>' : "") +
                '<div class="module-card">' +
                    '<h3>Medical Data by Dog</h3>' +
                    '<div class="btn-row">' +
                        '<input id="medicalDogId" type="number" placeholder="Dog ID" />' +
                        '<button class="btn btn-ghost" id="loadVaccinationsBtn" type="button">Vaccinations</button>' +
                        '<button class="btn btn-ghost" id="loadReportsBtn" type="button">Health Reports</button>' +
                    '</div>' +
                    '<div id="medicalTableArea"></div>' +
                '</div>' +
            '</div>'
        );

        $container.on("submit", "#vacForm", function (e) {
            e.preventDefault();
            var payload = {
                dogId: Number($("#vacDogId").val()),
                vaccineName: $.trim($("#vacName").val()),
                givenDate: $("#vacGiven").val(),
                expiryDate: $("#vacExpiry").val()
            };
            Auth.createVaccination(payload)
                .done(function () {
                    setNotice("success", "Vaccination saved.");
                    $("#vacForm")[0].reset();
                })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });

        $container.on("submit", "#hrForm", function (e) {
            e.preventDefault();
            var payload = {
                dogId: Number($("#hrDogId").val()),
                createdById: Number($("#hrCreatorId").val()),
                behaviour: $("#hrBehaviour").val(),
                healthStatus: $("#hrStatus").val(),
                notes: $.trim($("#hrNotes").val()) || null
            };
            Auth.createHealthReport(payload)
                .done(function () {
                    setNotice("success", "Health report saved.");
                    $("#hrForm")[0].reset();
                })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });

        $container.on("click", "#loadVaccinationsBtn", function () {
            var dogId = Number($("#medicalDogId").val());
            Auth.getVaccinationsByDog(dogId)
                .done(function (rows) { renderTable($("#medicalTableArea"), rows); })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });

        $container.on("click", "#loadReportsBtn", function () {
            var dogId = Number($("#medicalDogId").val());
            Auth.getHealthReportsByDog(dogId)
                .done(function (rows) { renderTable($("#medicalTableArea"), rows); })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });
    }

    function initDoctorAppointmentsModule(defaultOwner) {
        var $container = $("#moduleContainer");
        $container.html(
            '<div class="module-grid">' +
                '<div class="module-card">' +
                    '<h3>Create Doctor Appointment</h3>' +
                    '<form id="docCreateForm" class="compact-form">' +
                        '<input id="docDogId" type="number" placeholder="Dog ID" required />' +
                        '<input id="docOwnerId" type="number" placeholder="Owner ID" required value="' + defaultOwner + '" />' +
                        '<input id="docDoctorId" type="number" placeholder="Doctor ID" required />' +
                        '<input id="docTime" type="datetime-local" required />' +
                        '<input id="docNotes" type="text" placeholder="Notes" />' +
                        '<button class="btn btn-primary" type="submit">Create Appointment</button>' +
                    '</form>' +
                '</div>' +
                '<div class="module-card">' +
                    '<h3>Doctor Appointments by Owner</h3>' +
                    '<div class="btn-row">' +
                        '<input id="docOwnerSearchId" type="number" placeholder="Owner ID" value="' + defaultOwner + '" />' +
                        '<button class="btn btn-ghost" id="docLoadBtn" type="button">Load</button>' +
                    '</div>' +
                    '<div id="docTableArea"></div>' +
                '</div>' +
            '</div>'
        );

        $container.on("submit", "#docCreateForm", function (e) {
            e.preventDefault();
            var payload = {
                dogId: Number($("#docDogId").val()),
                ownerId: Number($("#docOwnerId").val()),
                doctorId: Number($("#docDoctorId").val()),
                appointmentTime: toLocalDateTime($("#docTime").val()),
                notes: $.trim($("#docNotes").val()) || null
            };
            Auth.createDoctorAppointment(payload)
                .done(function () {
                    setNotice("success", "Doctor appointment created.");
                    $("#docCreateForm")[0].reset();
                })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });

        $container.on("click", "#docLoadBtn", function () {
            var ownerId = Number($("#docOwnerSearchId").val());
            Auth.getDoctorAppointmentsByOwner(ownerId)
                .done(function (rows) { renderTable($("#docTableArea"), rows); })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });
    }

    function initNotificationsModule(defaultOwner) {
        var $container = $("#moduleContainer");
        $container.html(
            '<div class="module-card">' +
                '<h3>Notifications</h3>' +
                '<div class="btn-row">' +
                    '<input id="notifOwnerId" type="number" placeholder="Owner ID" value="' + defaultOwner + '" />' +
                    '<button class="btn btn-ghost" id="notifLoadBtn" type="button">Load Notifications</button>' +
                '</div>' +
                '<div id="notifTableArea"></div>' +
            '</div>'
        );

        $container.on("click", "#notifLoadBtn", function () {
            var ownerId = Number($("#notifOwnerId").val());
            Auth.getNotificationsByOwner(ownerId)
                .done(function (rows) { renderTable($("#notifTableArea"), rows); })
                .fail(function (xhr) { setNotice("error", getErrorMessage(xhr)); });
        });
    }

    function initPage() {
        session = Auth.getSession();
        if (!session || !session.token || session.userId == null) {
            window.location.href = (window.DogDaycareLayout ? window.DogDaycareLayout.relPath : '') + "login.html";
            return;
        }

        // Populate Stats (Mock or real depending on API availability)
        const $statsGrid = $('#statsGrid');
        $statsGrid.empty();

        if (session.role === 'PET_OWNER') {
            Auth.getDogsByOwner(session.userId).done(dogs => {
                $statsGrid.append(`
                    <div class="card-premium stat-card">
                        <div class="stat-icon">🐕</div>
                        <div>
                            <span class="stat-value">${dogs.length}</span>
                            <span class="stat-label">Registered Dogs</span>
                        </div>
                    </div>
                `);

                // Get appointments for all dogs
                let apptCount = 0;
                let promises = dogs.map(d => Auth.getDaycareByDog(d.id).done(appts => apptCount += appts.length));
                Promise.all(promises).then(() => {
                    $statsGrid.append(`
                        <div class="card-premium stat-card">
                            <div class="stat-icon">📅</div>
                            <div>
                                <span class="stat-value">${apptCount}</span>
                                <span class="stat-label">Total Appointments</span>
                            </div>
                        </div>
                    `);
                });
            });
        } else if (session.role === 'ADMIN') {
             Auth.getUsers().done(users => {
                $statsGrid.append(`
                    <div class="card-premium stat-card">
                        <div class="stat-icon">👥</div>
                        <div>
                            <span class="stat-value">${users.length}</span>
                            <span class="stat-label">Total Users</span>
                        </div>
                    </div>
                `);
             });
        }
        
        // Mock some activity for now if no "all activity" endpoint exists
        $('#activityContainer').html(`
            <ul style="list-style: none;">
                <li style="padding: 1rem 0; border-bottom: 1px solid #f1f5f9;">
                    <div style="font-weight: 500;">Welcome to Bark & Stay!</div>
                    <div style="font-size: 0.8rem; color: var(--text-muted);">Your dashboard is ready. Explore the sidebar to manage your pets and appointments.</div>
                </li>
            </ul>
        `);
    }

    $(initPage);
})(jQuery, window.DogDaycareAuth);

