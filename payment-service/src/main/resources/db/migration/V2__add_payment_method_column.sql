ALTER TABLE payments
ADD COLUMN payment_method VARCHAR(50) AFTER amount,
ADD COLUMN payment_gateway_response TEXT AFTER transaction_reference;