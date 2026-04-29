-- Combined seed data. Runs after JPA DDL because
-- spring.jpa.defer-datasource-initialization=true.
-- The id column on users is IDENTITY (auto-increment), so do not
-- supply an explicit id — H2 assigns 1..N in insertion order.

INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Alex',  'Ocean Lover',     '', 'London',      120, 300, 500, 120);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Sam',   'Beach Cleaner',   '', 'Southampton',  80, 500, 400, 200);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Tala',  'Sea Protector',   '', 'Bristol',     150, 700, 600, 350);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('May',   'Volunteer',       '', 'London',       34, 498, 500, 498);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Bob',   'Weekend Diver',   '', 'Bristol',      33, 250, 400, 250);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Alice', 'Coastline Fan',   '', 'London',       59, 100, 300, 100);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Diana', 'Reef Scout',      '', 'London',       50, 113, 300, 113);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Ahmed', 'Rescue Team',     '', 'Bristol',      70, 113, 300, 113);
INSERT INTO users (username, bio, profile_picture, country, followers, total_score, weekly_goal, weekly_points) VALUES ('Nay',   'Cleanup Captain', '', 'Southampton',  53, 113, 300, 113);
