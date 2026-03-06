/* ============================================================
   PawCare — Sidebar Builder
   Call buildSidebar(role, activePage) to inject sidebar HTML
   ============================================================ */

const ADMIN_NAV = [
    { href:'admin-dashboard.html',       icon:'🏠', label:'Dashboard' },
    { href:'dogs.html',                  icon:'🐕', label:'Dogs' },
    { href:'appointments.html',          icon:'📅', label:'Appointments', badge:3 },
    { href:'health-reports.html',        icon:'📋', label:'Health Reports' },
    { href:'vaccinations.html',          icon:'💉', label:'Vaccinations', badge:2 },
    { href:'doctor-appointments.html',   icon:'🩺', label:'Doctor Visits' },
    { href:'qrcodes.html',               icon:'📱', label:'QR Codes' },
];

const OWNER_NAV = [
    { href:'owner-dashboard.html',       icon:'🏠', label:'My Dashboard' },
    { href:'owner-dogs.html',            icon:'🐕', label:'My Dogs' },
    { href:'owner-appointments.html',    icon:'📅', label:'Book Daycare' },
    { href:'owner-doctor.html',          icon:'🩺', label:'Doctor Visits' },
    { href:'owner-vaccinations.html',    icon:'💉', label:'Vaccinations' },
    { href:'owner-reports.html',         icon:'📋', label:'Health Reports' },
];

const CARETAKER_NAV = [
    { href:'caretaker-dashboard.html',   icon:'🏠', label:'My Dashboard' },
    { href:'appointments.html',          icon:'📅', label:'My Schedule', badge:3 },
    { href:'health-reports.html',        icon:'📋', label:'Add Reports' },
    { href:'dogs.html',                  icon:'🐕', label:'Dog Profiles' },
];

const DOCTOR_NAV = [
    { href:'doctor-dashboard.html',      icon:'🏠', label:'My Dashboard' },
    { href:'doctor-appointments.html',   icon:'🩺', label:'My Appointments', badge:2 },
    { href:'health-reports.html',        icon:'📋', label:'Health Reports' },
    { href:'vaccinations.html',          icon:'💉', label:'Vaccinations' },
    { href:'dogs.html',                  icon:'🐕', label:'Dog Profiles' },
];

const ROLE_CONFIG = {
    ADMIN:     { nav: ADMIN_NAV,     icon:'👩‍💼', sub:'Administrator' },
    USER:      { nav: OWNER_NAV,     icon:'🐾',   sub:'Pet Owner' },
    CARETAKER: { nav: CARETAKER_NAV, icon:'👷',   sub:'Caretaker' },
    DOCTOR:    { nav: DOCTOR_NAV,    icon:'🩺',   sub:'Doctor' },
};

function buildSidebar(role, activePage) {
    const cfg  = ROLE_CONFIG[role] || ROLE_CONFIG['ADMIN'];
    const user = Auth.getUser();
    return `
    <div class="sidebar-brand">
      <div class="brand-icon">🐾</div>
      <div class="brand-text">
        <div class="brand-name">PawCare</div>
        <div class="brand-sub">${cfg.sub}</div>
      </div>
    </div>
    <nav class="sidebar-nav">
      <div class="nav-section">Menu</div>
      ${cfg.nav.map(i => `
        <a href="${i.href}" class="nav-item ${i.href===activePage?'active':''}">
          <span class="nav-icon">${i.icon}</span> ${i.label}
          ${i.badge ? `<span class="nav-badge">${i.badge}</span>` : ''}
        </a>`).join('')}
    </nav>
    <div class="sidebar-footer">
      <div class="user-card">
        <div class="user-ava">${cfg.icon}</div>
        <div class="user-info">
          <div class="user-name" id="sidebarUserName">${user.fullName||'User'}</div>
          <div class="user-role">${cfg.sub}</div>
        </div>
        <button class="logout-btn" onclick="Auth.logout()" title="Logout">↩</button>
      </div>
    </div>`;
}

function injectSidebar(activePage) {
    const role = Auth.getRole() || 'ADMIN';
    const el = document.getElementById('sidebar');
    if (el) el.innerHTML = buildSidebar(role, activePage);
}