// Components Configuration
document.addEventListener('DOMContentLoaded', function() {
    // Card Components
    const cards = document.querySelectorAll('.card');
    if (cards.length > 0) {
        cards.forEach(function(card) {
            // Card Actions
            const actions = card.querySelector('.card-actions');
            if (actions) {
                card.classList.add('has-actions');
            }
            
            // Card Collapse
            const collapseToggle = card.querySelector('[data-bs-toggle="collapse"]');
            if (collapseToggle) {
                const target = collapseToggle.getAttribute('data-bs-target');
                const collapseElement = document.querySelector(target);
                
                if (collapseElement) {
                    collapseToggle.addEventListener('click', function() {
                        collapseElement.classList.toggle('show');
                    });
                }
            }
            
            // Card Refresh
            const refreshButton = card.querySelector('.card-refresh');
            if (refreshButton) {
                refreshButton.addEventListener('click', function() {
                    const icon = refreshButton.querySelector('i');
                    if (icon) {
                        icon.classList.add('rotate');
                        setTimeout(function() {
                            icon.classList.remove('rotate');
                        }, 1000);
                    }
                });
            }
            
            // Card Remove
            const removeButton = card.querySelector('.card-remove');
            if (removeButton) {
                removeButton.addEventListener('click', function() {
                    card.classList.add('removing');
                    setTimeout(function() {
                        card.remove();
                    }, 300);
                });
            }
        });
    }
    
    // Table Components
    const tables = document.querySelectorAll('.table');
    if (tables.length > 0) {
        tables.forEach(function(table) {
            // Table Responsive
            const wrapper = table.closest('.table-responsive');
            if (wrapper) {
                wrapper.classList.add('table-responsive-wrapper');
            }
            
            // Table Checkbox
            const checkboxes = table.querySelectorAll('input[type="checkbox"]');
            if (checkboxes.length > 0) {
                const selectAll = table.querySelector('.select-all');
                if (selectAll) {
                    selectAll.addEventListener('change', function() {
                        checkboxes.forEach(function(checkbox) {
                            checkbox.checked = selectAll.checked;
                        });
                    });
                }
            }
            
            // Table Search
            const searchInput = table.closest('.card').querySelector('.table-search');
            if (searchInput) {
                searchInput.addEventListener('input', function() {
                    const searchText = this.value.toLowerCase();
                    const rows = table.querySelectorAll('tbody tr');
                    
                    rows.forEach(function(row) {
                        const text = row.textContent.toLowerCase();
                        row.style.display = text.includes(searchText) ? '' : 'none';
                    });
                });
            }
            
            // Table Pagination
            const pagination = table.closest('.card').querySelector('.table-pagination');
            if (pagination) {
                const itemsPerPage = parseInt(pagination.getAttribute('data-items-per-page')) || 10;
                const rows = table.querySelectorAll('tbody tr');
                const totalPages = Math.ceil(rows.length / itemsPerPage);
                
                // Create pagination controls
                const paginationControls = document.createElement('div');
                paginationControls.className = 'pagination-controls';
                
                for (let i = 1; i <= totalPages; i++) {
                    const button = document.createElement('button');
                    button.className = 'btn btn-sm btn-outline-primary';
                    button.textContent = i;
                    
                    if (i === 1) {
                        button.classList.add('active');
                    }
                    
                    button.addEventListener('click', function() {
                        // Update active state
                        paginationControls.querySelectorAll('button').forEach(function(btn) {
                            btn.classList.remove('active');
                        });
                        this.classList.add('active');
                        
                        // Show/hide rows
                        const start = (i - 1) * itemsPerPage;
                        const end = start + itemsPerPage;
                        
                        rows.forEach(function(row, index) {
                            row.style.display = index >= start && index < end ? '' : 'none';
                        });
                    });
                    
                    paginationControls.appendChild(button);
                }
                
                pagination.appendChild(paginationControls);
                
                // Show first page by default
                rows.forEach(function(row, index) {
                    row.style.display = index < itemsPerPage ? '' : 'none';
                });
            }
        });
    }
    
    // Form Components
    const forms = document.querySelectorAll('form');
    if (forms.length > 0) {
        forms.forEach(function(form) {
            // Form Validation
            if (form.classList.contains('needs-validation')) {
                form.addEventListener('submit', function(event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    
                    form.classList.add('was-validated');
                });
            }
            
            // Form Input Mask
            const maskedInputs = form.querySelectorAll('[data-mask]');
            if (maskedInputs.length > 0) {
                maskedInputs.forEach(function(input) {
                    const mask = input.getAttribute('data-mask');
                    if (mask) {
                        input.addEventListener('input', function() {
                            let value = this.value.replace(/\D/g, '');
                            let result = '';
                            let index = 0;
                            
                            for (let i = 0; i < mask.length; i++) {
                                if (mask[i] === '#') {
                                    result += value[index] || '';
                                    index++;
                                } else {
                                    result += mask[i];
                                }
                            }
                            
                            this.value = result;
                        });
                    }
                });
            }
            
            // Form Select2
            const select2Inputs = form.querySelectorAll('.select2');
            if (select2Inputs.length > 0 && typeof $.fn.select2 !== 'undefined') {
                select2Inputs.forEach(function(input) {
                    $(input).select2({
                        theme: 'bootstrap-5',
                        width: '100%'
                    });
                });
            }
            
            // Form Datepicker
            const datepickerInputs = form.querySelectorAll('.datepicker');
            if (datepickerInputs.length > 0 && typeof $.fn.datepicker !== 'undefined') {
                datepickerInputs.forEach(function(input) {
                    $(input).datepicker({
                        format: 'dd/mm/yyyy',
                        autoclose: true,
                        todayHighlight: true
                    });
                });
            }
            
            // Form Timepicker
            const timepickerInputs = form.querySelectorAll('.timepicker');
            if (timepickerInputs.length > 0 && typeof $.fn.timepicker !== 'undefined') {
                timepickerInputs.forEach(function(input) {
                    $(input).timepicker({
                        showMeridian: false,
                        minuteStep: 1
                    });
                });
            }
        });
    }
    
    // Modal Components
    const modals = document.querySelectorAll('.modal');
    if (modals.length > 0) {
        modals.forEach(function(modal) {
            // Modal Events
            modal.addEventListener('show.bs.modal', function() {
                document.body.classList.add('modal-open');
            });
            
            modal.addEventListener('hidden.bs.modal', function() {
                document.body.classList.remove('modal-open');
            });
            
            // Modal Form
            const form = modal.querySelector('form');
            if (form) {
                form.addEventListener('submit', function(event) {
                    event.preventDefault();
                    
                    // Simulate form submission
                    const submitButton = form.querySelector('[type="submit"]');
                    if (submitButton) {
                        submitButton.disabled = true;
                        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Enviando...';
                        
                        setTimeout(function() {
                            submitButton.disabled = false;
                            submitButton.innerHTML = 'Enviar';
                            modal.querySelector('.btn-close').click();
                        }, 1500);
                    }
                });
            }
        });
    }
    
    // Alert Components
    const alerts = document.querySelectorAll('.alert');
    if (alerts.length > 0) {
        alerts.forEach(function(alert) {
            // Alert Dismiss
            const dismissButton = alert.querySelector('.btn-close');
            if (dismissButton) {
                dismissButton.addEventListener('click', function() {
                    alert.classList.add('fade');
                    setTimeout(function() {
                        alert.remove();
                    }, 150);
                });
            }
            
            // Auto Dismiss
            const autoDismiss = alert.getAttribute('data-auto-dismiss');
            if (autoDismiss) {
                setTimeout(function() {
                    alert.classList.add('fade');
                    setTimeout(function() {
                        alert.remove();
                    }, 150);
                }, parseInt(autoDismiss));
            }
        });
    }
    
    // Toast Components
    const toasts = document.querySelectorAll('.toast');
    if (toasts.length > 0) {
        toasts.forEach(function(toast) {
            // Toast Dismiss
            const dismissButton = toast.querySelector('.btn-close');
            if (dismissButton) {
                dismissButton.addEventListener('click', function() {
                    toast.classList.remove('show');
                });
            }
            
            // Auto Dismiss
            const autoDismiss = toast.getAttribute('data-auto-dismiss');
            if (autoDismiss) {
                setTimeout(function() {
                    toast.classList.remove('show');
                }, parseInt(autoDismiss));
            }
        });
    }
    
    // Tooltip Components
    const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    if (tooltips.length > 0 && typeof bootstrap !== 'undefined') {
        tooltips.forEach(function(tooltip) {
            new bootstrap.Tooltip(tooltip);
        });
    }
    
    // Popover Components
    const popovers = document.querySelectorAll('[data-bs-toggle="popover"]');
    if (popovers.length > 0 && typeof bootstrap !== 'undefined') {
        popovers.forEach(function(popover) {
            new bootstrap.Popover(popover);
        });
    }
    
    // Dropdown Components
    const dropdowns = document.querySelectorAll('.dropdown');
    if (dropdowns.length > 0) {
        dropdowns.forEach(function(dropdown) {
            const toggle = dropdown.querySelector('.dropdown-toggle');
            const menu = dropdown.querySelector('.dropdown-menu');
            
            if (toggle && menu) {
                toggle.addEventListener('click', function(e) {
                    e.preventDefault();
                    menu.classList.toggle('show');
                });
                
                document.addEventListener('click', function(e) {
                    if (!dropdown.contains(e.target)) {
                        menu.classList.remove('show');
                    }
                });
            }
        });
    }
    
    // Avatar Components
    const avatars = document.querySelectorAll('.avatar');
    if (avatars.length > 0) {
        avatars.forEach(function(avatar) {
            // Avatar Initials
            if (avatar.classList.contains('avatar-initials')) {
                const text = avatar.textContent.trim();
                if (text) {
                    const initials = text.split(' ').map(function(word) {
                        return word.charAt(0);
                    }).join('');
                    
                    avatar.textContent = initials;
                    avatar.style.backgroundColor = getRandomColor();
                }
            }
            
            // Avatar Status
            const status = avatar.getAttribute('data-status');
            if (status) {
                const statusDot = document.createElement('span');
                statusDot.className = 'avatar-status bg-' + status;
                avatar.appendChild(statusDot);
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