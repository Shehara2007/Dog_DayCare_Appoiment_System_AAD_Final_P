(function (window, $) {
    "use strict";


    // Dynamic relative path resolution
    const getRelativeRoot = () => {
        const path = window.location.pathname;
        if (path.indexOf('/pos/pages/') === -1) return ''; // Fallback
        const afterPages = path.split('/pos/pages/')[1];
        const depth = afterPages.split('/').length - 1;
        return '../'.repeat(depth);
    };

    const REL_PATH = getRelativeRoot();

    function initHeader() {
        const session = window.DogDaycareAuth.getSession();
        const headerHtml = `
            <nav class="navbar-premium">
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <button class="sidebar-toggle-btn" id="sidebarToggle">☰</button>
                    <a href="${REL_PATH}dashboard.html" class="logo-container">
                        <span style="font-size: 2rem;">🐾</span>
                        <span class="logo">PawCare</span>
                    </a>
                </div>
                <div class="user-profile">
                    ${session ? `
                        <div style="display: flex; align-items: center;">
                            ${session.role === 'PET_OWNER' ? `
                                <div class="notification-container" id="notificationContainer">
                                    <button class="notification-bell" id="notificationBell" title="Notifications">
                                        🔔
                                        <span class="notification-badge" id="notificationBadge"></span>
                                    </button>
                                    <div class="notification-dropdown" id="notificationDropdown">
                                        <div class="notification-header">
                                            <h4>Notifications</h4>
                                            <button class="btn-premium btn-secondary" id="markAllRead" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Clear All</button>
                                        </div>
                                        <div class="notification-list" id="notificationList">
                                            <div class="notification-empty">Loading notifications...</div>
                                        </div>
                                    </div>
                                </div>
                            ` : ''}
                            <div style="display: flex; align-items: center; gap: 1rem;">
                                <span style="font-weight: 500;">${session.name}</span>
                                <button id="logoutBtn" class="btn-premium btn-secondary" style="padding: 0.5rem 1rem;">Logout</button>
                            </div>
                        </div>
                    ` : `
                        <a href="${REL_PATH}login.html" class="btn-premium btn-primary">Login</a>
                    `}
                </div>
            </nav>
        `;
        $('body').prepend(headerHtml);

        $('#sidebarToggle').on('click', function () {
            $('.sidebar-premium').toggleClass('show');
        });

        $(document).on('click', function (e) {
            if (!$(e.target).closest('.sidebar-premium, .sidebar-toggle-btn').length) {
                $('.sidebar-premium').removeClass('show');
            }
        });

        $('#logoutBtn').on('click', function () {
            window.DogDaycareAuth.clearSession();
            window.location.href = REL_PATH + 'login.html';
        });
    }

    function initDashboardStats() {
        const $statsGrid = $('#statsGrid');
        if ($statsGrid.length === 0) return;

        const session = window.DogDaycareAuth.getSession();
        if (!session) return;

        $statsGrid.html('<p style="padding: 2rem; color: var(--text-muted);">Calculating your stats...</p>');

        if (session.role === 'PET_OWNER') {
            window.DogDaycareAuth.getDogsByOwner(session.userId).then(dogs => {
                $statsGrid.html(`
                    <div class="card-premium stat-card">
                        <h3>My Registered Dogs</h3>
                        <p class="stat-value">${dogs.length}</p>
                    </div>
                    <div class="card-premium stat-card">
                        <h3>Dashboard Active</h3>
                        <p class="stat-value" style="font-size: 1.2rem;">Ready</p>
                    </div>
                `);
            });
        } else if (session.role === 'ADMIN') {
            window.DogDaycareAuth.getUsers().then(users => {
                $statsGrid.html(`
                    <div class="card-premium stat-card">
                        <h3>Total Staff & Users</h3>
                        <p class="stat-value">${users.length}</p>
                    </div>
                `);
            });
        }
    }

    function initSidebar() {
        const session = window.DogDaycareAuth.getSession();
        if (!session) return;

        let navItems = [
            { name: 'Dashboard', icon: '🏠', url: REL_PATH + 'dashboard.html' }
        ];

        if (session.role === 'ADMIN') {
            navItems.push(
                { name: 'User Management', icon: '👥', url: REL_PATH + 'AdminDashboard/users.html' },
                { name: 'Dog Management', icon: '🐕', url: REL_PATH + 'AdminDashboard/dogs.html' },
                { name: 'Daycare Bookings', icon: '📅', url: REL_PATH + 'AdminDashboard/appointments.html' },
                { name: 'Doctor Appointments', icon: '🩺', url: REL_PATH + 'AdminDashboard/doctor-appointments.html' },
                { name: 'Vaccinations', icon: '💉', url: REL_PATH + 'AdminDashboard/vaccinations.html' },
                { name: 'Health Reports', icon: '📋', url: REL_PATH + 'AdminDashboard/health-reports.html' }
            );
        } else if (session.role === 'PET_OWNER') {
            navItems.push(
                { name: 'My Dogs', icon: '🐕', url: REL_PATH + 'PetOwnerDashboard/dogs.html' },
                { name: 'Book Daycare', icon: '✨', url: REL_PATH + 'PetOwnerDashboard/darecare-appointments.html' },
                { name: 'Doctor Visits', icon: '🩺', url: REL_PATH + 'PetOwnerDashboard/doctor-appointments.html' },
                { name: 'Health Reports', icon: '📋', url: REL_PATH + 'PetOwnerDashboard/reports.html' }
            );
        }

        const currentPath = window.location.pathname;
        const sidebarHtml = `
            <aside class="sidebar-premium">
                <ul style="list-style: none;">
                    ${navItems.map(item => {
            const isActive = currentPath.endsWith(item.url) || currentPath.includes(item.url);
            return `
                        <li style="margin-bottom: 0.5rem;">
                            <a href="${item.url}" class="nav-item-link ${isActive ? 'active' : ''}" 
                               style="display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1rem; border-radius: 8px; text-decoration: none; color: ${isActive ? 'var(--primary-color)' : 'var(--text-muted)'}; background: ${isActive ? 'rgba(79, 70, 229, 0.05)' : 'transparent'}; font-weight: 500; transition: var(--transition);">
                                <span>${item.icon}</span>
                                <span>${item.name}</span>
                            </a>
                        </li>
                    `}).join('')}
                </ul>
            </aside>
        `;

        $('.app-container').prepend(sidebarHtml);
    }

    window.DogDaycareLayout = {
        relPath: REL_PATH,
        init: function () {
            initHeader();
            if ($('.app-container').length > 0) {
                initSidebar();
                initDashboardStats();
            }
            if (window.BarkStayNotification) {
                window.BarkStayNotification.init();
            }
        }
    };
})(window, jQuery);
