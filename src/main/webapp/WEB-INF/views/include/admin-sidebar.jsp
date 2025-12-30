<div class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <h3 class="sidebar-title label mb-0"><a href="<%=request.getContextPath()%>/dashboard">Programmize</a></h3>
        <button id="toggleSidebar" class="btn">
            <i class="fa fa-bars" id="toggleIcon"></i>
        </button>
    </div>

    <ul class="nav flex-column px-0">
        <li class="nav-item">
            <a href=<%=request.getContextPath()%>"/dashboard" class="nav-link text-white">
                <i class="fa fa-chart-line me-2"></i> <span class="label">Dashboard</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="account-list" class="nav-link text-white">
                <i class="fa fa-users me-2"></i> <span class="label">Account List</span>
            </a>
        </li>
        <li class="nav-item">
            <a href=<%=request.getContextPath()%>"/course-list" class="nav-link text-white">
                <i class="fa fa-book me-2"></i> <span class="label">Courses</span>
            </a>
        </li>
        <li class="nav-item">
            <a href=<%=request.getContextPath()%>"/class-list" class="nav-link text-white">
                <i class="fa fa-chalkboard me-2"></i> <span class="label">Classes</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="setting-list" class="nav-link text-white">
                <i class="fa fa-tags me-2"></i> <span class="label">Settings</span>
            </a>
        </li>
    </ul>
</div>