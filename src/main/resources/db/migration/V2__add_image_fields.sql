-- Add image_url column to artists table
ALTER TABLE artists ADD COLUMN image_url VARCHAR(500);

-- Add image_url column to services table
ALTER TABLE services ADD COLUMN image_url VARCHAR(500);

-- Add FCM token column to users table for push notifications
ALTER TABLE users ADD COLUMN fcm_token VARCHAR(255);

-- Add profile_image_url column to users table
ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(500);
