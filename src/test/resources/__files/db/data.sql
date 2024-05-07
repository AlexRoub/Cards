INSERT INTO users (id, email, role, password) VALUES
    (3, 'a@b.com', 'MEMBER', 'password1'),
    (4, 'b@c.com', 'ADMIN', 'password2'),
    (15, 'abcd@gmail.com', 'ADMIN', '$2a$10$g.nhdwSIcg/.XJRQaloqGurWJs8zYuCPCOVGzIdAQMDSsl66w8i32');

INSERT INTO cards (id, name, description, color, status, creation_date, user_id) VALUES
    (3, 'Card 3', 'Description 3', '#ff0000', 'TO_DO', '2024-05-04', 3),
    (4, 'Card 4', 'Description 4', '#00ff00', 'IN_PROGRESS', '2024-05-05', 4);

INSERT INTO token (id, token, token_type, revoked, expired, user_id) VALUES
    (3, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9.3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8', 'BEARER', FALSE, FALSE, 3),
    (4, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiQGMuY29tIiwiaWF0IjoxNzE1MDg2NTQ0LCJleHAiOjE3MTUxNzI5NDR9.xwOIRvsC48oePk41T5JIxZR1z-eWhMG1stVEsO1ElTg', 'BEARER',  FALSE, FALSE, 4);