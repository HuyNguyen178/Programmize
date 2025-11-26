document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('sidebar');
    const content = document.getElementById('content');
    const topbar = document.getElementById('topbar');
    const toggleBtn = document.getElementById('toggleSidebar');

    if (sidebar && content && topbar && toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            content.classList.toggle('expanded');
            topbar.classList.toggle('expanded');
        });
    }
});