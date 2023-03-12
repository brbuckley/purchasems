INSERT INTO purchase (purchase_id, order_id, supplier_id, supplier_name, status, datetime, updated, supplier_order_id)
VALUES ('PUR0000001', 'ORD0000001', 'SUP0000001', 'Supplier A', 0, '19801225 13:00:00+00', null, null);

INSERT INTO order_line (purchase_id, quantity, product_id, name, price, category)
VALUES ((SELECT id from purchase where purchase_id = 'PUR0000001'), 2, 'PRD0000001', 'Heineken', 4.99, 0);