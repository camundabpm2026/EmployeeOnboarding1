const jobsListEl = document.getElementById("jobs-list");

async function loadJobs() {
    try {
        const response = await fetch("/api/jobs");
        if (!response.ok) throw new Error("Failed to load jobs");
        const jobs = await response.json();
        renderJobs(jobs);
    } catch (err) {
        jobsListEl.innerHTML = `<p class="empty-state">Could not load open positions. Is the server running?</p>`;
    }
}

function renderJobs(jobs) {
    if (jobs.length === 0) {
        jobsListEl.innerHTML = `<p class="empty-state">No open positions right now.</p>`;
        return;
    }

    jobsListEl.innerHTML = jobs.map(job => `
        <div class="job-card" data-job-id="${job.id}">
            <h2>${escapeHtml(job.title)}</h2>
            <div class="job-meta">${escapeHtml(job.department)} &middot; ${escapeHtml(job.location)}</div>
            <div class="job-description">${escapeHtml(job.description)}</div>
            <button type="button" class="apply-toggle">Apply</button>

            <form class="apply-form">
                <div>
                    <label>Full name</label>
                    <input type="text" name="name" required>
                </div>
                <div>
                    <label>Email</label>
                    <input type="email" name="email" required>
                </div>
                <div>
                    <label>Phone</label>
                    <input type="tel" name="phone">
                </div>
                <div>
                    <label>Cover letter</label>
                    <textarea name="coverLetter" placeholder="Why are you a good fit for this role?"></textarea>
                </div>
                <div class="form-actions">
                    <button type="submit">Submit application</button>
                    <button type="button" class="secondary cancel">Cancel</button>
                </div>
                <div class="status-message"></div>
            </form>
        </div>
    `).join("");

    document.querySelectorAll(".job-card").forEach(card => {
        const toggleBtn = card.querySelector(".apply-toggle");
        const cancelBtn = card.querySelector(".cancel");
        const form = card.querySelector(".apply-form");

        toggleBtn.addEventListener("click", () => form.classList.add("open"));
        cancelBtn.addEventListener("click", () => form.classList.remove("open"));

        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const jobId = card.getAttribute("data-job-id");
            const statusEl = form.querySelector(".status-message");
            const payload = {
                jobId,
                name: form.name.value,
                email: form.email.value,
                phone: form.phone.value,
                coverLetter: form.coverLetter.value
            };

            try {
                const response = await fetch("/api/applications", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    const err = await response.json().catch(() => ({}));
                    throw new Error(err.error || "Submission failed");
                }

                statusEl.textContent = "Application submitted successfully!";
                statusEl.className = "status-message success";
                form.reset();
            } catch (err) {
                statusEl.textContent = err.message;
                statusEl.className = "status-message error";
            }
        });
    });
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}

loadJobs();
