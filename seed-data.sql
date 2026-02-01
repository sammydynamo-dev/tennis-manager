drop database if exists TennisLeague;
create database TennisLeague;
use TennisLeague;

drop table if exists WorkExperience;
drop table if exists PlayerTeamAssociation;
drop table if exists Coach;
drop table if exists Player;
drop table if exists team;


CREATE TABLE Team (
    TeamNumber INT PRIMARY KEY,
    Name VARCHAR(255),
    City VARCHAR(255),
    ManagerName VARCHAR(255)
);


CREATE TABLE Coach (
    CoachID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(255),
    TelephoneNumber VARCHAR(20),
    TeamNumber INT,
    FOREIGN KEY (TeamNumber) REFERENCES Team(TeamNumber)
);

CREATE TABLE Player (
    PlayerID INT PRIMARY KEY AUTO_INCREMENT,
    LeagueWideNumber INT UNIQUE,
    Name VARCHAR(255),
    Age INT
);


CREATE TABLE WorkExperience (
    ExperienceID INT PRIMARY KEY AUTO_INCREMENT,
    CoachID INT,
    ExperienceType VARCHAR(255),
    Duration INT,
    FOREIGN KEY (CoachID) REFERENCES Coach(CoachID)
);


CREATE TABLE PlayerTeamAssociation (
    AssociationID INT PRIMARY KEY AUTO_INCREMENT,
    PlayerID INT,
    TeamNumber INT,
    YearJoined INT,
    YearLeft INT,
    FOREIGN KEY (PlayerID) REFERENCES Player(PlayerID),
    FOREIGN KEY (TeamNumber) REFERENCES Team(TeamNumber)
);

drop procedure if exists SeedData;
DELIMITER //
CREATE PROCEDURE SeedData()
BEGIN

declare rowIndex integer;
set rowIndex = 0;


-- Seed data for Teams
INSERT INTO Team (TeamNumber, Name, City, ManagerName)
VALUES 
    (1, 'Manchester United', 'Manchester', 'Ole Gunnar Solskjær'),
    (2, 'Real Madrid', 'Madrid', 'Zinedine Zidane'),
    (3, 'FC Barcelona', 'Barcelona', 'Ronald Koeman'),
    (4, 'Bayern Munich', 'Munich', 'Hansi Flick'),
    (5, 'Liverpool FC', 'Liverpool', 'Jürgen Klopp'),
    (6, 'Paris Saint-Germain', 'Paris', 'Mauricio Pochettino'),
    (7, 'Juventus', 'Turin', 'Andrea Pirlo'),
    (8, 'Chelsea FC', 'London', 'Thomas Tuchel'),
    (9, 'Manchester City', 'Manchester', 'Pep Guardiola'),
    (10, 'AC Milan', 'Milan', 'Stefano Pioli');

-- Seed data for Coaches
-- Seed data for Coaches (30 coaches for 10 teams)
INSERT INTO Coach (Name, TelephoneNumber, TeamNumber)
VALUES
    -- Coaches for Manchester United (TeamNumber = 1)
    ('Ole Gunnar Solskjær', '+44 1234 567890', 1),
    ('Michael Carrick', '+44 1234 567891', 1),
    ('Mike Phelan', '+44 1234 567892', 1),
    
    -- Coaches for Real Madrid (TeamNumber = 2)
    ('Zinedine Zidane', '+34 123 456789', 2),
    ('David Bettoni', '+34 123 456790', 2),
    ('Carlos Lalín', '+34 123 456791', 2),
    ('Hamidou Msaidie', '+34 123 456792', 2),
    
    -- Coaches for FC Barcelona (TeamNumber = 3)
    ('Ronald Koeman', '+34 234 567890', 3),
    ('Alfred Schreuder', '+34 234 567891', 3),
    ('Henrik Larsson', '+34 234 567892', 3),
    
    -- Coaches for Bayern Munich (TeamNumber = 4)
    ('Hansi Flick', '+49 1234 567890', 4),
    ('Hermann Gerland', '+49 1234 567891', 4),
    ('Danny Röhl', '+49 1234 567892', 4),
    ('Miroslav Klose', '+49 1234 567893', 4),
    
    -- Coaches for Liverpool FC (TeamNumber = 5)
    ('Jürgen Klopp', '+44 2345 678901', 5),
    ('Pepijn Lijnders', '+44 2345 678902', 5),
    ('Peter Krawietz', '+44 2345 678903', 5),
    
    -- Coaches for Paris Saint-Germain (TeamNumber = 6)
    ('Mauricio Pochettino', '+33 123 456789', 6),
    ('Jesus Perez', '+33 123 456790', 6),
    ('Miguel D''Agostino', '+33 123 456791', 6),
    
    -- Coaches for Juventus (TeamNumber = 7)
    ('Andrea Pirlo', '+39 123 456789', 7),
    ('Luca Gotti', '+39 123 456790', 7),
    ('Gianluca Spinelli', '+39 123 456791', 7),
    
    -- Coaches for Chelsea FC (TeamNumber = 8)
    ('Thomas Tuchel', '+44 3456 789012', 8),
    ('Arno Michels', '+44 3456 789013', 8),
    ('Zsolt Lőw', '+44 3456 789014', 8),
    
    -- Coaches for Manchester City (TeamNumber = 9)
    ('Pep Guardiola', '+44 4567 890123', 9),
    ('Juanma Lillo', '+44 4567 890124', 9),
    ('Rodolfo Borrell', '+44 4567 890125', 9),
    
    -- Coaches for AC Milan (TeamNumber = 10)
    ('Stefano Pioli', '+39 234 567890', 10),
    ('Giuseppe Baresi', '+39 234 567891', 10),
    ('Francesco Mauri', '+39 234 567892', 10);

-- Seed data for Players

-- INSERT INTO Player (LeagueWideNumber, Name, Age)

while(rowIndex < 200) do
set rowIndex = rowIndex+1;
INSERT INTO Player (LeagueWideNumber, Name, Age)
SELECT 
    FLOOR(RAND() * 100000) AS LeagueWideNumber,
    CONCAT('Player ', rowIndex) as Name,
    FLOOR(RAND() * 15)+18 AS Age; -- Assuming players are at least 18 years old
end while;


-- Seed data for Work Experience
INSERT INTO WorkExperience (CoachID, ExperienceType, Duration)
SELECT 
    c.CoachID,
    CONCAT('Assistant Coach at ', t.Name),
    FLOOR(RAND() * 10) + 5 -- Assuming coaches have at least 5 years of experience
FROM
    Coach AS c
JOIN
    Team AS t ON c.TeamNumber = t.TeamNumber
WHERE
    RAND() < 0.7; -- 70% chance of generating experience for each coach, assuming not all coaches have public work experience

-- Seed data for Player Team Association
INSERT INTO PlayerTeamAssociation (PlayerID, TeamNumber, YearJoined, YearLeft)
SELECT 
    p.PlayerID,
    FLOOR(RAND() * 10) + 1 AS TeamNumber,
    FLOOR(RAND() * 10) + 2010 AS YearJoined, -- Assuming teams exist since 2010
    FLOOR(RAND() * 10) + 2014 AS YearLeft -- Assuming some players have recently left their teams
FROM
    Player AS p
JOIN
    (SELECT RAND() AS rnd) AS r
WHERE
    RAND() < 0.8; -- 80% chance of generating association for each player, assuming players 

end //

call SeedData();
