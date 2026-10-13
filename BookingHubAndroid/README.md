# BookingHub — Android app (Kotlin + Jetpack Compose)

This app is the native client for your existing Django **BookingHub** website. It talks to
the new `/api/` JSON endpoints added to that project (see the other zip,
`BookingWebApplication_with_API.zip`) — it never touches the database directly.

```
Android app  --(HTTPS/JSON + token)-->  Django REST API (/api/...)  -->  your DB (SQLite/Postgres)
```

## 1. Start the backend first

Unzip `BookingWebApplication_with_API.zip`, then from `BOOKINGS/bookinghub/`:

```bash
pip install django djangorestframework qrcode reportlab requests pytz
python manage.py migrate
python manage.py seed_data          # optional: demo providers/services/slots
python manage.py runserver 0.0.0.0:8000
```

Confirm it's up: open `http://127.0.0.1:8000/api/categories/` in a browser — you should see
a small JSON array.

## 2. Open this folder in Android Studio

File → Open → select this `BookingHubAndroid` folder → let Gradle sync (first sync
downloads Gradle 8.7 + dependencies, needs internet).

If Android Studio asks about the Gradle wrapper jar, let it regenerate one — the wrapper
jar binary isn't included in this download; `gradle/wrapper/gradle-wrapper.properties`
already points at Gradle 8.7, so Android Studio's own bundled Gradle can bootstrap it.

## 3. Point the app at your backend

`app/build.gradle.kts` → `BASE_URL`:

| Where you're running the backend | BASE_URL to use |
|---|---|
| Android **emulator**, `runserver` on the same machine | `http://10.0.2.2:8000/api/` (already set — 10.0.2.2 is the emulator's alias for your machine's localhost) |
| A **physical phone** on the same Wi-Fi | `http://<your-computer's-LAN-IP>:8000/api/` — and add that IP to `network_security_config.xml` |
| A **deployed** backend (Railway/Render/your domain) | `https://yourdomain.com/api/` — and you can delete `network_security_config.xml` + its manifest reference once it's HTTPS |

Then Run ▶ on an emulator or device.

## 4. What's wired up

- **Login / Register** → `POST /api/auth/login/`, `/api/auth/register/` — token stored
  on-device with DataStore and sent as `Authorization: Token <token>` on every request after.
- **Home** → categories + top-rated providers.
- **Browse by category** → search, backed by `GET /api/providers/?category=...&q=...`.
- **Provider detail** → bio, services, open time slots, reviews, leave a review.
- **Book** → pick a service (+ optional time slot), address/notes, then **Paystack checkout**
  opens in an in-app WebView; the app verifies the payment server-side
  (`/api/bookings/{id}/verify/`) the moment Paystack redirects, then shows a confirmation.
- **My Bookings** → status, price, date, address for every booking you've made.
- **Become a Provider** → any logged-in customer who isn't already a provider sees a "Run a business?" card on Home. Filling in the form (`POST /api/provider/apply/`) creates their `Provider` row with `is_active=False` — exactly the same flag every customer-facing listing already filters on — so it's invisible to other customers until you flip `is_active` to `True` for them in `/admin/`. They get immediate access to their Provider Portal (dashboard, availability, profile) while waiting, so they can set everything up in advance.
- **Provider Portal** → shown on Home automatically for any logged-in user who owns a Provider profile (i.e. `provider.owner` points at them). Mirrors the website's `/provider/portal/` pages one-for-one:
  - **Dashboard** — booking counts, total earnings, and an available/busy toggle
  - **Bookings** — filter by status, confirm/reject pending bookings, mark confirmed ones completed (same emails to the customer as the website sends)
  - **Availability** — pick a date, tick hours, add slots; remove any slot that isn't already booked
  - **Business profile** — edit name, bio, location, phone (photo upload isn't wired up from the app yet — use the website for that)
  - **Earnings** — all-time and this-month totals, plus every paid booking

  To test this: log in as a user that already owns a Provider in your seed data (check `Provider.objects.filter(owner__isnull=False)` in the Django shell), or set one up via `/admin/`.

## 5. Things to change before this goes to real users

- **Secrets**: your `settings.py` has the Paystack secret key and an Anthropic API key
  hardcoded in plain text. Move both to environment variables
  (`os.environ["PAYSTACK_SECRET_KEY"]`) before deploying anywhere public — anyone with the
  source currently has them.
- **HTTPS**: get the backend behind a real domain + TLS, switch `BASE_URL` to `https://`,
  and delete the cleartext network security config — Play Store review also expects this.
- **Database**: SQLite is fine for development; switch to PostgreSQL for production per
  your own `readme.txt`.
- **App icon**: I generated a simple placeholder vector icon
  (`res/drawable/ic_launcher_*.xml`) so the project builds cleanly out of the box — swap in
  real branded icons via Android Studio's Image Asset tool when you're ready
  (right-click `res` → New → Image Asset).
- **Password reset / email verification**: not built yet — the web app doesn't have this
  either, so there was nothing to mirror. Same goes for the provider portal (dashboard,
  availability, earnings) — this app is the **customer-facing** side only, matching the
  public pages of the site.

## 6. If something doesn't build

This was written and the API side was tested end-to-end, but the Kotlin/Compose code
itself couldn't be compiled in the environment I built it in (no Android SDK available
there) — Android Studio's own compiler is the first real build. If you hit a red
underline or a Gradle error, paste it back to me and I'll fix it directly.
