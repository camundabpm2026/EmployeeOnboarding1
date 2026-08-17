const containerEl = document.getElementById("applications-container");

async function loadApplications() {
    try {
        const [jobsRes, appsRes] = await Promise.all([
            fetch("/api/jobs"),
            fetch("/api/applications")
        ]);
        if (!jobsRes.ok || !appsRes.ok) throw new Error("Failed to load data");

        const jobs = await jobsRes.json();
        const applications = await appsRes.json();
        const jobTitleById = Object.fromEntries(jobs.map(j => [j.id, j.title]));

        renderApplications(applications, jobTitleById);
    } catch (err) {
        containerEl.innerHTML = `<p class="empty-state">Could not load applications. Is the server running?</p>`;
    }
}

function renderApplications(applications, jobTitleById) {
    if (applications.length === 0) {
        containerEl.innerHTML = `<p class="empty-state">No applications submitted yet.</p>`;
        return;
    }

    const rows = applications
        .slice()
        .reverse()
        .map(app => `
            <tr>
                <td>${app.id}</td>
                <td>${escapeHtml(jobTitleById[app.jobId] || `Job #${app.jobId}`)}</td>
                <td>${escapeHtml(app.name)}</td>
                <td>${escapeHtml(app.email)}</td>
                <td>${escapeHtml(app.phone)}</td>
                <td><span class="badge">${escapeHtml(app.status)}</span></td>
                <td>${formatDate(app.submittedAt)}</td>
            </tr>
        `).join("");

    containerEl.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Position</th>
                    <th>Candidate</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Status</th>
                    <th>Submitted</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>
    `;
}

function formatDate(isoString) {
    try {
        return new Date(isoString).toLocaleString();
    } catch (err) {
        return isoString;
    }
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}

loadApplications();
