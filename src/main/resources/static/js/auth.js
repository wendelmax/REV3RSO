// Função para obter as credenciais de autenticação
function getAuthCredentials() {
    return localStorage.getItem('auth_credentials') || sessionStorage.getItem('auth_credentials');
}

// Função para fazer logout
function fazerLogout() {
    localStorage.removeItem('auth_credentials');
    sessionStorage.removeItem('auth_credentials');
    window.location.href = '/login';
}

// Função para verificar se o usuário está autenticado
function isAutenticado() {
    return !!getAuthCredentials();
}

// Função para adicionar o header de autenticação em todas as requisições
function adicionarAuthHeader(headers = {}) {
    const credentials = getAuthCredentials();
    if (credentials) {
        headers['Authorization'] = 'Basic ' + credentials;
    }
    return headers;
}

// Intercepta todas as requisições fetch para adicionar o header de autenticação
const originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
    options.headers = adicionarAuthHeader(options.headers || {});
    return originalFetch(url, options);
};

// Intercepta todas as requisições XMLHttpRequest para adicionar o header de autenticação
const originalXHROpen = XMLHttpRequest.prototype.open;
XMLHttpRequest.prototype.open = function(method, url) {
    const xhr = this;
    originalXHROpen.apply(xhr, arguments);
    
    const originalSetRequestHeader = xhr.setRequestHeader;
    xhr.setRequestHeader = function(header, value) {
        if (header.toLowerCase() === 'authorization') {
            return;
        }
        originalSetRequestHeader.apply(xhr, arguments);
    };
    
    const credentials = getAuthCredentials();
    if (credentials) {
        originalSetRequestHeader.call(xhr, 'Authorization', 'Basic ' + credentials);
    }
};

// Verifica se o usuário está autenticado ao carregar a página
document.addEventListener('DOMContentLoaded', function() {
    // Se não estiver na página de login e não estiver autenticado, redireciona para o login
    if (!window.location.pathname.includes('/login') && !isAutenticado()) {
        window.location.href = '/login';
    }
    
    // Se estiver na página de login e estiver autenticado, redireciona para o dashboard
    if (window.location.pathname.includes('/login') && isAutenticado()) {
        window.location.href = '/dashboard';
    }
}); 