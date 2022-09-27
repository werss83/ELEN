INSERT INTO public.account (uid, created_at, last_update, first_name, last_name, email, address, city, zip_code,
                            additional_address, coordinate, phone_number, profile_picture, password, google_id,
                            facebook_id, twitter_id, last_login, active, email_verified, deleted)
VALUES ('f8c4d4a5-f582-4a2c-ad3e-0e7f8b5ebfeb', '2020-07-03 21:26:50.207000', '2020-07-03 21:26:50.207000',
        'Parent1', 'Test', 'parent1@local.com', '11 Rue du Maréchal-Joffre', 'Strasbourg', '67000', '4e étage',
        ST_SetSRID(ST_MakePoint(7.757344, 48.587143), 4326),
        '+33612345678', NULL, NULL, NULL, NULL, NULL, NULL, TRUE, TRUE, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.account (uid, created_at, last_update, first_name, last_name, email, address, city, zip_code,
                            additional_address, coordinate, phone_number, profile_picture, password, google_id,
                            facebook_id, twitter_id, last_login, active, email_verified, deleted)
VALUES ('ecb71779-65cb-4d44-8b28-713162f42e05', '2020-07-03 21:26:50.207000', '2020-07-03 21:26:50.207000',
        'Parent2', 'Test', 'parent2@local.com', '25 Rue de Lattre de Tassigny', 'Schiltigheim', '67300', '7e étage',
        ST_SetSRID(ST_MakePoint(7.744844, 48.606904), 4326),
        '+33623456789', NULL, NULL, NULL, NULL, NULL, NULL, TRUE, TRUE, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.account (uid, created_at, last_update, first_name, last_name, email, address, city, zip_code,
                            additional_address, coordinate, phone_number, profile_picture, password, google_id,
                            facebook_id, twitter_id, last_login, active, email_verified, deleted)
VALUES ('3ffc20fb-4268-4841-9be2-c0d8ffa9a67f', '2020-07-03 21:26:50.207000', '2020-07-03 21:26:50.207000',
        'Sitter1', 'Test', 'sitter1@local.com', '16 Rue des Hallebardes', 'Strasbourg', '67000', '4e étage',
        ST_SetSRID(ST_MakePoint(7.749319, 48.581932), 4326),
        '+33634567890', NULL, NULL, NULL, NULL, NULL, NULL, TRUE, TRUE, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.account (uid, created_at, last_update, first_name, last_name, email, address, city, zip_code,
                            additional_address, coordinate, phone_number, profile_picture, password, google_id,
                            facebook_id, twitter_id, last_login, active, email_verified, deleted)
VALUES ('94e7995b-1446-46a1-b3b7-0d5f2a5df69a', '2020-07-03 21:26:50.207000', '2020-07-03 21:26:50.207000',
        'Sitter2', 'Test', 'sitter2@local.com', '24 Rue Waldteufel', 'Bischheim', '67800', '',
        ST_SetSRID(ST_MakePoint(7.752663, 48.613363), 4326),
        '+33645678901', NULL, NULL, NULL, NULL, NULL, NULL, TRUE, TRUE, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('77e1941a-0329-4f43-9b7b-746679f79115', '2020-07-03 21:26:50.212000', '2020-07-03 21:26:50.212000',
        'ROLE_PARENT', (SELECT id FROM account WHERE uid = 'f8c4d4a5-f582-4a2c-ad3e-0e7f8b5ebfeb'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('351f4bb7-1945-40f2-8fd1-c7c90c776578', '2020-07-03 21:26:50.212000', '2020-07-03 21:26:50.212000',
        'ROLE_PARENT', (SELECT id FROM account WHERE uid = 'ecb71779-65cb-4d44-8b28-713162f42e05'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('a769c61c-ca61-4474-a03d-849538d3d532', '2020-07-03 21:26:50.212000', '2020-07-03 21:26:50.212000',
        'ROLE_SITTER', (SELECT id FROM account WHERE uid = '3ffc20fb-4268-4841-9be2-c0d8ffa9a67f'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('631f1277-97c9-44bf-b4de-b7da86585673', '2020-07-03 21:26:50.212000', '2020-07-03 21:26:50.212000',
        'ROLE_SITTER', (SELECT id FROM account WHERE uid = '94e7995b-1446-46a1-b3b7-0d5f2a5df69a'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent (uid, created_at, last_update, account_id, sponsor_code, sponsor_id, deleted)
VALUES ('2219817b-e3d2-4cf2-b175-a93ba3fdf0fb', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = 'f8c4d4a5-f582-4a2c-ad3e-0e7f8b5ebfeb'),
        'ON5weBJbwC', NULL, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent (uid, created_at, last_update, account_id, sponsor_code, sponsor_id, deleted)
VALUES ('ac10d690-a1a0-4675-8f62-074c175375d6', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = 'ecb71779-65cb-4d44-8b28-713162f42e05'),
        'WSUxj6VdUr', NULL, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent_criteria (uid, created_at, last_update, gender, have_car, speaks_english,
                                    experience_with_children, smoke, childhood_training, parent_id, deleted)
VALUES ('a8ba8bee-75d4-43ac-a063-50aad48bf2e9', '2020-07-20 09:11:28.218000', '2020-07-20 09:11:28.218000', FALSE,
        FALSE, FALSE, FALSE, FALSE, FALSE,
        (SELECT id FROM parent WHERE uid = '2219817b-e3d2-4cf2-b175-a93ba3fdf0fb'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent_criteria (uid, created_at, last_update, gender, have_car, speaks_english,
                                    experience_with_children, smoke, childhood_training, parent_id, deleted)
VALUES ('e18f2f92-98ad-4540-97ff-2a1cdcd6d7f5', '2020-07-20 09:11:28.218000', '2020-07-20 09:11:28.218000', FALSE,
        FALSE, FALSE, FALSE, FALSE, FALSE,
        (SELECT id FROM parent WHERE uid = 'ac10d690-a1a0-4675-8f62-074c175375d6'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter (uid, created_at, last_update, account_id, birthday, picture, picture_mime_type,
                           picture_etag)
VALUES ('851e45a2-c1d5-4197-8912-10574d201de1', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = '3ffc20fb-4268-4841-9be2-c0d8ffa9a67f'),
        '1980-03-10', NULL, NULL, NULL)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter (uid, created_at, last_update, account_id, birthday, picture, picture_mime_type,
                           picture_etag)
VALUES ('c84a637d-e4a8-4b6b-9617-3efceebad7a3', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = '94e7995b-1446-46a1-b3b7-0d5f2a5df69a'),
        '1960-03-10', NULL, NULL, NULL)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter_attribute (uid, created_at, last_update, gender, have_car, speaks_english,
                                     experience_with_children, smoke, childhood_training, guard_type, experiences,
                                     experience_types, situation, situation_detail, certifications,
                                     extra_certification, spoken_languages, homework_capabilities, car_situation,
                                     public_transports, availabilities, commendation, information, sitter_id,
                                     deleted)
VALUES ('ea476223-e916-4a72-a7ae-90fb74062e4f', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', FALSE,
        TRUE, FALSE, FALSE, FALSE, FALSE, '{}', '{}', '{}', 'ACTIVE_WITH_CHILDREN', '{}', '{}', '', '{}', '{}', 'YES',
        '{}', '{}', '', '', (SELECT id FROM sitter WHERE uid = '851e45a2-c1d5-4197-8912-10574d201de1'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter_attribute (uid, created_at, last_update, gender, have_car, speaks_english,
                                     experience_with_children, smoke, childhood_training, guard_type, experiences,
                                     experience_types, situation, situation_detail, certifications,
                                     extra_certification, spoken_languages, homework_capabilities, car_situation,
                                     public_transports, availabilities, commendation, information, sitter_id,
                                     deleted)
VALUES ('0b48af1a-2ea7-4ce5-86d7-0946c9cac5cc', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', FALSE,
        TRUE, FALSE, FALSE, FALSE, FALSE, '{}', '{}', '{}', 'SENIOR', '{}', '{}', '', '{}', '{}', 'NO', '{}', '{}',
        '', '', (SELECT id FROM sitter WHERE uid = 'c84a637d-e4a8-4b6b-9617-3efceebad7a3'), FALSE)
ON CONFLICT DO NOTHING;

-- Reservation 1 one shot
INSERT INTO booked_care (uid, created_at, last_update, booked_care_type, start_date, end_date, duration, parent_id)
VALUES ('0fd18560-268e-48a8-aa38-358d59888dc6', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ONE_TIME',
        '2020-08-03 06:00:00', '2020-08-03 11:00:00', 5 * 3600 * 1000,
        (SELECT id FROM parent WHERE uid = '2219817b-e3d2-4cf2-b175-a93ba3fdf0fb'))
ON CONFLICT DO NOTHING;

INSERT INTO planned_unavailability (uid, created_at, last_update, sitter_unavailability_type, start_date, end_date,
                                    duration, sitter_id)
VALUES ('cf3ae158-f62a-4da0-8cc6-9651c04e61e8', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ONE_TIME',
        '2020-08-03 06:00:00', '2020-08-03 11:00:00', 5 * 3600 * 1000,
        (SELECT id FROM sitter WHERE uid = '851e45a2-c1d5-4197-8912-10574d201de1'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_unavailability (uid, created_at, last_update, start_date, end_date, planned_unavailability_id)
VALUES ('30a8e775-ac02-4bdc-a68f-e873873fee73', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000',
        '2020-08-03 06:00:00', '2020-08-03 11:00:00',
        (SELECT id FROM planned_unavailability WHERE uid = 'cf3ae158-f62a-4da0-8cc6-9651c04e61e8'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('3a3d829e-95aa-43cd-8308-37d9053af8ec', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ASSIGNED',
        '2020-08-03 06:00:00', '2020-08-03 11:00:00',
        (SELECT id FROM booked_care WHERE uid = '0fd18560-268e-48a8-aa38-358d59888dc6'),
        (SELECT id FROM effective_unavailability WHERE uid = '30a8e775-ac02-4bdc-a68f-e873873fee73'))
ON CONFLICT DO NOTHING;

-- Reservation 2 one shot
INSERT INTO booked_care (uid, created_at, last_update, booked_care_type, start_date, end_date, duration, parent_id)
VALUES ('d4254124-e319-492d-bb0e-0c09ad15dc34', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ONE_TIME',
        '2020-08-10 06:00:00', '2020-08-10 15:00:00', 5 * 3600 * 1000,
        (SELECT id FROM parent WHERE uid = '2219817b-e3d2-4cf2-b175-a93ba3fdf0fb'))
ON CONFLICT DO NOTHING;

INSERT INTO planned_unavailability (uid, created_at, last_update, sitter_unavailability_type, start_date, end_date,
                                    duration, sitter_id)
VALUES ('770c5701-52df-4fa8-bf9c-c217b1a086f7', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ONE_TIME',
        '2020-08-10 06:00:00', '2020-08-10 15:00:00', 5 * 3600 * 1000,
        (SELECT id FROM sitter WHERE uid = '851e45a2-c1d5-4197-8912-10574d201de1'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_unavailability (uid, created_at, last_update, start_date, end_date, planned_unavailability_id)
VALUES ('eb04a884-23d7-4180-a57f-8a25aafa1b15', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000',
        '2020-08-10 06:00:00', '2020-08-10 15:00:00',
        (SELECT id FROM planned_unavailability WHERE uid = '770c5701-52df-4fa8-bf9c-c217b1a086f7'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('2b9c288f-b0cb-45b6-b43f-0218986a5bcb', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ASSIGNED',
        '2020-08-10 06:00:00', '2020-08-10 15:00:00',
        (SELECT id FROM booked_care WHERE uid = 'd4254124-e319-492d-bb0e-0c09ad15dc34'),
        (SELECT id FROM effective_unavailability WHERE uid = 'eb04a884-23d7-4180-a57f-8a25aafa1b15'))
ON CONFLICT DO NOTHING;

-- Reservation 3 recurring
INSERT INTO booked_care (uid, created_at, last_update, booked_care_type, start_date, end_date, duration, parent_id)
VALUES ('cb081d03-2df9-4b0f-a775-8c2cb28eacb7', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'RECURRENT',
        '2020-08-08 9:30:00', NULL, 3.5 * 3600 * 1000,
        (SELECT id FROM parent WHERE uid = 'ac10d690-a1a0-4675-8f62-074c175375d6'))
ON CONFLICT DO NOTHING;

INSERT INTO planned_unavailability (uid, created_at, last_update, sitter_unavailability_type, start_date, end_date,
                                    duration, sitter_id)
VALUES ('84a4c2af-b14d-4610-8f25-4ad1588a5b7a', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'RECURRENT',
        '2020-08-08 9:30:00', NULL, 3.5 * 3600 * 1000,
        (SELECT id FROM sitter WHERE uid = 'c84a637d-e4a8-4b6b-9617-3efceebad7a3'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_unavailability (uid, created_at, last_update, start_date, end_date, planned_unavailability_id)
VALUES ('ff0b5340-2b60-4b17-a3b2-b509f0007752', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000',
        '2020-08-08 9:30:00', '2020-08-08 13:00:00',
        (SELECT id FROM planned_unavailability WHERE uid = '84a4c2af-b14d-4610-8f25-4ad1588a5b7a'))
ON CONFLICT DO NOTHING;
INSERT INTO effective_unavailability (uid, created_at, last_update, start_date, end_date, planned_unavailability_id)
VALUES ('f854c2a9-244b-48c6-946d-5d35f4cb1ec1', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000',
        '2020-08-15 9:30:00', '2020-08-15 13:00:00',
        (SELECT id FROM planned_unavailability WHERE uid = '84a4c2af-b14d-4610-8f25-4ad1588a5b7a'))
ON CONFLICT DO NOTHING;
INSERT INTO effective_unavailability (uid, created_at, last_update, start_date, end_date, planned_unavailability_id)
VALUES ('41226a04-5a4c-490c-9ca0-2944d365b23f', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000',
        '2020-08-22 9:30:00', '2020-08-22 13:00:00',
        (SELECT id FROM planned_unavailability WHERE uid = '84a4c2af-b14d-4610-8f25-4ad1588a5b7a'))
ON CONFLICT DO NOTHING;
INSERT INTO effective_unavailability (uid, created_at, last_update, start_date, end_date, planned_unavailability_id)
VALUES ('61296d5c-d2d5-4e2d-a574-df53cff7dc77', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000',
        '2020-08-29 9:30:00', '2020-08-29 13:00:00',
        (SELECT id FROM planned_unavailability WHERE uid = '84a4c2af-b14d-4610-8f25-4ad1588a5b7a'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('ad11f6c8-dbdd-4b7c-a73d-931b7d582b0c', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ASSIGNED',
        '2020-08-08 9:30:00', '2020-08-08 13:00:00',
        (SELECT id FROM booked_care WHERE uid = 'cb081d03-2df9-4b0f-a775-8c2cb28eacb7'),
        (SELECT id FROM effective_unavailability WHERE uid = 'ff0b5340-2b60-4b17-a3b2-b509f0007752'))
ON CONFLICT DO NOTHING;
INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('68bec7e3-4001-46ec-accb-cbec37e16547', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ASSIGNED',
        '2020-08-15 9:30:00', '2020-08-15 13:00:00',
        (SELECT id FROM booked_care WHERE uid = 'cb081d03-2df9-4b0f-a775-8c2cb28eacb7'),
        (SELECT id FROM effective_unavailability WHERE uid = 'f854c2a9-244b-48c6-946d-5d35f4cb1ec1'))
ON CONFLICT DO NOTHING;
INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('ed34c327-1f61-43c3-b934-348df3fa8aa6', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ASSIGNED',
        '2020-08-22 9:30:00', '2020-08-22 13:00:00',
        (SELECT id FROM booked_care WHERE uid = 'cb081d03-2df9-4b0f-a775-8c2cb28eacb7'),
        (SELECT id FROM effective_unavailability WHERE uid = '41226a04-5a4c-490c-9ca0-2944d365b23f'))
ON CONFLICT DO NOTHING;
INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('0753015b-16ff-4452-9c09-cc7cdc901d11', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ASSIGNED',
        '2020-08-29 9:30:00', '2020-08-29 13:00:00',
        (SELECT id FROM booked_care WHERE uid = 'cb081d03-2df9-4b0f-a775-8c2cb28eacb7'),
        (SELECT id FROM effective_unavailability WHERE uid = '61296d5c-d2d5-4e2d-a574-df53cff7dc77'))
ON CONFLICT DO NOTHING;

-- Reservation 4 recurring
INSERT INTO booked_care (uid, created_at, last_update, booked_care_type, start_date, end_date, duration, parent_id)
VALUES ('fcc90c3e-a9f5-4ccf-bc6b-463ab3afa471', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'RECURRENT',
        '2020-08-06 14:45:00', NULL, 1.75 * 3600 * 1000,
        (SELECT id FROM parent WHERE uid = 'ac10d690-a1a0-4675-8f62-074c175375d6'))
ON CONFLICT DO NOTHING;

INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('96993daf-a623-4f5e-a342-cea4b8891f1c', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'PLANNED',
        '2020-08-06 14:45:00', '2020-08-06 16:30:00',
        (SELECT id FROM booked_care WHERE uid = 'fcc90c3e-a9f5-4ccf-bc6b-463ab3afa471'),
        NULL)
ON CONFLICT DO NOTHING;
INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('7fdfa602-be23-4760-9911-e5a34c098035', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'PLANNED',
        '2020-08-13 14:45:00', '2020-08-13 16:30:00',
        (SELECT id FROM booked_care WHERE uid = 'fcc90c3e-a9f5-4ccf-bc6b-463ab3afa471'),
        NULL)
ON CONFLICT DO NOTHING;
INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('0695d218-ab90-4638-9c84-ae37dc000811', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'PLANNED',
        '2020-08-20 14:45:00', '2020-08-20 16:30:00',
        (SELECT id FROM booked_care WHERE uid = 'fcc90c3e-a9f5-4ccf-bc6b-463ab3afa471'),
        NULL)
ON CONFLICT DO NOTHING;

INSERT INTO effective_care (uid, created_at, last_update, status, start_date, end_date, booked_care_id,
                            effective_unavailability_id)
VALUES ('b9bd39bf-f8ac-4b65-bd8a-02a3b2eeb42f', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'PLANNED',
        '2020-08-27 14:45:00', '2020-08-27 16:30:00',
        (SELECT id FROM booked_care WHERE uid = 'fcc90c3e-a9f5-4ccf-bc6b-463ab3afa471'),
        NULL)
ON CONFLICT DO NOTHING;

-- Reservation 5 one shot (without effective care)
INSERT INTO booked_care (uid, created_at, last_update, booked_care_type, start_date, end_date, duration, parent_id)
VALUES ('b20e768f-bdbe-4eee-b4c1-8aff52290ca4', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', 'ONE_TIME',
        '2020-08-12 06:00:00', '2020-08-12 11:00:00', 5 * 3600 * 1000,
        (SELECT id FROM parent WHERE uid = 'ac10d690-a1a0-4675-8f62-074c175375d6'))
ON CONFLICT DO NOTHING;
