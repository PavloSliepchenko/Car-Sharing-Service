INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount, is_deleted)
VALUES (1, 'PAID', 'PAYMENT', 1, 'https://checkout.stripe.com/c/pay/cs_test_a1X7UkxhQpnKNdVWInF1',
        'cs_test_a1X7UkxhQpnKNdVWInF1TN5GWV8NdbXj4q', 12, FALSE),
       (2, 'PENDING', 'PAYMENT', 2, 'https://checkout.stripe.com/c/pay/cs_test_a1Z4B7c1',
        'cs_test_a1Z4B7c1JUmnJOGo285k3MO6', 23, FALSE),
       (3, 'PENDING', 'FINE', 2, 'https://checkout.stripe.com/c/pay/cs_test_a14teXDuA2LtQ7T3NiAIXw1',
        'cs_test_a14teXDuA2LtQ7T3NiAIXw1si', 4, FALSE),
       (4, 'PAID', 'PAYMENT', 3, 'https://checkout.stripe.com/c/pay/cs_test_a1X7Ua1Z4B7c1',
        'cs_test_a1Z4B7cDuA2LtQ7T3', 43, FALSE),
       (5, 'PENDING', 'FINE', 1, 'https://checkout.stripe.com/c/pay/cs_test_a14teX7UkxhQpnuA2',
        'cs_test_a1Z4B7JUmnJOGo2', 31, FALSE),
       (6, 'PAID', 'PAYMENT', 4, 'https://checkout.stripe.com/c/pay/cs_test_aa1X7UkxhQpnKN4teX7UkxhQpnu',
        'cs_test_a1OGo285k3MTN5GW', 65, FALSE);
