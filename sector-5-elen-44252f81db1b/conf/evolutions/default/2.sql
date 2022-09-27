------------------------
--- INITIAL DATABASE ---
------------------------

# --- !Ups
ALTER TABLE sitter_attribute
    ADD COLUMN past_experience TEXT;
ALTER TABLE sitter_attribute
    ADD COLUMN social_security_number VARCHAR(255);

# --- !Downs
ALTER TABLE sitter_attribute
    DROP COLUMN past_experience;
ALTER TABLE sitter_attribute
    DROP COLUMN social_security_number;
