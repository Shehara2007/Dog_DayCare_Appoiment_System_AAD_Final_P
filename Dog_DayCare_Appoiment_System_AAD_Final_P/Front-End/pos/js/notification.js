(function (window, $) {
    "use strict";

    const Auth = window.DogDaycareAuth;

    const BarkStayNotification = {
        notifications: [],
        
        init: function () {
            const session = Auth.getSession();
            if (!session || session.role !== 'PET_OWNER') return;

            this.setupEventListeners();
            this.fetchNotifications(session.userId);
            
            // Auto-refresh every 2 minutes
            setInterval(() => this.fetchNotifications(session.userId), 120000);
        },

        setupEventListeners: function () {
            const $bell = $('#notificationBell');
            const $dropdown = $('#notificationDropdown');

            $bell.on('click', (e) => {
                e.stopPropagation();
                $dropdown.toggleClass('show');
            });

            $(document).on('click', (e) => {
                if (!$(e.target).closest('#notificationContainer').length) {
                    $dropdown.removeClass('show');
                }
            });

            $('#markAllRead').on('click', () => {
                this.notifications = [];
                this.render();
            });
        },

        fetchNotifications: function (userId) {
            Auth.getNotificationsByOwner(userId)
                .done((data) => {
                    this.notifications = data || [];
                    this.render();
                })
                .fail((xhr) => {
                    console.error("Failed to fetch notifications:", xhr);
                    $('#notificationList').html('<div class="notification-empty">Error loading notifications.</div>');
                });
        },

        render: function () {
            const $list = $('#notificationList');
            const $badge = $('#notificationBadge');
            
            if (this.notifications.length === 0) {
                $list.html('<div class="notification-empty">No new notifications</div>');
                $badge.hide();
                return;
            }

            // Filter unread (if backend supports readFlag, assuming false for now)
            const unreadCount = this.notifications.filter(n => !n.readFlag).length;
            if (unreadCount > 0) {
                $badge.text(unreadCount).show();
            } else {
                $badge.hide();
            }

            const html = this.notifications.map(n => {
                const icon = n.type === 'VACCINATION_ALERT' ? '💉' : '🩺';
                const iconClass = n.type === 'VACCINATION_ALERT' ? 'vaccination' : 'health';
                const date = new Date(n.createdAt).toLocaleDateString();
                
                return `
                    <div class="notification-item ${!n.readFlag ? 'unread' : ''}">
                        <div class="notification-icon ${iconClass}">${icon}</div>
                        <div class="notification-content">
                            <p>${n.message}</p>
                            <span class="notification-time">${date}</span>
                        </div>
                    </div>
                `;
            }).join('');

            $list.html(html);
        }
    };

    window.BarkStayNotification = BarkStayNotification;
})(window, jQuery);
