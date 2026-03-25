# 📱 ANDROID APP TESTING GUIDE

## ✅ What's Been Done

1. ✅ **React web app removed** - Only Android app now
2. ✅ **Premium dark UI created** - Gold accent salon theme
3. ✅ **OTP now shows in console** - Easy testing without real SMS
4. ✅ **Demo data ready** - Users, Services, Artists pre-loaded

---

## 🚀 HOW TO RUN THE APP

### STEP 1: Start the Backend

Open **Command Prompt** or **Terminal** and run:

```bash
cd C:\salon_management_system
mvn spring-boot:run
```

Wait for:
```
✅ DEMO DATA INITIALIZATION COMPLETE!
Started SalonManagementSystemApplication in X seconds
```

Backend runs on: **http://localhost:8080**

---

### STEP 2: Open Android App in Android Studio

1. Open **Android Studio**
2. Click **File → Open**
3. Navigate to: `C:\salon_management_system\salon-app`
4. Click **OK** and wait for Gradle sync

---

### STEP 3: Run on Emulator

1. Create an emulator: **Tools → Device Manager → Create Device**
   - Select: Pixel 6 or any phone
   - System Image: API 33 or higher
2. Click the **Green Play button** to run

---

## 📱 TESTING THE APP

### Test Login:

1. App opens → You see beautiful Login screen
2. Enter: **customer@salon.com**
3. Enter Phone: **+919876543210** (or any number)
4. Click **SEND OTP**
5. **IMPORTANT**: Look at backend console for OTP!

```
╔════════════════════════════════════════╗
║     📱 OTP GENERATED (DEMO MODE)      ║
╠════════════════════════════════════════╣
║  Channel: PHONE
║  Target : +919876543210
║  OTP    : 123456   ← USE THIS!
║  Expires: 5 minutes
╚════════════════════════════════════════╝
```

6. Enter the OTP from console
7. Enter Password: **customer123**
8. Click **SIGN IN**

---

## 👤 DEMO ACCOUNTS

| Role     | Email               | Password    |
|----------|---------------------|-------------|
| Customer | customer@salon.com  | customer123 |
| Admin    | admin@salon.com     | admin123    |

---

## 🎨 NEW UI FEATURES

### Login Screen:
- Dark gradient background
- Gold accent colors (#FFD700)
- Premium card styling
- Demo accounts info card

### Home Screen:
- Personalized greeting
- Search bar
- Category chips
- Service cards with gold accents
- Bottom navigation

### Service Cards:
- Dark card background
- Gold price highlight
- Duration with icon
- Book Now button

---

## 🔧 TROUBLESHOOTING

### "Connection error" on login:
- Make sure backend is running
- Check if emulator has internet access
- Backend should be on http://10.0.2.2:8080 for emulator

### OTP not showing:
- Check backend console (terminal where you ran mvn spring-boot:run)
- OTP is printed in a box format

### App crashes on startup:
- Sync Gradle: File → Sync Project with Gradle Files
- Clean and rebuild: Build → Clean Project, then Build → Rebuild

---

## 🎯 FEATURES TO TEST

1. **Login/Register** - With OTP verification
2. **Browse Services** - Scroll through service cards
3. **View Artists** - See artist profiles
4. **Book Appointment** - Select service → artist → date → time
5. **My Appointments** - View upcoming bookings
6. **Profile** - View and edit profile
7. **Admin Dashboard** - Login with admin account

---

## 💡 TIPS

1. **Always check backend console for OTP** - It won't come to your phone
2. **Use 10.0.2.2 instead of localhost** - Android emulator uses this
3. **Keep backend terminal visible** - So you can see OTP
4. **Demo data resets on restart** - All test data is recreated fresh

---

Happy Testing! 💇✨
