INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10'), 'test0', 'ABCDEFtest', 0, 30, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('00000000-0000-0000-0000-000000000000'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), 'test1', 'ABCDEF1', 100002, 30, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('11111111-1111-1111-1111-111111111111'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'), 'test2', 'ABCDEF2', 2033, 20, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('22222222-2222-2222-2222-222222222222'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'), 'test3', 'ABCDEF3', 1500, 40, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('33333333-3333-3333-3333-333333333333'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'), 'test4', 'ABCDEF4', 1888, 10, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('44444444-4444-4444-4444-444444444444'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'), 'test5', 'ABCDEF5', 1200, 30, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('55555555-5555-5555-5555-555555555555'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16'), 'test6', 'ABCDEF6', 1999, 20, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('66666666-6666-6666-6666-666666666666'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17'), 'test7', 'ABCDEF7', 3000, 10, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('77777777-7777-7777-7777-777777777777'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18'), 'test8', 'ABCDEF8', 2500, 40, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('88888888-8888-8888-8888-888888888888'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19'), 'test9', 'ABCDEF9', 1050, 30, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('99999999-9999-9999-9999-999999999999'));

INSERT INTO p_coupon (id, name, code, quantity, discount, is_active, expired_at, created_at, updated_at, created_by)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20'), 'test10', 'ABCDEF0', 1700, 20, true, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), UUID_TO_BIN('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'));
