// Theme Configuration
document.addEventListener('DOMContentLoaded', function() {
    // Theme Toggle
    const themeToggle = document.querySelector('.theme-toggle');
    const html = document.documentElement;
    
    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        html.setAttribute('data-bs-theme', savedTheme);
        updateThemeIcon(savedTheme);
    } else {
        // Check system preference
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        const theme = prefersDark ? 'dark' : 'light';
        html.setAttribute('data-bs-theme', theme);
        localStorage.setItem('theme', theme);
        updateThemeIcon(theme);
    }
    
    // Theme toggle click handler
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            const currentTheme = html.getAttribute('data-bs-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            html.setAttribute('data-bs-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);
        });
    }
    
    // Update theme icon
    function updateThemeIcon(theme) {
        if (themeToggle) {
            const icon = themeToggle.querySelector('i');
            if (icon) {
                icon.className = theme === 'dark' ? 'bi bi-sun-fill' : 'bi bi-moon-fill';
            }
        }
    }
    
    // System theme change handler
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
        if (!localStorage.getItem('theme')) {
            const theme = e.matches ? 'dark' : 'light';
            html.setAttribute('data-bs-theme', theme);
            updateThemeIcon(theme);
        }
    });
    
    // Perfect Scrollbar
    if (typeof PerfectScrollbar !== 'undefined') {
        const menuScrollbar = document.querySelector('.menu-inner');
        if (menuScrollbar) {
            new PerfectScrollbar(menuScrollbar, {
                suppressScrollX: true
            });
        }
    }
    
    // Menu Toggle
    const menuToggle = document.querySelector('.menu-toggle');
    const menu = document.querySelector('.layout-menu');
    const overlay = document.querySelector('.layout-overlay');
    
    if (menuToggle && menu && overlay) {
        menuToggle.addEventListener('click', function() {
            menu.classList.toggle('collapsed');
            overlay.classList.toggle('show');
        });
        
        overlay.addEventListener('click', function() {
            menu.classList.add('collapsed');
            overlay.classList.remove('show');
        });
    }
    
    // Dropdown Notifications
    const notificationDropdown = document.querySelector('.dropdown-notifications');
    if (notificationDropdown) {
        const dropdownToggle = notificationDropdown.querySelector('.dropdown-toggle');
        const dropdownMenu = notificationDropdown.querySelector('.dropdown-menu');
        
        if (dropdownToggle && dropdownMenu) {
            dropdownToggle.addEventListener('click', function(e) {
                e.preventDefault();
                dropdownMenu.classList.toggle('show');
            });
            
            document.addEventListener('click', function(e) {
                if (!notificationDropdown.contains(e.target)) {
                    dropdownMenu.classList.remove('show');
                }
            });
        }
    }
    
    // Active Menu Item
    const menuItems = document.querySelectorAll('.menu-item');
    if (menuItems.length > 0) {
        const currentPath = window.location.pathname;
        
        menuItems.forEach(function(item) {
            const link = item.querySelector('.menu-link');
            if (link) {
                const href = link.getAttribute('href');
                if (href === currentPath) {
                    item.classList.add('active');
                }
            }
        });
    }
    
    // Form Validation
    const forms = document.querySelectorAll('.needs-validation');
    if (forms.length > 0) {
        Array.from(forms).forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                
                form.classList.add('was-validated');
            });
        });
    }
    
    // Tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    if (tooltipTriggerList.length > 0) {
        tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
    
    // Popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    if (popoverTriggerList.length > 0) {
        popoverTriggerList.map(function(popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });
    }
    
    // Modal Events
    const modals = document.querySelectorAll('.modal');
    if (modals.length > 0) {
        modals.forEach(function(modal) {
            modal.addEventListener('show.bs.modal', function() {
                document.body.classList.add('modal-open');
            });
            
            modal.addEventListener('hidden.bs.modal', function() {
                document.body.classList.remove('modal-open');
            });
        });
    }
    
    // Table Responsive
    const tables = document.querySelectorAll('.table-responsive');
    if (tables.length > 0) {
        tables.forEach(function(table) {
            const wrapper = table.parentElement;
            if (wrapper) {
                wrapper.classList.add('table-responsive-wrapper');
            }
        });
    }
    
    // Card Actions
    const cardActions = document.querySelectorAll('.card-actions');
    if (cardActions.length > 0) {
        cardActions.forEach(function(action) {
            const card = action.closest('.card');
            if (card) {
                card.classList.add('has-actions');
            }
        });
    }
    
    // Avatar Initials
    const avatars = document.querySelectorAll('.avatar-initials');
    if (avatars.length > 0) {
        avatars.forEach(function(avatar) {
            const text = avatar.textContent.trim();
            if (text) {
                const initials = text.split(' ').map(function(word) {
                    return word.charAt(0);
                }).join('');
                
                avatar.textContent = initials;
                avatar.style.backgroundColor = getRandomColor();
            }
        });
    }
    
    // Random Color Generator
    function getRandomColor() {
        const colors = [
            '#696cff',
            '#8592a3',
            '#71dd37',
            '#ff3e1d',
            '#ffab00',
            '#03c3ec'
        ];
        
        return colors[Math.floor(Math.random() * colors.length)];
    }
}); 