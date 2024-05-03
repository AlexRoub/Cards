INSERT INTO users (email, password, role) VALUES
('user1@example.com', 'password1', 'MEMBER'),
('user2@example.com', 'password2', 'MEMBER'),
('admin@example.com', 'adminpassword', 'ADMIN');

--TODO add creation date
INSERT INTO cards (name, description, color, status, user_id) VALUES
('Task 1', 'Description of Task 1', '#ff0000', 'To Do', 1),
('Task 2', 'Description of Task 2', '#00ff00', 'In Progress', 1),
('Task 3', 'Description of Task 3', '#0000ff', 'Done', 2);