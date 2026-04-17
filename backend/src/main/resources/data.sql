-- Combined seed data. Runs after JPA DDL.
-- MERGE INTO is used so re-starting the app with a file-mode H2 doesn't
-- blow up on unique-key violations for rows we already inserted.

MERGE INTO users (id, username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points)
KEY (id)
VALUES
(1, 'Alex', 'Ocean Lover',    '', 'London',      120, 300, 500, 120),
(2, 'Sam',  'Beach Cleaner',  '', 'Southampton',  80, 500, 400, 200),
(3, 'Tala', 'Sea Protector',  '', 'Bristol',     150, 700, 600, 350),
(4, 'May',  'Volunteer',      '', 'London',       34, 498, 500, 498),
(5, 'Bob',  'Weekend Diver',  '', 'Bristol',      33, 250, 400, 250),
(6, 'Alice','Coastline Fan',  '', 'London',       59, 100, 300, 100),
(7, 'Diana','Reef Scout',     '', 'London',       50, 113, 300, 113),
(8, 'Ahmed','Rescue Team',    '', 'Bristol',      70, 113, 300, 113),
(9, 'Nay',  'Cleanup Captain','', 'Southampton',  53, 113, 300, 113);
