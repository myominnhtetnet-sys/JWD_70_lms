
/*<![CDATA[*/
// State Management
let currentRole = 'admin';

// Mock Data
let usersData = [
	{ id: 1, name: 'Sarah Jenkins', email: 'sarah.j@edupulse.edu', role: 'Admin', status: 'Active', joined: 'Jan 12, 2024', avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=120' },
	{ id: 2, name: 'Dr. David Vance', email: 'david.vance@edupulse.edu', role: 'Teacher', status: 'Active', joined: 'Mar 04, 2023', avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=120' },
	{ id: 3, name: 'Alex Morgan', email: 'alex.m@student.edu', role: 'Student', status: 'Active', joined: 'Sep 01, 2025', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=120' },
	{ id: 4, name: 'Prof. Marcus Brody', email: 'm.brody@edupulse.edu', role: 'Teacher', status: 'Active', joined: 'Nov 18, 2022', avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=120' },
	{ id: 5, name: 'Chloe Zhao', email: 'chloe.z@student.edu', role: 'Student', status: 'Suspended', joined: 'Oct 14, 2025', avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&q=80&w=120' }
];

const teacherGradingQueue = [
	{ id: 101, student: 'Alex Morgan', course: 'CS-301', title: 'B-Tree Indexing Project', submitted: '2 hours ago' },
	{ id: 102, student: 'Liam Neeson', course: 'AI-402', title: 'Gradient Descent Analysis', submitted: '5 hours ago' },
	{ id: 103, student: 'Emma Watson', course: 'CS-301', title: 'Hash Collisions Lab', submitted: 'Yesterday' }
];

const studentCourses = [
	{ id: 'CS-301', title: 'Advanced Data Structures', instructor: 'Prof. David Vance', progress: 78, nextLesson: 'B-Trees & Indexing' },
	{ id: 'AI-402', title: 'Machine Learning Fundamentals', instructor: 'Prof. Marcus Brody', progress: 45, nextLesson: 'Neural Networks Basics' },
	{ id: 'ENG-201', title: 'Technical Writing for Engineers', instructor: 'Dr. Linda Carter', progress: 90, nextLesson: 'Final Research Paper' }
];

const studentDeadlines = [
	{ course: 'CS-301', title: 'B-Trees Implementation Lab', due: 'Today, 11:59 PM', badgeBg: 'bg-rose-100 text-rose-700 dark:bg-rose-950 dark:text-rose-300' },
	{ course: 'AI-402', title: 'Linear Regression Notebook', due: 'Oct 2, 2026', badgeBg: 'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300' }
];

// Sidebar Navigation Definitions
const navMenus = {
	admin: [
		{ icon: 'fa-chart-pie', label: 'Dashboard', active: true },
		{ icon: 'fa-users', label: 'Users & Roles' },
		{ icon: 'fa-book-open', label: 'Global Courses' },
		{ icon: 'fa-chart-line', label: 'Analytics' },
		{ icon: 'fa-file-invoice-dollar', label: 'Financials' },
		{ icon: 'fa-gear', label: 'System Settings' }
	],
	/*teacher: [
		{ icon: 'fa-house', label: 'Overview', active: true },
		{ icon: 'fa-chalkboard', label: 'My Classes' },
		{ icon: 'fa-list-check', label: 'Grading Queue' },
		{ icon: 'fa-graduation-cap', label: 'Student Performance' },
		{ icon: 'fa-folder-tree', label: 'Curriculum Builder' },
		{ icon: 'fa-database', label: 'Question Bank' },
		{ icon: 'fa-clipboard-list', label: 'Assignments' },
		{ icon: 'fa-comments', label: 'Messages' }
	],*/
	teacher: [
	    { icon: 'fa-house', label: 'Dashboard', active: true },
	    { icon: 'fa-book-open', label: 'My Courses' },
	    { icon: 'fa-users', label: 'My Batches' },
	    { icon: 'fa-calendar-check', label: 'Take Attendance' },
	    { icon: 'fa-clipboard-list', label: 'Assignments' },
	    { icon: 'fa-pen-to-square', label: 'Grade Submissions' },
	    { icon: 'fa-circle-question', label: 'Question Bank' },
	    { icon: 'fa-file-circle-check', label: 'Exams' },
	    { icon: 'fa-chart-column', label: 'Exam Results' },
	    { icon: 'fa-award', label: 'Final Grades' }
	],
	student: [
		{ icon: 'fa-compass', label: 'Dashboard', active: true },
		{ icon: 'fa-book-bookmark', label: 'My Courses' },
		{ icon: 'fa-pencil', label: 'Assignments' },
		{ icon: 'fa-trophy', label: 'Grades & Certificates' },
		{ icon: 'fa-box-archive', label: 'Resources' },
		{ icon: 'fa-calendar', label: 'Schedule' }
	]
};

// User Profiles Mapping
const profiles = {
	admin: { name: 'Sarah Jenkins', role: 'Administrator', avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=120' },
	teacher: { name: 'Prof. David Vance', role: 'Senior Lecturer', avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=120' },
	student: { name: 'Alex Morgan', role: 'Computer Science Major', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=120' }
};

// Initialize App
window.onload = function() {
	switchRole('admin');
	renderUserTable();
	renderTeacherGradingQueue();
	renderStudentViews();
	initCharts();
};

// Role Switching Logic
function switchRole(role) {
	currentRole = role;

	document.querySelectorAll('.role-btn').forEach(btn => {
		btn.className = "role-btn text-xs font-semibold px-3 sm:px-4 py-1.5 rounded-lg flex items-center gap-2 transition-all text-slate-600 dark:text-slate-300 hover:bg-slate-200/60 dark:hover:bg-slate-600";
	});
	const activeBtn = document.getElementById(`role-btn-${role}`);
	if (activeBtn) {
		activeBtn.className = "role-btn text-xs font-bold px-3 sm:px-4 py-1.5 rounded-lg flex items-center gap-2 transition-all bg-white dark:bg-slate-800 text-indigo-600 dark:text-indigo-400 shadow-sm border border-slate-200/50 dark:border-slate-700";
	}

	const p = profiles[role];
	if (document.getElementById('user-name')) document.getElementById('user-name').innerText = p.name;
	if (document.getElementById('user-role-label')) document.getElementById('user-role-label').innerText = p.role;
	if (document.getElementById('user-avatar')) document.getElementById('user-avatar').src = p.avatar;

	renderSidebar(role);

	document.querySelectorAll('.role-view').forEach(v => v.classList.add('hidden'));
	const targetView = document.getElementById(`view-${role}`);
	if (targetView) {
		targetView.classList.remove('hidden');
		targetView.classList.add('fade-in');
	}

	showToast(`Switched to ${role.toUpperCase()} View`);
}

function renderSidebar(role) {
	const menuContainer = document.getElementById('sidebar-menu');
	const categoryLabel = document.getElementById('sidebar-category');

	if (categoryLabel) categoryLabel.innerText = `${role.toUpperCase()} MENU`;
	if (menuContainer) {
		menuContainer.innerHTML = '';
		navMenus[role].forEach(item => {
			const navItem = document.createElement('a');
			navItem.href = '#';
			/*navItem.onclick = (e) => {
				e.preventDefault();
				setActiveNavItem(navItem);
				showToast(`Navigated to ${item.label}`);
			};*/
			/*navItem.onclick = (e) => {
				e.preventDefault();
				if (item.label === 'Question Bank') {
					window.location.href = '/question-list';
					return;
				}
				setActiveNavItem(navItem);
				showToast(`Navigated to ${item.label}`);
			};*/
			navItem.onclick = (e) => {
			    e.preventDefault();

			    // Question Bank
			    if (item.label === 'Question Bank') {
			        window.location.href = '/question-list';
			        return;
			    }

			    // Assignment
			    if (item.label === 'Assignments') {
			        window.location.href = '/teacher/assignments';
			        return;
			    }
				// Exams
				   if (item.label === 'Exams') {
				       window.location.href = '/teacher/exams';
				       return;
				   }

			    setActiveNavItem(navItem);
			    showToast(`Navigated to ${item.label}`);
			};

			const baseClasses = "flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-medium transition-all";
			const activeClasses = item.active
				? "bg-indigo-600 text-white font-semibold shadow-md shadow-indigo-600/30"
				: "text-slate-400 hover:text-slate-200 hover:bg-slate-800/80";

			navItem.className = `${baseClasses} ${activeClasses}`;
			navItem.innerHTML = `<i class="fa-solid ${item.icon} w-4 text-center"></i> <span>${item.label}</span>`;
			menuContainer.appendChild(navItem);
		});
	}
}

function setActiveNavItem(selectedItem) {
	const items = document.querySelectorAll('#sidebar-menu a');
	items.forEach(el => {
		el.className = "flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-medium text-slate-400 hover:text-slate-200 hover:bg-slate-800/80 transition-all";
	});
	selectedItem.className = "flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-semibold bg-indigo-600 text-white shadow-md shadow-indigo-600/30 transition-all";
}

function renderUserTable(filteredData = usersData) {
	const tbody = document.getElementById('userTableBody');
	if (!tbody) return;
	tbody.innerHTML = '';

	filteredData.forEach(user => {
		const tr = document.createElement('tr');
		tr.className = "hover:bg-slate-50 dark:hover:bg-slate-700/30 transition";

		const roleColor = user.role === 'Admin' ? 'bg-purple-100 text-purple-700 dark:bg-purple-950 dark:text-purple-300' :
			user.role === 'Teacher' ? 'bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300' :
				'bg-slate-100 text-slate-700 dark:bg-slate-700 dark:text-slate-300';

		const statusColor = user.status === 'Active' ? 'bg-emerald-500' : 'bg-rose-500';

		tr.innerHTML = `
                    <td class="px-6 py-4 flex items-center gap-3">
                        <img src="${user.avatar}" class="w-8 h-8 rounded-full object-cover">
                        <div>
                            <div class="font-bold text-slate-900 dark:text-white">${user.name}</div>
                            <div class="text-xs text-slate-400">${user.email}</div>
                        </div>
                    </td>
                    <td class="px-6 py-4">
                        <span class="text-[10px] font-bold px-2.5 py-1 rounded-full ${roleColor}">${user.role}</span>
                    </td>
                    <td class="px-6 py-4">
                        <span class="flex items-center gap-1.5 text-xs">
                            <span class="w-1.5 h-1.5 rounded-full ${statusColor}"></span> ${user.status}
                        </span>
                    </td>
                    <td class="px-6 py-4 text-xs text-slate-400">${user.joined}</td>
                    <td class="px-6 py-4 text-right">
                        <button onclick="deleteUser(${user.id})" class="text-slate-400 hover:text-rose-500 transition p-1"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
		tbody.appendChild(tr);
	});
}

function filterUserTable() {
	const searchInput = document.getElementById('userTableSearch');
	const roleSelect = document.getElementById('roleFilter');
	if (!searchInput || !roleSelect) return;

	const searchVal = searchInput.value.toLowerCase();
	const roleVal = roleSelect.value;

	const filtered = usersData.filter(user => {
		const matchesSearch = user.name.toLowerCase().includes(searchVal) || user.email.toLowerCase().includes(searchVal);
		const matchesRole = (roleVal === 'all') || (user.role === roleVal);
		return matchesSearch && matchesRole;
	});

	renderUserTable(filtered);
}

function deleteUser(userId) {
	usersData = usersData.filter(u => u.id !== userId);
	renderUserTable();
	showToast('User removed successfully');
}

function renderTeacherGradingQueue() {
	const list = document.getElementById('grading-queue-list');
	if (!list) return;
	list.innerHTML = '';

	teacherGradingQueue.forEach(item => {
		const div = document.createElement('div');
		div.className = "p-3 bg-slate-50 dark:bg-slate-700/50 rounded-xl flex items-center justify-between border border-slate-100 dark:border-slate-700";
		div.innerHTML = `
                    <div>
                        <div class="text-xs font-bold text-slate-900 dark:text-white">${item.student}</div>
                        <div class="text-[11px] text-slate-500">${item.course}: ${item.title}</div>
                        <div class="text-[10px] text-slate-400 mt-0.5">${item.submitted}</div>
                    </div>
                    <button onclick="gradeSubmission(${item.id})" class="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-semibold transition">
                        Grade
                    </button>
                `;
		list.appendChild(div);
	});
}

function gradeSubmission(id) {
	showToast('Grade submitted for assignment #' + id);
}

function renderStudentViews() {
	const coursesContainer = document.getElementById('student-courses-container');
	if (coursesContainer) {
		coursesContainer.innerHTML = '';
		studentCourses.forEach(c => {
			const div = document.createElement('div');
			div.className = "bg-white dark:bg-slate-800 p-5 rounded-2xl border border-slate-200/80 dark:border-slate-700/80 shadow-sm flex flex-col justify-between";
			div.innerHTML = `
                        <div>
                            <div class="flex justify-between items-start mb-2">
                                <span class="px-2 py-0.5 text-[10px] font-bold rounded bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400">${c.id}</span>
                                <span class="text-xs font-bold text-indigo-600">${c.progress}%</span>
                            </div>
                            <h4 class="font-bold text-slate-900 dark:text-white text-base">${c.title}</h4>
                            <p class="text-xs text-slate-400 mt-1">${c.instructor}</p>
                        </div>
                        <div class="mt-6">
                            <div class="w-full h-2 bg-slate-100 dark:bg-slate-700 rounded-full overflow-hidden mb-3">
                                <div class="h-full bg-indigo-600 rounded-full" style="width: ${c.progress}%"></div>
                            </div>
                            <p class="text-[11px] text-slate-500 dark:text-slate-400 mb-3"><i class="fa-solid fa-play-circle text-indigo-500 mr-1"></i> Next: ${c.nextLesson}</p>
                            <button onclick="showToast('Resuming lesson: ${c.nextLesson}')" class="w-full py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-semibold transition shadow-sm">
                                Resume Learning
                            </button>
                        </div>
                    `;
			coursesContainer.appendChild(div);
		});
	}

	const deadlinesList = document.getElementById('student-deadlines-list');
	if (deadlinesList) {
		deadlinesList.innerHTML = '';
		studentDeadlines.forEach(d => {
			const div = document.createElement('div');
			div.className = "p-3 bg-slate-50 dark:bg-slate-700/50 rounded-xl flex items-center justify-between";
			div.innerHTML = `
                        <div>
                            <div class="text-xs font-bold text-slate-900 dark:text-white">${d.title}</div>
                            <div class="text-[11px] text-slate-500">${d.course}</div>
                        </div>
                        <span class="text-[10px] font-bold px-2 py-1 rounded ${d.badgeBg}">${d.due}</span>
                    `;
			deadlinesList.appendChild(div);
		});
	}
}

function openModal(id) {
	const el = document.getElementById(id);
	if (el) el.classList.remove('hidden');
}

function closeModal(id) {
	const el = document.getElementById(id);
	if (el) el.classList.add('hidden');
}

function handleAddUser(e) {
	e.preventDefault();
	const name = document.getElementById('new-user-name').value;
	const email = document.getElementById('new-user-email').value;
	const role = document.getElementById('new-user-role').value;

	usersData.unshift({
		id: Date.now(),
		name,
		email,
		role,
		status: 'Active',
		joined: 'Just now',
		avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=120'
	});

	renderUserTable();
	closeModal('user-modal');
	showToast(`User ${name} created as ${role}!`);
	e.target.reset();
}

function handleCreateAssignment(e) {
	e.preventDefault();
	closeModal('assignment-modal');
	showToast('Assignment successfully published to students!');
}

function handleSubmitAssignment(e) {
	e.preventDefault();
	closeModal('submit-work-modal');
	showToast('Homework file uploaded successfully!');
}

function handleSendAnnouncement(e) {
	e.preventDefault();
	closeModal('announcement-modal');
	showToast('Broadcast notification sent to all users!');
}

function showToast(message) {
	const toast = document.getElementById('toast');
	const msgEl = document.getElementById('toast-message');
	if (toast && msgEl) {
		msgEl.innerText = message;
		toast.classList.remove('translate-y-20', 'opacity-0');

		setTimeout(() => {
			toast.classList.add('translate-y-20', 'opacity-0');
		}, 3000);
	}
}

function toggleNotifications() {
	const el = document.getElementById('notif-dropdown');
	if (el) el.classList.toggle('hidden');
}

function toggleDarkMode() {
	document.documentElement.classList.toggle('dark');
	const themeIcon = document.getElementById('theme-icon');
	if (themeIcon) {
		if (document.documentElement.classList.contains('dark')) {
			themeIcon.className = "fa-solid fa-sun text-amber-400 text-lg";
		} else {
			themeIcon.className = "fa-solid fa-moon text-lg";
		}
	}
}

function initCharts() {
	const canvas = document.getElementById('adminEnrollmentChart');
	if (!canvas || typeof Chart === 'undefined') return;
	const ctx = canvas.getContext('2d');
	new Chart(ctx, {
		type: 'line',
		data: {
			labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
			datasets: [{
				label: 'Student Registrations',
				data: [650, 720, 800, 890, 960, 1050],
				borderColor: '#4f46e5',
				backgroundColor: 'rgba(79, 70, 229, 0.1)',
				fill: true,
				tension: 0.4
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			plugins: {
				legend: { display: false }
			},
			scales: {
				y: { grid: { color: 'rgba(0,0,0,0.05)' } },
				x: { grid: { display: false } }
			}
		}
	});
}
/*]]>*/
