INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted, is_active)
VALUES (1, '2024-01-12', '2024-01-15', '2024-01-20', 2, 2, FALSE, FALSE),
       (2, '2024-01-13', '2024-01-14', '2024-01-16', 1, 3, FALSE, FALSE),
       (3, '2024-01-13', '2024-01-15', '2024-01-14', 3, 4, FALSE, FALSE),
       (4, '2024-01-13', '2024-01-16', '2024-01-16', 5, 5, FALSE, FALSE),
       (5, '2024-01-13', '2024-01-21', NULL, 1, 4, FALSE, TRUE),
       (6, '2024-01-15', '2024-02-01', NULL, 3, 5, FALSE, TRUE);
