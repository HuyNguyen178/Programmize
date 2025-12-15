document.addEventListener('DOMContentLoaded', function() {
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content;

    if (csrfToken) {
        // Tự động thêm CSRF token vào tất cả AJAX requests (Fetch API)
        const originalFetch = window.fetch;
        window.fetch = function(url, options = {}) {
            if (options.method && options.method !== 'GET') {
                options.headers = options.headers || {};
                options.headers['X-CSRF-Token'] = csrfToken;
            }
            return originalFetch(url, options);
        };

        // Nếu sử dụng jQuery
        if (typeof $ !== 'undefined') {
            $.ajaxSetup({
                beforeSend: function(xhr, settings) {
                    if (settings.type !== 'GET') {
                        xhr.setRequestHeader('X-CSRF-Token', csrfToken);
                    }
                }
            });
        }
    }
});