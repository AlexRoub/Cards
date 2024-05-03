INSERT INTO users (email, password, role) VALUES
('user1@example.com', 'password', 'MEMBER'),
('user2@example.com', 'password', 'MEMBER'),
('admin@example.com', 'adminpassword', 'ADMIN');

INSERT INTO cards (id, name, description, color, status, user_id) VALUES
(1, 'Task 1', 'Description of Task 1', '#ff0000', 'To Do', 1),
(2, 'Task 2', 'Description of Task 2', '#00ff00', 'In Progress', 1),
(3, 'Task 3', 'Description of Task 3', '#0000ff', 'Done', 2);