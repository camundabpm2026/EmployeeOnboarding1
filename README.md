# Employee Hiring Process Workflow

A minimal candidate-application-intake workflow: job listings, a public
application form, and an HR view of submitted applications.

- **Backend:** plain Java (JDK's built-in `com.sun.net.httpserver`), no
  frameworks or build tool — just `javac`/`java`.
- **Frontend:** static HTML/CSS/JS served by the same server, calling a
  JSON REST API.
- **Storage:** in-memory only (data resets when the server restarts).

## Project layout

```
src/main/java/com/onboarding/
  Server.java              entrypoint, wires up the HTTP server
  model/                   Job, Application
  store/                   in-memory JobStore, ApplicationStore
  handler/                 JobsHandler, ApplicationsHandler, StaticFileHandler
  util/                    JsonUtil (parse/escape), HttpUtil (request/response helpers)
web/
  index.html               candidate-facing job listing + apply form
  applications.html        HR view of submitted applications
  css/style.css
  js/app.js, js/applications.js
```

## API

| Method | Path                        | Description                          |
|--------|-----------------------------|---------------------------------------|
| GET    | `/api/jobs`                 | List open jobs                        |
| GET    | `/api/jobs/{id}`             | Get a single job                      |
| GET    | `/api/applications`          | List all applications (optional `?jobId=`) |
| GET    | `/api/applications/{id}`     | Get a single application              |
| POST   | `/api/applications`          | Submit an application (`jobId`, `name`, `email`, `phone`, `coverLetter`) |

## Build & run

No build tool required — compile with `javac` and run with `java`.

**PowerShell / cmd (Windows):**

```powershell
# from the project root
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp out com.onboarding.Server
```

> If `java`/`javac` on your `PATH` resolve to different JDK versions
> (e.g. an old `javapath` shim vs. a newer `JAVA_HOME`), use
> `"$JAVA_HOME/bin/java"` / `"$JAVA_HOME/bin/javac"` explicitly so
> compile and run use the same version.

**bash:**

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.onboarding.Server
```

Then open http://localhost:8081 in a browser:

- `/` — candidate view: browse open jobs, apply
- `/applications.html` — HR view: see submitted applications

The server must be run from the project root so it can find the `web/`
directory for static files.

## Notes / next steps

This covers job listing + candidate application intake only. Natural
follow-ups if you want to extend the workflow: interview stage tracking,
application status transitions, offer generation, and persistent storage
(e.g. swap the in-memory stores for a real database).
