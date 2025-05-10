// Layout Configuration
document.addEventListener('DOMContentLoaded', function() {
    // Layout Elements
    const layoutWrapper = document.querySelector('.layout-wrapper');
    const layoutMenu = document.querySelector('.layout-menu');
    const layoutNavbar = document.querySelector('.layout-navbar');
    const layoutPage = document.querySelector('.layout-page');
    const layoutOverlay = document.querySelector('.layout-overlay');
    
    // Menu Elements
    const menuToggle = document.querySelector('.menu-toggle');
    const menuHeader = document.querySelector('.menu-header');
    const menuInner = document.querySelector('.menu-inner');
    const menuItems = document.querySelectorAll('.menu-item');
    const menuLinks = document.querySelectorAll('.menu-link');
    
    // Navbar Elements
    const navbarBrand = document.querySelector('.navbar-brand');
    const navbarSearch = document.querySelector('.navbar-search');
    const navbarUser = document.querySelector('.navbar-user');
    const navbarNotifications = document.querySelector('.navbar-notifications');
    
    // Content Elements
    const contentHeader = document.querySelector('.content-header');
    const contentBody = document.querySelector('.content-body');
    const contentFooter = document.querySelector('.content-footer');
    
    // Initialize Layout
    function initLayout() {
        // Check for saved menu state
        const menuCollapsed = localStorage.getItem('menuCollapsed') === 'true';
        if (menuCollapsed) {
            layoutMenu.classList.add('collapsed');
        }
        
        // Check for saved navbar state
        const navbarDetached = localStorage.getItem('navbarDetached') === 'true';
        if (navbarDetached) {
            layoutNavbar.classList.add('navbar-detached');
        }
        
        // Check for saved content state
        const contentLayout = localStorage.getItem('contentLayout');
        if (contentLayout) {
            layoutPage.setAttribute('data-layout', contentLayout);
        }
    }
    
    // Menu Toggle
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            layoutMenu.classList.toggle('collapsed');
            localStorage.setItem('menuCollapsed', layoutMenu.classList.contains('collapsed'));
        });
    }
    
    // Menu Overlay
    if (layoutOverlay) {
        layoutOverlay.addEventListener('click', function() {
            layoutMenu.classList.add('collapsed');
            localStorage.setItem('menuCollapsed', true);
        });
    }
    
    // Menu Items
    if (menuItems.length > 0) {
        menuItems.forEach(function(item) {
            const link = item.querySelector('.menu-link');
            const submenu = item.querySelector('.menu-submenu');
            
            if (link && submenu) {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    
                    // Close other submenus
                    menuItems.forEach(function(otherItem) {
                        if (otherItem !== item) {
                            otherItem.classList.remove('open');
                        }
                    });
                    
                    // Toggle current submenu
                    item.classList.toggle('open');
                });
            }
        });
    }
    
    // Navbar Search
    if (navbarSearch) {
        const searchInput = navbarSearch.querySelector('.form-control');
        const searchResults = navbarSearch.querySelector('.search-results');
        
        if (searchInput && searchResults) {
            searchInput.addEventListener('focus', function() {
                searchResults.classList.add('show');
            });
            
            searchInput.addEventListener('blur', function() {
                setTimeout(function() {
                    searchResults.classList.remove('show');
                }, 200);
            });
            
            searchInput.addEventListener('input', function() {
                // Implement search functionality here
            });
        }
    }
    
    // Navbar Notifications
    if (navbarNotifications) {
        const notificationToggle = navbarNotifications.querySelector('.dropdown-toggle');
        const notificationMenu = navbarNotifications.querySelector('.dropdown-menu');
        
        if (notificationToggle && notificationMenu) {
            notificationToggle.addEventListener('click', function(e) {
                e.preventDefault();
                notificationMenu.classList.toggle('show');
            });
            
            document.addEventListener('click', function(e) {
                if (!navbarNotifications.contains(e.target)) {
                    notificationMenu.classList.remove('show');
                }
            });
        }
    }
    
    // Navbar User
    if (navbarUser) {
        const userToggle = navbarUser.querySelector('.dropdown-toggle');
        const userMenu = navbarUser.querySelector('.dropdown-menu');
        
        if (userToggle && userMenu) {
            userToggle.addEventListener('click', function(e) {
                e.preventDefault();
                userMenu.classList.toggle('show');
            });
            
            document.addEventListener('click', function(e) {
                if (!navbarUser.contains(e.target)) {
                    userMenu.classList.remove('show');
                }
            });
        }
    }
    
    // Content Layout
    function setContentLayout(layout) {
        layoutPage.setAttribute('data-layout', layout);
        localStorage.setItem('contentLayout', layout);
    }
    
    // Content Header
    if (contentHeader) {
        const layoutToggle = contentHeader.querySelector('.layout-toggle');
        
        if (layoutToggle) {
            layoutToggle.addEventListener('click', function() {
                const currentLayout = layoutPage.getAttribute('data-layout');
                const newLayout = currentLayout === 'default' ? 'boxed' : 'default';
                setContentLayout(newLayout);
            });
        }
    }
    
    // Perfect Scrollbar
    if (typeof PerfectScrollbar !== 'undefined') {
        // Menu Scrollbar
        if (menuInner) {
            new PerfectScrollbar(menuInner, {
                suppressScrollX: true
            });
        }
        
        // Content Scrollbar
        if (contentBody) {
            new PerfectScrollbar(contentBody, {
                suppressScrollX: true
            });
        }
    }
    
    // Initialize Layout
    initLayout();
    
    // Window Resize Handler
    let resizeTimer;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
            // Update layout on resize
            if (window.innerWidth < 992) {
                layoutMenu.classList.add('collapsed');
                localStorage.setItem('menuCollapsed', true);
            }
        }, 250);
    });
}); 