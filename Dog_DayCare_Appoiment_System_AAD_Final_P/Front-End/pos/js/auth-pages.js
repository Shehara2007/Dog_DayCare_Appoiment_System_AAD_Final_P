(function ($, Auth) {
    "use strict";

    function setNotice($el, type, message) {
        $el.removeClass("notice-success notice-error");
        $el.addClass(type === "success" ? "notice-success" : "notice-error");
        $el.text(message).fadeIn();
    }

    function getErrorMessage(xhr) {
        if (xhr && xhr.responseJSON) {
            return xhr.responseJSON.message || xhr.responseJSON.error || "Request failed.";
        }
        return "Unable to reach backend. Check API URL and server status.";
    }

    function getDashboardPathByRole(role) {
        if (role === "ADMIN" || role === "PET_OWNER" || role === "CARETAKER" || role === "DOCTOR") {
            return "dashboard.html";
        }
        return "dashboard.html";
    }

    function initIndexPage() {
        var $sessionInfo = $("#sessionInfo");
        var $continueBtn = $("#continueBtn");
        var session = Auth.getSession();

        if (session) {
            $sessionInfo.html(
                "Logged in as <strong>" + session.name + "</strong> (" + session.role + ")"
            );
            $continueBtn.attr("href", getDashboardPathByRole(session.role));
            $("#guestActions").hide();
            $("#userActions").show();
        } else {
            $sessionInfo.text("You are not logged in.");
            $("#guestActions").show();
            $("#userActions").hide();
        }

        $("#logoutBtn").on("click", function () {
            Auth.clearSession();
            window.location.reload();
        });
    }

    function initRegisterPage() {
        var $form = $("#registerForm");
        var $notice = $("#registerNotice");

        $form.on("submit", function (e) {
            e.preventDefault();
            $notice.hide();

            var payload = {
                name: $.trim($("#name").val()),
                email: $.trim($("#email").val()),
                phone: $.trim($("#phone").val()),
                password: $("#password").val()
            };
            var confirmPassword = $("#confirmPassword").val();

            if (!payload.name || !payload.email || !payload.phone || !payload.password) {
                setNotice($notice, "error", "Please fill all required fields.");
                return;
            }
            if (payload.password !== confirmPassword) {
                setNotice($notice, "error", "Password and confirm password must match.");
                return;
            }

            var $submit = $("#registerBtn");
            $submit.prop("disabled", true).text("Creating account...");

            Auth.register(payload)
                .done(function () {
                    setNotice($notice, "success", "Registration successful. Redirecting to login...");
                    // Registration endpoint also returns token; clear to force explicit login step.
                    Auth.clearSession();
                    setTimeout(function () {
                        window.location.href = "login.html";
                    }, 800);
                })
                .fail(function (xhr) {
                    setNotice($notice, "error", getErrorMessage(xhr));
                })
                .always(function () {
                    $submit.prop("disabled", false).text("Create Account");
                });
        });
    }

    function initLoginPage() {
        var $form = $("#loginForm");
        var $notice = $("#loginNotice");
        var $forgotPanel = $("#forgotPasswordPanel");
        var $showForgot = $("#showForgotPassword");
        var $hideForgot = $("#hideForgotPassword");
        var $requestOtpBtn = $("#requestOtpBtn");
        var $resetForm = $("#resetPasswordForm");

        function toggleForgotPassword(show) {
            if (show) {
                $forgotPanel.stop(true, true).slideDown(160);
                $form.hide();
            } else {
                $forgotPanel.stop(true, true).slideUp(160);
                $form.show();
            }
            $notice.hide();
        }

        $showForgot.on("click", function (e) {
            e.preventDefault();
            var loginEmail = $.trim($("#email").val());
            if (loginEmail) {
                $("#fpEmail").val(loginEmail);
            }
            toggleForgotPassword(true);
        });

        $hideForgot.on("click", function (e) {
            e.preventDefault();
            toggleForgotPassword(false);
        });

        $form.on("submit", function (e) {
            e.preventDefault();
            $notice.hide();

            var payload = {
                email: $.trim($("#email").val()),
                password: $("#password").val()
            };

            if (!payload.email || !payload.password) {
                setNotice($notice, "error", "Email and password are required.");
                return;
            }

            var $submit = $("#loginBtn");
            $submit.prop("disabled", true).text("Signing in...");

            Auth.login(payload)
                .done(function (session) {
                    var dashboardPath = getDashboardPathByRole(session.role);
                    setNotice($notice, "success", "Login successful. Redirecting...");
                    setTimeout(function () {
                        window.location.href = dashboardPath;
                    }, 700);
                })
                .fail(function (xhr) {
                    setNotice($notice, "error", getErrorMessage(xhr));
                })
                .always(function () {
                    $submit.prop("disabled", false).text("Login");
                });
        });

        $requestOtpBtn.on("click", function () {
            $notice.hide();

            var email = $.trim($("#fpEmail").val());
            if (!email) {
                setNotice($notice, "error", "Please enter your email address.");
                return;
            }

            $requestOtpBtn.prop("disabled", true).text("Sending OTP...");
            Auth.requestForgotPasswordOtp({ email: email })
                .done(function (res) {
                    setNotice($notice, "success", (res && res.message) || "OTP sent. Check your email.");
                })
                .fail(function (xhr) {
                    setNotice($notice, "error", getErrorMessage(xhr));
                })
                .always(function () {
                    $requestOtpBtn.prop("disabled", false).text("Send OTP");
                });
        });

        $resetForm.on("submit", function (e) {
            e.preventDefault();
            $notice.hide();

            var payload = {
                email: $.trim($("#fpEmail").val()),
                otp: $.trim($("#fpOtp").val()),
                newPassword: $("#fpNewPassword").val()
            };
            var confirmPassword = $("#fpConfirmPassword").val();

            if (!payload.email || !payload.otp || !payload.newPassword) {
                setNotice($notice, "error", "Email, OTP and new password are required.");
                return;
            }
            if (payload.newPassword !== confirmPassword) {
                setNotice($notice, "error", "New password and confirm password must match.");
                return;
            }

            var $submit = $("#resetPasswordBtn");
            $submit.prop("disabled", true).text("Resetting...");

            Auth.resetPasswordWithOtp(payload)
                .done(function () {
                    setNotice($notice, "success", "Password reset successful. You can now sign in.");
                    $resetForm[0].reset();
                    toggleForgotPassword(false);
                })
                .fail(function (xhr) {
                    setNotice($notice, "error", getErrorMessage(xhr));
                })
                .always(function () {
                    $submit.prop("disabled", false).text("Reset Password");
                });
        });
    }

    $(function () {
        var page = $("body").data("page");
        if (page === "index") {
            initIndexPage();
        } else if (page === "register") {
            initRegisterPage();
        } else if (page === "login") {
            initLoginPage();
        }
    });
})(jQuery, window.DogDaycareAuth);
