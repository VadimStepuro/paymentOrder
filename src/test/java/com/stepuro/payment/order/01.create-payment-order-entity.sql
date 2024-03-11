CREATE TABLE public.payment_order_entity (
	id uuid DEFAULT gen_random_uuid(),
	source_account_number varchar NULL,
	destination_account_number varchar NULL,
	source_card_number varchar NULL,
	destination_card_number varchar NULL,
	created_date timestamp NOT NULL,
	updated_date timestamp NULL,
	status varchar NOT NULL,
	payment_type varchar NOT NULL,
	amount numeric(20,2) NOT NULL,
	user_id INTEGER NOT NULL,

	CONSTRAINT account_pk PRIMARY KEY (id),
	CONSTRAINT source_card_number_check CHECK (source_card_number = NULL OR source_card_number ~* '^(?:4[0-9]{12}(?:[0-9]{3})? | (?:5[1-5][0-9]{2} | 222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12} | 3[47][0-9]{13} | 3(?:0[0-5]|[68][0-9])[0-9]{11} | 6(?:011|5[0-9]{2})[0-9]{12} |  (?:2131|1800|35\d{3})\d{11})$'),
	CONSTRAINT destination_card_number_check CHECK (destination_card_number = NULL OR destination_card_number ~* '^(?:4[0-9]{12}(?:[0-9]{3})? | (?:5[1-5][0-9]{2} | 222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12} | 3[47][0-9]{13} | 3(?:0[0-5]|[68][0-9])[0-9]{11} | 6(?:011|5[0-9]{2})[0-9]{12} |  (?:2131|1800|35\d{3})\d{11})$'),
	CONSTRAINT created_date_check CHECK (created_date <= CURRENT_TIMESTAMP),
	CONSTRAINT updated_date_check CHECK (updated_date <= CURRENT_TIMESTAMP),
	CONSTRAINT status_check CHECK (status = 'APPROVED' OR status = 'ERROR' OR status = 'IN_PROGRESS'),
	CONSTRAINT payment_type_check CHECK (payment_type = 'CARD' OR payment_type = 'ACCOUNT'),
	CONSTRAINT source_account_number_check CHECK (source_account_number = NULL OR source_account_number ~* '^[A-Z]{2}\d{2}[A-Za-z\d]{1,30}$'),
	CONSTRAINT destination_account_number_check CHECK (destination_account_number = NULL OR destination_account_number ~* '^[A-Z]{2}\d{2}[A-Za-z\d]{1,30}$'),
	CONSTRAINT amount_check CHECK (amount > 0)
);