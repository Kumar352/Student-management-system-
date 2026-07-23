document.addEventListener('DOMContentLoaded', () => {
    let globalData = []; 
    let riskChartInstance = null;
    let detailChartInstance = null;

    Chart.defaults.color = '#a1a1aa';
    Chart.defaults.font.family = "'Inter', sans-serif";
    Chart.defaults.scale.grid.color = '#222222';
    Chart.defaults.scale.grid.borderColor = 'transparent';

    // --- 1. GENERATE 100 MOCK FACULTY ---
    function generateFaculty() {
        const container = document.getElementById('facultyGridContainer');
        if(!container) return;
        
        const depts = ['Computer Science', 'Mechanical Eng.', 'Electronics', 'Civil Eng.', 'Information Tech'];
        const titles = ['Dr.', 'Prof.', 'Assoc. Prof.'];
        const fNames = ['Amit', 'Sara', 'Rahul', 'Priya', 'Vikram', 'Neha', 'Arjun', 'Kavya'];
        const lNames = ['Sharma', 'Menon', 'Verma', 'Das', 'Singh', 'Iyer', 'Patel', 'Reddy'];
        
        let html = '';
        for(let i=1; i<=100; i++) {
            let name = `${titles[i%3]} ${fNames[i%8]} ${lNames[i%8]}`;
            let dept = depts[i%5];
            let initial = name.split(' ')[1][0];
            let colors = ['var(--accent-blue)', 'var(--accent-green)', 'var(--accent-purple)', 'var(--accent-red)', 'var(--accent-orange)'];
            let color = colors[i%5];

            html += `
            <div class="kpi-card" style="display: flex; align-items: center; gap: 16px; padding: 15px;">
                <div class="avatar-large" style="width: 45px; height: 45px; font-size: 18px; margin: 0; background: ${color};">${initial}</div>
                <div><h3 style="font-size: 14px; margin-bottom: 2px;">${name}</h3><p style="font-size: 11px; color: var(--text-muted);">${dept}</p></div>
            </div>`;
        }
        container.innerHTML = html;
    }

    // --- 2. INITIALIZE CHARTS ---
    function initDashboardCharts() {
        const gpaCtx = document.getElementById('gpaTrendChart');
        if(!gpaCtx) return;
        let gradient = gpaCtx.getContext('2d').createLinearGradient(0, 0, 0, 300);
        gradient.addColorStop(0, 'rgba(0, 112, 243, 0.4)'); gradient.addColorStop(1, 'rgba(0, 112, 243, 0.0)'); 
        new Chart(gpaCtx.getContext('2d'), {
            type: 'line',
            data: { labels: ['Semester 1', 'Semester 2', 'Semester 3'], datasets: [{ label: 'GPA', data: [7.2, 7.5, 7.8], borderColor: '#0070f3', backgroundColor: gradient, borderWidth: 2, fill: true, tension: 0.4 }] },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { min: 6.0, max: 9.0 } } }
        });

        const riskCtx = document.getElementById('riskDoughnutChart');
        if(!riskCtx) return;
        riskChartInstance = new Chart(riskCtx.getContext('2d'), {
            type: 'doughnut',
            data: { labels: ['Low', 'Medium', 'High', 'Critical'], datasets: [{ data: [1, 1, 1, 1], backgroundColor: ['#17c964', '#f5a623', '#f21361', '#7928ca'], borderWidth: 0 }] },
            options: { responsive: true, maintainAspectRatio: false, cutout: '75%', plugins: { legend: { position: 'bottom' } } }
        });
    }

    // --- 3. FETCH LIVE DATA ---
    function fetchSafeJSON(url, options = {}) {
        return fetch(url, options)
            .then(res => {
                if (res.redirected || res.url.includes('login.html') || res.status === 401) { window.location.href = 'login.html'; throw new Error("Session Expired."); }
                if (!res.ok) throw new Error("Server Error: " + res.status);
                return res.text(); 
            })
            .then(text => {
                try { return JSON.parse(text); } 
                catch (e) { if (text.includes('<!DOCTYPE html>')) window.location.href = 'login.html'; throw e; }
            });
    }

    function fetchLiveStudentData(searchQuery = "") {
        fetchSafeJSON(`/SAS_Enterprise_Java/api/students?search=${encodeURIComponent(searchQuery)}`)
            .then(json => { globalData = json.data; renderTable(json.data); if(searchQuery === "") fetchAnalytics(); })
            .catch(err => console.error(err));
    }

    function fetchAnalytics() {
        fetchSafeJSON('/SAS_Enterprise_Java/api/analytics')
            .then(data => {
                document.getElementById('kpiTotal').innerText = data.totalStudents ? data.totalStudents.toLocaleString() : "0";
                document.getElementById('kpiAvgGpa').innerText = data.avgGpa ? data.avgGpa.toFixed(2) : "0.0";
                document.getElementById('kpiRisk').innerText = data.highRisk ? data.highRisk.toLocaleString() : "0";
                document.getElementById('kpiAtt').innerText = data.avgAtt ? data.avgAtt.toFixed(1) + "%" : "0%";
                if(riskChartInstance && data.risks) {
                    riskChartInstance.data.datasets[0].data = [data.risks.Low || 0, data.risks.Medium || 0, data.risks.High || 0, data.risks.Critical || 0];
                    riskChartInstance.update();
                }
            }).catch(err => console.error(err));
    }

    function renderTable(students) {
        const tbody = document.getElementById('rosterTableBody');
        if(!tbody) return;
        let html = '';
        if(!students || students.length === 0) { tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; padding: 30px;">No students found.</td></tr>`; return; }

        students.forEach(s => {
            let badgeClass = s.risk === 'Critical' ? 'badge-critical' : s.risk === 'High' ? 'badge-high' : 'badge-dept';
            let attColor = s.att.startsWith('5') || s.att.startsWith('6') ? 'var(--accent-red)' : 'var(--text-muted)';
            html += `
                <tr class="clickable-row" onclick="openProfile('${s.id}')">
                    <td><div class="stu-info"><span class="stu-name">${s.name}</span><span class="stu-id">${s.id}</span></div></td>
                    <td><span class="badge badge-dept">${s.dept}</span></td>
                    <td style="font-weight: 500; color: var(--accent-blue);">${s.gpa.toFixed(1)}</td>
                    <td style="color: ${attColor}">${s.att}</td>
                    <td><span class="badge ${badgeClass}">${s.risk} (${s.score})</span></td>
                    <td class="action-cell"><button onclick="event.stopPropagation(); deleteStudent('${s.id}')" class="btn-delete">Delete</button></td>
                </tr>`;
        });
        tbody.innerHTML = html;
    }

    // --- 4. EXPORTS & PDF GENERATION ---
    const btnExportMain = document.getElementById('btnExportMain');
    const btnExportFullDb = document.getElementById('btnExportFullDb');
    
    const exportCSV = function(e) {
        e.preventDefault();
        if(globalData.length === 0) { alert("No data to export."); return; }
        let csv = "ID,Name,Department,GPA,Attendance,Risk Tier\n";
        globalData.forEach(s => { csv += `"${s.id}","${s.name}","${s.dept}",${s.gpa},"${s.att}","${s.risk}"\n`; });
        let link = document.createElement("a"); link.download = "SAS_Master_Roster.csv"; 
        link.href = window.URL.createObjectURL(new Blob([csv], {type: "text/csv"})); link.click();
        logAudit(`<span style="color: var(--accent-blue);">CSV Downloaded</span>`);
    };
    if(btnExportMain) btnExportMain.addEventListener('click', exportCSV);
    if(btnExportFullDb) btnExportFullDb.addEventListener('click', exportCSV);

    const btnPdf = document.getElementById('btnPdfDefaulters');
    if(btnPdf) {
        btnPdf.addEventListener('click', (e) => {
            e.preventDefault();
            const { jsPDF } = window.jspdf;
            const doc = new jsPDF();
            
            doc.setFontSize(18);
            doc.text("University Attendance Defaulters Report (< 75%)", 14, 20);
            doc.setFontSize(11);
            doc.text(`Generated on: ${new Date().toLocaleDateString()}`, 14, 28);
            
            // Filter globalData for attendance < 75%
            const defaulters = globalData.filter(s => parseFloat(s.att.replace('%', '')) < 75);
            
            const tableData = defaulters.map(s => [s.id, s.name, s.dept, s.att, s.gpa]);
            
            doc.autoTable({
                startY: 35,
                head: [['Registration No', 'Student Name', 'Department', 'Attendance', 'GPA']],
                body: tableData,
                theme: 'grid',
                headStyles: { fillColor: [242, 19, 97] } // Red header
            });
            
            doc.save("Attendance_Defaulters.pdf");
            logAudit(`<span style="color: var(--accent-red);">PDF Generated</span>`);
        });
    }

    const btnUGC = document.getElementById('btnUGCExport');
    if(btnUGC) {
        btnUGC.addEventListener('click', (e) => {
            e.preventDefault();
            const payload = JSON.stringify({ institution: "SAS University", timestamp: new Date(), record_count: globalData.length, data: globalData }, null, 2);
            let link = document.createElement("a"); link.download = "UGC_Compliance.json"; 
            link.href = window.URL.createObjectURL(new Blob([payload], {type: "application/json"})); link.click();
            logAudit(`<span style="color: var(--accent-green);">JSON Exported</span>`);
        });
    }

    // --- 5. NAVIGATION & AUDIT LOGIC ---
    function switchView(activeLinkId, activeViewId, titleText = null) {
        const navLinks = ['navOverview', 'navDirectory', 'navReports', 'navFaculty', 'navAudit', 'navSettings'];
        navLinks.forEach(id => { const el = document.getElementById(id); if(el) el.classList.remove('active'); });
        const activeEl = document.getElementById(activeLinkId);
        if(activeEl) activeEl.classList.add('active');

        const views = ['mainDashboardView', 'reportsView', 'facultyView', 'auditView', 'settingsView'];
        views.forEach(id => { const el = document.getElementById(id); if(el) el.style.display = 'none'; });
        const targetEl = document.getElementById(activeViewId);
        if(targetEl) targetEl.style.display = 'block';

        if(activeViewId === 'mainDashboardView') {
            document.getElementById('mainTitle').innerText = titleText;
            if(activeLinkId === 'navOverview') {
                document.getElementById('kpiSection').style.display = 'grid';
                document.getElementById('chartSection').style.display = 'grid';
            } else {
                document.getElementById('kpiSection').style.display = 'none';
                document.getElementById('chartSection').style.display = 'none';
            }
        }
    }

    document.getElementById('navOverview').addEventListener('click', (e) => { e.preventDefault(); switchView('navOverview', 'mainDashboardView', 'Global Overview'); });
    document.getElementById('navDirectory').addEventListener('click', (e) => { e.preventDefault(); switchView('navDirectory', 'mainDashboardView', 'Student Directory'); });
    document.getElementById('navReports').addEventListener('click', (e) => { e.preventDefault(); switchView('navReports', 'reportsView'); });
    document.getElementById('navFaculty').addEventListener('click', (e) => { e.preventDefault(); switchView('navFaculty', 'facultyView'); });
    document.getElementById('navAudit').addEventListener('click', (e) => { e.preventDefault(); switchView('navAudit', 'auditView'); });
    document.getElementById('navSettings').addEventListener('click', (e) => { e.preventDefault(); switchView('navSettings', 'settingsView'); });

    function logAudit(actionHtml, user = "Admin User", ip = "192.168.1.1") {
        const tbody = document.getElementById('auditTableBody');
        if(!tbody) return;
        const now = new Date();
        const timeStr = now.getHours() + ':' + String(now.getMinutes()).padStart(2, '0') + ':' + String(now.getSeconds()).padStart(2, '0');
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${timeStr}</td><td>${actionHtml}</td><td>${user}</td><td>${ip}</td>`;
        tbody.insertBefore(tr, tbody.firstChild);
    }

    // --- 6. MODALS & CRUD ---
    const profileModal = document.getElementById('profileModal');
    document.getElementById('closeProfileModal').addEventListener('click', () => profileModal.classList.remove('active'));

    window.openProfile = function(studentId) {
        const student = globalData.find(s => s.id === studentId);
        if(!student) return;

        document.getElementById('profName').innerText = student.name;
        document.getElementById('profId').innerText = student.id;
        document.getElementById('profDept').innerText = student.dept;
        document.getElementById('profGpa').innerText = student.gpa.toFixed(1);
        document.getElementById('profAtt').innerText = student.att;
        
        let riskColor = student.risk === 'Critical' ? 'var(--accent-red)' : student.risk === 'High' ? 'var(--accent-orange)' : 'var(--accent-green)';
        document.getElementById('profRisk').innerHTML = `<span style="color: ${riskColor}">${student.risk} (${student.score})</span>`;
        document.getElementById('profAvatar').innerText = student.name.split(' ').map(n=>n[0]).join('').substring(0,2).toUpperCase();

        let baseAtt = parseFloat(student.att.replace('%', ''));
        let monthData = [baseAtt+4, baseAtt-2, baseAtt+1, baseAtt-5, baseAtt+2].map(v => Math.min(100, Math.max(0, v)));

        const ctx = document.getElementById('studentDetailChart');
        if(ctx) {
            if(detailChartInstance) detailChartInstance.destroy();
            detailChartInstance = new Chart(ctx.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: ['Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                    datasets: [
                        { label: 'Attendance %', data: monthData, backgroundColor: 'rgba(121, 40, 202, 0.8)', borderRadius: 4 },
                        { type: 'line', label: 'Required (75%)', data: [75,75,75,75,75], borderColor: '#f21361', borderWidth: 2, borderDash: [5,5], pointRadius: 0, fill: false }
                    ]
                },
                options: { responsive: true, maintainAspectRatio: false, scales: { y: { min: 0, max: 100 } } }
            });
        }
        profileModal.classList.add('active');
        logAudit(`<span style="color: var(--accent-purple);">Viewed Profile</span> (${student.id})`);
    };

    const addModal = document.getElementById('addModal');
    document.getElementById('btnOpenAdd').addEventListener('click', () => addModal.classList.add('active'));
    document.getElementById('closeAddModal').addEventListener('click', () => addModal.classList.remove('active'));

    document.getElementById('addStudentForm').addEventListener('submit', function(e) {
        e.preventDefault(); 
        let payload = { name: document.getElementById('stuName').value, id: document.getElementById('stuReg').value, dept: document.getElementById('stuDept').value, gpa: parseFloat(document.getElementById('stuGpa').value) };
        fetch('/SAS_Enterprise_Java/api/students', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
        .then(res => { 
            if (res.ok) { location.reload(); } 
            else if (res.status === 401) { window.location.href = 'login.html'; }
            else { alert("Ensure Department code is 'CSE', 'MECH', or 'ECE'."); }
        });
    });

    window.deleteStudent = function(regNo) {
        if (confirm(`Delete student ${regNo}?`)) {
            fetch(`/SAS_Enterprise_Java/api/students?regNo=${regNo}`, { method: 'DELETE' })
            .then(res => { 
                if (res.ok) { logAudit(`<span style="color: var(--accent-red);">Student Deleted</span> (${regNo})`); fetchLiveStudentData(); } 
                else if (res.status === 401) window.location.href = 'login.html'; 
            });
        }
    };

    let searchTimeout;
    const searchInput = document.getElementById('searchInput');
    if(searchInput) {
        searchInput.addEventListener('input', (e) => { clearTimeout(searchTimeout); searchTimeout = setTimeout(() => { fetchLiveStudentData(e.target.value); }, 300); });
    }

    // Boot Sequence
    generateFaculty();
    initDashboardCharts();
    fetchLiveStudentData();
});