<!-- file: README.md -->
# Бэйэ Дьоно — Android-приложение кафе

## Сборка на телефоне (AndroidIDE)
1. Открыть AndroidIDE → Open Project → `/storage/emulated/0/AndroidIDEProjects/beyedono/`.
2. Убедиться: JDK 17 выбран в Preferences → Build & Run → JDK.
3. Положить `app/google-services.json` (Firebase Console → Project settings → Android app `ru.beyedono.app`).
4. Положить `keystore.properties` в корень проекта (по образцу `keystore.properties.example`).
5. Вставить Yandex MapKit API key в `local.properties`:
   `YANDEX_MAPKIT_API_KEY=ваш_ключ`
   и в `app/build.gradle.kts` добавить:
   `manifestPlaceholders["YANDEX_MAPKIT_API_KEY"] = localProperties.getProperty("YANDEX_MAPKIT_API_KEY") ?: ""`
6. Терминал AndroidIDE:
./gradlew assembleDebug
7. Установка APK: кнопка Run / Install в AndroidIDE.
8. Logcat: View → Logcat.

## Сборка release через GitHub Actions
Push тега `v*` → workflow `android-release.yml` собирает APK и создаёт Release.

## Backend dev (Termux)
pkg install proot-distro
proot-distro install ubuntu
proot-distro login ubuntu
apt update && apt install python3 python3-venv python3-pip
cd /root/beyedono/backend
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000

## Backend prod (VPS по SSH)
ssh root@<vps-ip>
cd /opt/beyedono/backend
docker compose up -d

## Контакты
Телефон кафе: 8 914 102 29 08.