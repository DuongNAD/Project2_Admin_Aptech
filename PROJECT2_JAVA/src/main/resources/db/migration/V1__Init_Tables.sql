-- Insert default Admin (password is 'admin123' bcrypt hashed)
INSERT INTO admins (full_name, email, password_hash, role)
VALUES ('Super Admin', 'admin@elearning.com', '$2a$10$EIFzOM8E0S0QkR1S2z1l1eB1yOOT005P7.i8E0bC.00A.7E0T/', 'super_admin');