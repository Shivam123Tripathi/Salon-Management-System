# 🧪 COMPLETE TESTING CHECKLIST

## ✅ Your Apps Are Running!

- **Frontend**: http://localhost:5173 (React Web App)
- **Backend**: http://localhost:8080 (Spring Boot API)
- **API Docs**: http://localhost:8080/swagger-ui.html

---

## 📝 TEST ACCOUNTS

### Customer Account:
```
Email: customer@salon.com
Password: customer123
```

### Admin Account:
```
Email: admin@salon.com
Password: admin123
```

---

## 🧪 STEP-BY-STEP TESTING GUIDE

### TEST 1: Customer Flow ✅

1. **Open**: http://localhost:5173
2. **Login** with `customer@salon.com` / `customer123`
3. **Home Page** should show:
   - Welcome message with your name
   - Search bar
   - Category chips (Hair, Skin, etc.)
   - Service cards with prices
   - Artist profiles with ratings
   - Bottom navigation

4. **Test Search**:
   - Type "Haircut" in search bar
   - Should filter services in real-time

5. **Test Categories**:
   - Click "Hair" category chip
   - Should show only Hair services

---

### TEST 2: Backend APIs ✅

Open Swagger UI: http://localhost:8080/swagger-ui.html

**Test these endpoints:**

1. **Auth - Login**:
   - POST `/api/auth/login`
   - Body:
   ```json
   {
     "email": "customer@salon.com",
     "password": "customer123"
   }
   ```
   - Should return JWT token

2. **Get All Services**:
   - GET `/api/services`
   - Should return 4 services

3. **Get All Artists**:
   - GET `/api/artists`
   - Should return 2 artists (Sam & Lisa)

4. **Get Available Slots**:
   - GET `/api/artists/1/slots?date=2026-03-26&serviceId=1`
   - Should return available time slots

---

### TEST 3: Booking Flow (Manual Test) ✅

**Using Swagger or Postman:**

1. **Login** to get token:
   ```
   POST /api/auth/login
   ```

2. **Book Appointment**:
   ```
   POST /api/appointments
   Authorization: Bearer <your_token>

   Body:
   {
     "serviceId": 1,
     "artistId": 1,
     "appointmentDate": "2026-03-26",
     "startTime": "10:00:00",
     "notes": "First booking test",
     "paymentMethod": "PAY_AT_SALON"
   }
   ```

3. **Get My Appointments**:
   ```
   GET /api/appointments
   Authorization: Bearer <your_token>
   ```

4. **Cancel Appointment**:
   ```
   PUT /api/appointments/1/cancel
   Authorization: Bearer <your_token>
   ```

---

### TEST 4: Admin Features ✅

1. **Login as Admin**:
   - Email: `admin@salon.com`
   - Password: `admin123`

2. **Using Swagger:**

   **Get Dashboard Stats**:
   ```
   GET /api/admin/dashboard
   Authorization: Bearer <admin_token>
   ```

   **Add New Service**:
   ```
   POST /api/admin/services
   Authorization: Bearer <admin_token>

   Body:
   {
     "name": "Hot Towel Shave",
     "description": "Luxury shaving experience",
     "durationMinutes": 45,
     "price": 30,
     "category": "Barber"
   }
   ```

   **Add Artist Leave**:
   ```
   POST /api/admin/artists/1/leave
   Authorization: Bearer <admin_token>

   Body:
   {
     "leaveDate": "2026-03-28",
     "reason": "Personal"
   }
   ```

---

### TEST 5: Double Booking Prevention ✅

**Try to book same slot twice:**

1. Book slot: `artistId=1, date=2026-03-26, startTime=10:00`
2. Try booking again: Same artist, date, and time
3. **Expected**: Error message "Slot already booked"

---

### TEST 6: Slot Generation ✅

**Test slot availability considers:**

1. **Artist working hours**: 9 AM - 6 PM
2. **Artist breaks**: 1 PM - 2 PM (no slots)
3. **Service duration**: 30 mins (slots: 10:00, 10:30, 11:00...)
4. **Past times**: Can't book past slots today
5. **Artist leaves**: No slots on leave days

**Test**: GET `/api/artists/1/slots?date=today&serviceId=1`

---

## 🎯 EXPECTED RESULTS

### ✅ Should Work:
- Login with demo accounts
- Browse services
- View artists
- Search and filter
- Get available slots
- Book appointments (future dates)
- Cancel appointments (before policy deadline)
- Admin dashboard
- Add/edit services
- Manage artists

### ❌ Should Fail (Expected):
- Booking past dates/times
- Double booking same slot
- Booking during artist break
- Booking on artist's leave day
- Cancelling < 2 hours before appointment

---

## 📱 Test on Mobile

The React app is responsive!

1. Open browser DevTools (F12)
2. Toggle device toolbar (Ctrl+Shift+M)
3. Select "iPhone 12 Pro" or "Pixel 5"
4. Test the mobile UI!

---

## 🚀 Performance Check

1. **Backend logs**: Check `backend.log` for any errors
2. **Frontend console**: Open browser console (F12) - should be no errors
3. **Network tab**: Check API calls are successful (200 OK)

---

## 📊 Database Check

```bash
# Connect to MySQL and verify data:
mysql -u root -p salon_db

# Check tables:
SHOW TABLES;

# Check users:
SELECT * FROM users;

# Check services:
SELECT * FROM services;

# Check artists:
SELECT * FROM artists;
```

---

## ✅ TESTING COMPLETE!

If all tests pass, your app is **fully functional**! 🎉

### Demo Data Includes:
- ✅ 1 Admin user
- ✅ 1 Customer user
- ✅ 2 Artists
- ✅ 4 Services
- ✅ Salon configuration

### Features Working:
- ✅ Authentication (JWT)
- ✅ Service browsing
- ✅ Artist listing
- ✅ Slot generation
- ✅ Appointment booking
- ✅ Double-booking prevention
- ✅ Admin dashboard
- ✅ Premium dark UI

---

## 🐛 If Something Doesn't Work

1. **Backend not starting?**
   - Check MySQL is running
   - Check `backend.log` for errors
   - Verify database credentials in `application.properties`

2. **Frontend not connecting?**
   - Check backend is running on port 8080
   - Check frontend.log for  errors
   - Open browser console (F12) for errors

3. **Login not working?**
   - Verify you're using correct credentials
   - Check backend logs for authentication errors
   - Try clearing browser localStorage

---

## 📞 Need Help?

Check these files:
- `PROJECT_STATUS.md` - Full documentation
- `QUICK_START.md` - Quick start guide
- Backend logs: `backend.log`
- Frontend logs: `frontend.log`
