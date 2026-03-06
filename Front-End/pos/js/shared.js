/* ============================================================
   PawCare — Shared JavaScript
   Utilities: API, Toast, Modal, Table, Auth, Sidebar
   ============================================================ */

const BASE_URL = 'http://localhost:8080/api/v1';

/* ============================================================
   AUTH HELPERS
   ============================================================ */
const Auth = {
    getToken: () => localStorage.getItem('pawcare_token'),
    getRole:  () => localStorage.getItem('pawcare_role'),
    getUser:  () => JSON.parse(localStorage.getItem('pawcare_user') || '{}'),

    setSession(token, role, user) {
        localStorage.setItem('pawcare_token', token);
        localStorage.setItem('pawcare_role',  role);
        localStorage.setItem('pawcare_user',  JSON.stringify(user));
    },

    clearSession() {
        localStorage.removeItem('pawcare_token');
        localStorage.removeItem('pawcare_role');
        localStorage.removeItem('pawcare_user');
    },

    requireAuth(allowedRoles = []) {
        const role = this.getRole();
        if (!this.getToken() || !role) {
            window.location.href = 'login.html';
            return false;
        }
        if (allowedRoles.length && !allowedRoles.includes(role)) {
            window.location.href = 'login.html';
            return false;
        }
        return true;
    },

    logout() {
        this.clearSession();
        window.location.href = 'login.html';
    }
};

/* ============================================================
   API WRAPPER
   ============================================================ */
const API = {
    headers() {
        const h = { 'Content-Type': 'application/json' };
        const token = Auth.getToken();
        if (token) h['Authorization'] = `Bearer ${token}`;
        return h;
    },

    async request(method, path, body = null) {
        const opts = { method, headers: this.headers() };
        if (body) opts.body = JSON.stringify(body);
        try {
            const res = await fetch(BASE_URL + path, opts);
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'Request failed');
            return data;
        } catch (err) {
            Toast.error(err.message);
            throw err;
        }
    },

    get:    (path)         => API.request('GET',    path),
    post:   (path, body)   => API.request('POST',   path, body),
    put:    (path, body)   => API.request('PUT',    path, body),
    delete: (path)         => API.request('DELETE', path),
};

/* ============================================================
   TOAST NOTIFICATIONS
   ============================================================ */
const Toast = {
    container: null,

    init() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.id = 'toast-container';
            document.body.appendChild(this.container);
        }
    },

    show(message, type = 'info', icon = '') {
        this.init();
        const t = document.createElement('div');
        t.className = `toast toast-${type}`;
        t.innerHTML = `<span>${icon || this._icon(type)}</span> ${message}`;
        this.container.appendChild(t);
        setTimeout(() => {
            t.style.animation = 'toastOut 0.3s ease forwards';
            setTimeout(() => t.remove(), 300);
        }, 3200);
    },

    _icon(type) {
        return { success:'✅', error:'❌', warn:'⚠️', info:'ℹ️' }[type] || 'ℹ️';
    },

    success: (msg) => Toast.show(msg, 'success'),
    error:   (msg) => Toast.show(msg, 'error'),
    warn:    (msg) => Toast.show(msg, 'warn'),
    info:    (msg) => Toast.show(msg, 'info'),
};

/* ============================================================
   MODAL HELPERS
   ============================================================ */
const Modal = {
    open(id)  { document.getElementById(id)?.classList.add('open'); },
    close(id) { document.getElementById(id)?.classList.remove('open'); },

    closeOnOverlay(id) {
        document.getElementById(id)?.addEventListener('click', function(e) {
            if (e.target === this) Modal.close(id);
        });
    },

    confirm(message, onConfirm) {
        if (window.confirm(message)) onConfirm();
    }
};

/* ============================================================
   TABLE BUILDER
   Builds a table with Edit + Delete action buttons
   ============================================================ */
const Table = {
    /**
     * @param {string} tbodyId  — id of <tbody>
     * @param {Array}  rows     — data rows
     * @param {Array}  cols     — [{ key, label, render? }]
     * @param {Object} actions  — { edit: fn(row), delete: fn(row), extra: [{label,icon,cls,fn}] }
     */
    render(tbodyId, rows, cols, actions = {}) {
        const tbody = document.getElementById(tbodyId);
        if (!tbody) return;

        if (!rows || rows.length === 0) {
            tbody.innerHTML = `<tr><td colspan="${cols.length + 1}" style="text-align:center;padding:32px;color:var(--text-muted);font-style:italic;">No records found</td></tr>`;
            return;
        }

        tbody.innerHTML = rows.map(row => {
            const cells = cols.map(col => {
                const val = col.render ? col.render(row) : (row[col.key] ?? '—');
                return `<td>${val}</td>`;
            }).join('');

            const btns = [];
            if (actions.edit)   btns.push(`<button class="btn btn-sm btn-sky btn-icon" title="Edit" onclick='_tableEdit(${JSON.stringify(row)})'>✏️</button>`);
            if (actions.delete) btns.push(`<button class="btn btn-sm btn-rust btn-icon" title="Delete" onclick='_tableDelete(${row.id || row[cols[0].key]})'>🗑</button>`);
            if (actions.extra)  actions.extra.forEach(ex => {
                btns.push(`<button class="btn btn-sm ${ex.cls||'btn-outline'} btn-icon" title="${ex.label}" onclick='${ex.fn}(${JSON.stringify(row)})'>${ex.icon}</button>`);
            });

            return `<tr>${cells}<td><div class="td-actions">${btns.join('')}</div></td></tr>`;
        }).join('');

        // Store callbacks for inline onclick
        if (actions.edit)   window._tableEdit   = actions.edit;
        if (actions.delete) window._tableDelete = actions.delete;
    }
};

/* ============================================================
   SIDEBAR ACTIVE STATE
   ============================================================ */
function initSidebarActive() {
    const page = window.location.pathname.split('/').pop();
    document.querySelectorAll('.nav-item').forEach(item => {
        const href = item.getAttribute('href') || '';
        if (href === page) item.classList.add('active');
    });
}

/* ============================================================
   USER CARD INIT
   Fills sidebar user card with stored user data
   ============================================================ */
function initUserCard() {
    const user = Auth.getUser();
    const nameEl = document.getElementById('sidebarUserName');
    const roleEl = document.getElementById('sidebarUserRole');
    if (nameEl && user.fullName) nameEl.textContent = user.fullName;
    if (roleEl && user.role)     roleEl.textContent  = user.role;
}

/* ============================================================
   FORM HELPERS
   ============================================================ */
const Form = {
    /** Collect all inputs/selects/textareas in a form by name */
    collect(formId) {
        const form = document.getElementById(formId);
        if (!form) return {};
        const data = {};
        form.querySelectorAll('[name]').forEach(el => {
            data[el.name] = el.value;
        });
        return data;
    },

    /** Fill form fields from an object */
    fill(formId, data) {
        const form = document.getElementById(formId);
        if (!form) return;
        Object.entries(data).forEach(([k, v]) => {
            const el = form.querySelector(`[name="${k}"]`);
            if (el) el.value = v ?? '';
        });
    },

    /** Reset all fields */
    reset(formId) {
        document.getElementById(formId)?.reset();
    }
};

/* ============================================================
   STATUS BADGE HELPER
   ============================================================ */
function statusBadge(status) {
    const map = {
        PENDING:   ['badge-amber', '⏳ Pending'],
        APPROVED:  ['badge-green', '✅ Approved'],
        CONFIRMED: ['badge-green', '✅ Confirmed'],
        CANCELLED: ['badge-rust',  '❌ Cancelled'],
        COMPLETED: ['badge-sky',   '✔ Completed'],
        VALID:     ['badge-green', '✅ Valid'],
        EXPIRED:   ['badge-rust',  '⚠️ Expired'],
        GOOD:      ['badge-green', '🟢 Good'],
        BAD:       ['badge-rust',  '🔴 Bad'],
        FRIENDLY:  ['badge-green', '🟢 Friendly'],
        ACTIVE:    ['badge-amber', '🟡 Active'],
        DANGEROUS: ['badge-rust',  '🔴 Dangerous'],
    };
    const [cls, label] = map[status] || ['badge-bark', status];
    return `<span class="badge ${cls}">${label}</span>`;
}

/* ============================================================
   COUNTER ANIMATION
   ============================================================ */
function animateCount(elId, target, duration = 900) {
    const el = document.getElementById(elId);
    if (!el) return;
    let start = 0;
    const step = Math.ceil(target / (duration / 30));
    const t = setInterval(() => {
        start = Math.min(start + step, target);
        el.textContent = start;
        if (start >= target) clearInterval(t);
    }, 30);
}

/* ============================================================
   INIT ON LOAD
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    initSidebarActive();
    initUserCard();
});