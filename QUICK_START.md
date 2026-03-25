# Quick Start Guide

## Start Backend (Spring Boot)

```bash
# Navigate to project
cd /c/salon_management_system

# Start backend
mvn spring-boot:run
```

Backend will run on: **http://localhost:8080**

Swagger API Docs: **http://localhost:8080/swagger-ui.html**

---

## Start Frontend (React)

```bash
# Open new terminal
cd /c/salon_management_system/salon-react-app

# Start React app
npm run dev
```

Frontend will run on: **http://localhost:5173**

---

## First Time Setup

### 1. Database
Ensure MySQL is running with database `salon_db`

### 2. Test the App

1. Go to http://localhost:5173
2. Click "Sign up"
3. Create an account
4. Login and explore the home page!

---

## Available Features

### Home Page (✅ Working)
- Personalized greeting
- Search services
- Browse categories
- View special offers
- See top artists
- Quick actions

### Backend APIs (✅ All Working)
- Authentication (Login, Register, OTP)
- Services (Browse, Filter)
- Artists (List, Details, Slots)
- Appointments (Book, Cancel, Reschedule)
- Payments (Razorpay integration)
- Reviews & Ratings
- Admin Dashboard
- File Uploads

---

## Tech Stack

- **Backend**: Spring Boot 3.2.5 + Java 17
- **Frontend**: React 18 + Vite
- **Database**: MySQL 8.0
- **Authentication**: JWT
- **Payments**: Razorpay
- **Notifications**: Email (SMTP), SMS (Twilio), Push (FCM)

---

## Design

- Dark theme (#0D0D0D)
- Yellow accent (#FFD700)
- Premium modern UI
- Responsive mobile-first design

---

## Need Help?

Check **PROJECT_STATUS.md** for complete documentation.
