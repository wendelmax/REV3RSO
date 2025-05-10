// Layout
document.addEventListener('DOMContentLoaded', function() {
    // Menu Toggle
    const menuToggle = document.querySelector('.layout-menu-toggle');
    const menu = document.querySelector('.layout-menu');
    const overlay = document.querySelector('.layout-overlay');
    
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            menu.classList.toggle('show');
            overlay.classList.toggle('show');
        });
    }
    
    if (overlay) {
        overlay.addEventListener('click', function() {
            menu.classList.remove('show');
            overlay.classList.remove('show');
        });
    }
    
    // Perfect Scrollbar
    if (typeof PerfectScrollbar !== 'undefined') {
        const menuInner = document.querySelector('.menu-inner');
        if (menuInner) {
            new PerfectScrollbar(menuInner, {
                suppressScrollX: true
            });
        }
        
        const notificationsList = document.querySelector('.dropdown-notifications-list');
        if (notificationsList) {
            new PerfectScrollbar(notificationsList, {
                suppressScrollX: true
            });
        }
    }
    
    // Dropdown Notifications
    const dropdownNotifications = document.querySelector('.dropdown-notifications');
    if (dropdownNotifications) {
        dropdownNotifications.addEventListener('show.bs.dropdown', function() {
            const notificationsList = this.querySelector('.dropdown-notifications-list');
            if (notificationsList && typeof PerfectScrollbar !== 'undefined') {
                new PerfectScrollbar(notificationsList, {
                    suppressScrollX: true
                });
            }
        });
    }
    
    // Active Menu Item
    const currentPath = window.location.pathname;
    const menuItems = document.querySelectorAll('.menu-item');
    
    menuItems.forEach(function(item) {
        const link = item.querySelector('.menu-link');
        if (link && link.getAttribute('href') === currentPath) {
            item.classList.add('active');
        }
    });
    
    // Form Validation
    const forms = document.querySelectorAll('.needs-validation');
    
    Array.from(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            form.classList.add('was-validated');
        }, false);
    });
    
    // Tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Modal Events
    const modals = document.querySelectorAll('.modal');
    modals.forEach(function(modal) {
        modal.addEventListener('show.bs.modal', function() {
            document.body.classList.add('modal-open');
        });
        
        modal.addEventListener('hidden.bs.modal', function() {
            document.body.classList.remove('modal-open');
        });
    });
    
    // Table Responsive
    const tables = document.querySelectorAll('.table-responsive');
    tables.forEach(function(table) {
        const wrapper = table.parentElement;
        if (wrapper && wrapper.classList.contains('card')) {
            wrapper.classList.add('table-responsive-wrapper');
        }
    });
    
    // Card Actions
    const cardActions = document.querySelectorAll('.card-actions');
    cardActions.forEach(function(action) {
        const card = action.closest('.card');
        if (card) {
            card.classList.add('has-actions');
        }
    });
    
    // Avatar Initials
    const avatars = document.querySelectorAll('.avatar-initial');
    avatars.forEach(function(avatar) {
        const text = avatar.textContent.trim();
        if (text) {
            avatar.style.backgroundColor = getRandomColor(text);
        }
    });
});

// Utility Functions
function getRandomColor(text) {
    const colors = [
        '#696cff', '#8592a3', '#71dd37', '#ff3e1d', '#ffab00',
        '#03c3ec', '#233446', '#566a7f', '#696cff', '#8592a3'
    ];
    
    let hash = 0;
    for (let i = 0; i < text.length; i++) {
        hash = text.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    return colors[Math.abs(hash) % colors.length];
}

// Theme Functions
function setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
}

function getTheme() {
    return localStorage.getItem('theme') || 'light';
}

// Initialize Theme
document.addEventListener('DOMContentLoaded', function() {
    const theme = getTheme();
    setTheme(theme);
}); 