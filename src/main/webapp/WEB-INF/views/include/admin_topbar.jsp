
<nav class="navbar navbar-light bg-white shadow-sm px-4 topbar" id="topbar">

    <form class="d-none d-md-flex me-auto">
        <div class="input-group">
            <input type="text" class="form-control" placeholder="Search for..." aria-label="Search">
            <button class="btn btn-outline-secondary" type="button"><i class="fa fa-search"></i></button>
        </div>
    </form>

    <div class="d-flex align-items-center ms-auto">

        <div class="dropdown me-3">
            <button class="btn btn-white position-relative" data-bs-toggle="dropdown">
                <i class="fa fa-bell"></i>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        3
                        <span class="visually-hidden">unread messages</span>
                    </span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">New user registered</a></li>
                <li><a class="dropdown-item" href="#">Course 'JS' updated</a></li>
            </ul>
        </div>

        <div class="dropdown me-4">
            <button class="btn btn-white position-relative" data-bs-toggle="dropdown">
                <i class="fa fa-envelope"></i>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning">
                        1
                        <span class="visually-hidden">unread messages</span>
                    </span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">Support message received</a></li>
            </ul>
        </div>

        <div class="dropdown">
            <button class="btn btn-white d-flex align-items-center" data-bs-toggle="dropdown">
                <img src="/assets/img/admin-avatar.png" class="rounded-circle me-2" width="35" height="35" alt="Admin Avatar">
                <span>Admin</span>
                <i class="fa fa-caret-down ms-2"></i>
            </button>

            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#"><i class="fa fa-user me-2"></i> Profile</a></li>
                <li>
                    <hr class="dropdown-divider">
                </li>
                <li><a class="dropdown-item text-danger" href="#"><i class="fa fa-sign-out-alt me-2"></i> Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>