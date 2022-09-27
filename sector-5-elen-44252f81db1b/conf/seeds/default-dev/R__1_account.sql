--
INSERT INTO public.account (uid, created_at, last_update, first_name, last_name, email, address, city, zip_code,
                            additional_address, coordinate, phone_number, profile_picture, password, google_id,
                            facebook_id, twitter_id, last_login, active, email_verified, deleted)
VALUES ('63c92be8-8576-4d39-9322-a09b6474ad99', '2020-07-03 21:26:50.207000', '2020-07-03 21:26:50.207000', 'Pierre',
        'Adam', 'adam.pierre.dany@gmail.com', '2A Rue Thomann', 'Strasbourg', '67000', '3rd floor',
        ST_SetSRID(ST_MakePoint(7.7435313, 48.5844866), 4326),
        '+33615727102', 'https://lh3.googleusercontent.com/a-/AOh14GjvmrAJnttyuSfDY7WOtPcJXR9x0-PeReen3ZU4bw', NULL,
        '115449349847391272088', NULL, NULL, NULL, TRUE, TRUE, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.account (uid, created_at, last_update, first_name, last_name, email, address, city, zip_code,
                            additional_address, coordinate, phone_number, profile_picture, password, google_id,
                            facebook_id, twitter_id, last_login, active, email_verified, deleted)
VALUES ('8ff5b81a-15ca-4936-bf0e-cc768e519275', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000', 'Lucas',
        'Stadelmann', 'lucas.stadelmann@gmail.com', '6B rue des Combattants Africains', 'Strasbourg', '67100', '',
        ST_SetSRID(ST_MakePoint(7.75184, 48.5710086), 4326),
        '+336847405064', NULL,
        'pbkdf2:VK/QjF3I63O4CDiwZRikg2M27DM=:CklxUxntfJdpf/9kFNYL1F2QC4NICl3uiVZBtTHb3iFr/vUaV39EmdKGOd8H2ePBcWUV8ywOboFGf5YwtgYcnQ==',
        NULL, NULL, NULL, NULL, TRUE, TRUE, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('6810533a-63a3-4ce7-8311-a812a3cdea86', '2020-07-03 21:26:50.212000', '2020-07-03 21:26:50.212000',
        'ROLE_PARENT', (SELECT id FROM account WHERE uid = '63c92be8-8576-4d39-9322-a09b6474ad99'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('a8faa662-d32a-4241-a0cf-4b8b379637b4', '2020-07-03 23:32:44.194000', '2020-07-03 23:32:44.893000',
        'ROLE_SITTER', (SELECT id FROM account WHERE uid = '63c92be8-8576-4d39-9322-a09b6474ad99'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('7d4f04a1-7e8b-4234-8adc-661068f9303c', '2020-07-03 23:32:46.513000', '2020-07-03 23:32:45.379000',
        'ROLE_ADMIN', (SELECT id FROM account WHERE uid = '63c92be8-8576-4d39-9322-a09b6474ad99'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('0dd9a0ad-895c-4c4f-beb2-34811a431243', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        'ROLE_PARENT', (SELECT id FROM account WHERE uid = '8ff5b81a-15ca-4936-bf0e-cc768e519275'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('7a3f7130-1071-421c-b30a-21b73275bd33', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        'ROLE_SITTER', (SELECT id FROM account WHERE uid = '8ff5b81a-15ca-4936-bf0e-cc768e519275'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.role (uid, created_at, last_update, role, account_id, deleted)
VALUES ('75bed686-f3fe-45d6-ae29-351a89662e03', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        'ROLE_ADMIN', (SELECT id FROM account WHERE uid = '8ff5b81a-15ca-4936-bf0e-cc768e519275'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent (uid, created_at, last_update, account_id, sponsor_code, sponsor_id, deleted)
VALUES ('4ede7085-77a8-4bb2-a41e-be2702dd6aab', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = '63c92be8-8576-4d39-9322-a09b6474ad99'),
        '0000000001', NULL, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent (uid, created_at, last_update, account_id, sponsor_code, sponsor_id, deleted)
VALUES ('0b174e4d-772b-4e49-94cd-35d2b1bc297a', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = '8ff5b81a-15ca-4936-bf0e-cc768e519275'),
        '0000000002', NULL, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent_criteria (uid, created_at, last_update, gender, have_car, speaks_english,
                                    experience_with_children, smoke, childhood_training, parent_id, deleted)
VALUES ('99d3796d-3d37-4fa4-a077-871479583a27', '2020-07-20 09:11:28.218000', '2020-07-20 09:11:28.218000', FALSE,
        FALSE, FALSE, FALSE, FALSE, FALSE, (SELECT id FROM parent WHERE uid = '4ede7085-77a8-4bb2-a41e-be2702dd6aab'),
        FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.parent_criteria (uid, created_at, last_update, gender, have_car, speaks_english,
                                    experience_with_children, smoke, childhood_training, parent_id, deleted)
VALUES ('93e5a300-faab-441d-ab1d-02db3e34a348', '2020-07-20 09:11:28.218000', '2020-07-20 09:11:28.218000', FALSE,
        FALSE, FALSE, FALSE, FALSE, FALSE, (SELECT id FROM parent WHERE uid = '0b174e4d-772b-4e49-94cd-35d2b1bc297a'),
        FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter (uid, created_at, last_update, account_id, birthday, picture, picture_mime_type,
                           picture_etag)
VALUES ('6ffd1897-7265-43ce-8e51-277c313c3fee', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = '63c92be8-8576-4d39-9322-a09b6474ad99'),
        '1991-03-10', NULL, NULL, NULL)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter (uid, created_at, last_update, account_id, birthday, picture, picture_mime_type,
                           picture_etag)
VALUES ('92d04cdd-4e99-4f00-9154-2e569bcd6e70', '2020-07-05 08:37:00.066000', '2020-07-05 08:37:00.066000',
        (SELECT id FROM account WHERE uid = '8ff5b81a-15ca-4936-bf0e-cc768e519275'),
        '1993-01-01', NULL, NULL, NULL)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter_attribute (uid, created_at, last_update, gender, have_car, speaks_english,
                                     experience_with_children, smoke, childhood_training, guard_type, experiences,
                                     experience_types, situation, situation_detail, certifications,
                                     extra_certification, spoken_languages, homework_capabilities, car_situation,
                                     public_transports, availabilities, commendation, information, sitter_id,
                                     deleted)
VALUES ('c900f806-f442-4bff-8881-71316e90bccb', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', FALSE,
        TRUE, FALSE, FALSE, FALSE, FALSE, '{}', '{}', '{}', 'STUDENT', '{}', '{}', '', '{}', '{}', 'YES', '{}', '{}',
        '', '', (SELECT id FROM sitter WHERE uid = '6ffd1897-7265-43ce-8e51-277c313c3fee'), FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO public.sitter_attribute (uid, created_at, last_update, gender, have_car, speaks_english,
                                     experience_with_children, smoke, childhood_training, guard_type, experiences,
                                     experience_types, situation, situation_detail, certifications,
                                     extra_certification, spoken_languages, homework_capabilities, car_situation,
                                     public_transports, availabilities, commendation, information, sitter_id,
                                     deleted)
VALUES ('1d8f2135-7290-42fd-a616-b5c6fc5cf8ae', '2020-07-20 09:11:28.246000', '2020-07-20 09:11:28.251000', FALSE,
        TRUE, FALSE, FALSE, FALSE, FALSE, '{}', '{}', '{}', 'STUDENT', '{}', '{}', '', '{}', '{}', 'YES', '{}', '{}',
        '', '', (SELECT id FROM sitter WHERE uid = '92d04cdd-4e99-4f00-9154-2e569bcd6e70'), FALSE)
ON CONFLICT DO NOTHING;
