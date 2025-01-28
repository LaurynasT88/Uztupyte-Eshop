-- Passwords are in the format : Password<UserLetter>123. Unless specified otherwise
INSERT INTO customer (EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, EMAIL_VERIFIED)
VALUES
    ('UserA@junit.com', 'UserA-first_name', 'UserA-last_name', '$2a$10$xfVdHnGjwpyTg3T4KW2DHuqAHJdhCeY2QvlMi3ukp5gIfw9N4zJ42', 'UserA', true),
    ('UserB@junit.com', 'UserB-first_name', 'UserB-last_name', '$2a$10$sqK7Qce7wLN6ZJvVN3lwW.eZOsSJv3ZWWo0UScieolhOqI8pdbEqi', 'UserB', false);
