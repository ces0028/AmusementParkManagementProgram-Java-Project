DROP DATABASE IF EXISTS amusementParkDB;
CREATE DATABASE IF NOT EXISTS amusementParkDB;
USE amusementParkDB;

-- TABLE(1)
CREATE TABLE staffTBL(
	staffNumber INT NOT NULL AUTO_INCREMENT,
	staffName CHAR(5) NOT NULL,
	password CHAR(10) NOT NULL,
	CONSTRAINT pk_staffTBL_staffNumber PRIMARY KEY(staffNumber)
);

-- INDEX (1)
CREATE INDEX IDX_staffTBL_staffNumber ON staffTBL(staffNumber);

-- TABLE(2)
DROP TABLE IF EXISTS memberTBL;
CREATE TABLE memberTBL(
	memberIntNumber INT NOT NULL AUTO_INCREMENT,
	memberName CHAR(5) NOT NULL, 
	birthDate DATE NOT NULL,
	telNumber CHAR(13) NOT NULL,
	email TEXT NOT NULL,
	membershipType CHAR(10) NOT NULL,
	membershipPurchaseDate DATE NOT NULL,
	membershipExpirationDate DATE,
	CONSTRAINT pk_memberTBL_memberIntNumber PRIMARY KEY(memberIntNumber),
	CONSTRAINT unique_memberTBL_telNumber UNIQUE INDEX (telNumber)
);
-- INDEX (2)
CREATE INDEX IDX_memberTBL_memberIntNumber ON memberTBL(memberIntNumber);

-- PROCEDURE (1)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_insert_memberTBL//
CREATE PROCEDURE procedure_insert_memberTBL(
    IN in_memberName CHAR(5), 
    IN in_birthDate DATE,
    IN in_telNumber CHAR(13),
    IN in_email TEXT,
    IN in_membershipType CHAR(10),
    IN in_membershipPurchaseDate DATE
)
BEGIN
DECLARE in_membershipExpirationDate DATE;
SET in_membershipExpirationDate = DATE_ADD(in_membershipPurchaseDate, INTERVAL 365 DAY);
	INSERT INTO memberTBL(memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate) VALUES (in_memberName, in_birthDate, in_telNumber, in_email, in_membershipType, in_membershipPurchaseDate);
	UPDATE memberTBL SET membershipExpirationDate = in_membershipExpirationDate WHERE telNumber = in_telNumber;

END //
DELIMITER ;

-- PROCEDRUE (2)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_update1_memberTBL //
CREATE PROCEDURE procedure_update1_memberTBL(
    IN in_memberName CHAR(5), 
    IN in_birthDate DATE,
    IN in_telNumber CHAR(13),
    IN in_email TEXT,
    IN in_membershipType CHAR(10),
    IN in_membershipPurchaseDate DATE,
	In in_memberIntNumber INT
)
BEGIN
	UPDATE memberTBL SET memberName = in_memberName, birthDate = in_birthDate, telNumber = in_telNumber, email = in_email, membershipType = in_membershipType, 
    membershipPurchaseDate = in_membershipPurchaseDate, membershipExpirationDate = DATE_ADD(membershipPurchaseDate, INTERVAL 365 DAY)
    WHERE memberIntNumber = in_memberIntNumber;
END //
DELIMITER ;

-- PROCEDURE (3)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_update2_memberTBL //
CREATE PROCEDURE procedure_update2_memberTBL(
	IN in_memberIntNumber INT,
    IN in_membershipPurchaseDate DATE
)
BEGIN
	UPDATE memberTBL SET membershipExpirationDate = in_membershipExpirationDate WHERE memberIntNumber = in_memberIntNumber;
END //
DELIMITER ;

-- PROCEDURE (4)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_delete_memberTBL //
CREATE PROCEDURE procedure_delete_memberTBL(
	IN in_memberIntNumber INT
)
BEGIN
	DELETE FROM memberTBL WHERE memberIntNumber = in_memberIntNumber;
END //
DELIMITER ;

-- PROCEDURE (5)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_selectSearch_memberTBL //
CREATE PROCEDURE procedure_selectSearch_memberTBL(
	IN selectNumber INT,
    IN in_memberIntNumber INT,
    IN in_memberName CHAR(5)
)
BEGIN
	IF selectNumber = 1 THEN
		SELECT * FROM memberTBL WHERE memberIntNumber = in_memberIntNumber;
	ELSE
		SELECT * FROM memberTBL WHERE memberName = in_memberName;
	END IF;
END //
DELIMITER ;

-- PROCEDURE (6)
DROP PROCEDURE IF EXISTS procedure_select_memberTBL;
DELIMITER //
CREATE PROCEDURE procedure_select_memberTBL()
BEGIN
	SELECT * FROM memberTBL;
END //
DELIMITER ;

-- PROCEDURE (7)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_selectOrderBy_memberTBL //
CREATE PROCEDURE procedure_selectOrderBy_memberTBL(
	IN selectNumber INT
)
BEGIN
	IF selectNumber = 1 THEN
    	SELECT * FROM memberTBL ORDER BY memberIntNumber ASC;
	ELSEIF selectNumber = 2 THEN
		SELECT * FROM memberTBL ORDER BY memberName ASC;
	ELSE
		SELECT * FROM memberTBL ORDER BY membershipExpirationDate ASC;
	END IF;
END //
DELIMITER ;

-- PROCEDURE (8)
DROP PROCEDURE IF EXISTS procedure_select_deleteMemberTBL;
DELIMITER //
CREATE PROCEDURE procedure_select_deleteMemberTBL()
BEGIN
	SELECT * FROM deleteMemberTBL;
END //
DELIMITER ;

-- PROCEDURE (9)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_insertselect_deleteMemberTBL //
CREATE PROCEDURE procedure_insertselect_deleteMemberTBL(
	IN in_memberIntNumber INT
)
BEGIN
	INSERT INTO memberTBL (SELECT * FROM deleteMemberTBL WHERE memberIntNumber = in_memberIntNumber);
END //
DELIMITER ;

-- PROCEDURE (10)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_delete_deleteMemberTBL //
CREATE PROCEDURE procedure_delete_deleteMemberTBL(
	IN in_memberIntNumber INT
)
BEGIN
	DELETE FROM deleteMemberTBL WHERE memberIntNumber = in_memberIntNumber;
END //
DELIMITER ;

-- PROCEDURE (11)
DELIMITER //
DROP PROCEDURE IF EXISTS procedure_delete_byExpirationDate_memberTBL //
CREATE PROCEDURE procedure_delete_byExpirationDate_memberTBL(
	inputDate DATE
)
BEGIN
	DELETE FROM memberTBL WHERE membershipExpirationDate < inputDate;
END //
DELIMITER ;

-- TABLE(3)
DROP TABLE IF EXISTS deleteMemberTBL;
CREATE TABLE deleteMemberTBL(
	memberIntNumber INT NOT NULL,
	memberName CHAR(5) NOT NULL, 
	birthDate DATE NOT NULL,
	telNumber CHAR(13) NOT NULL,
	email TEXT NOT NULL,
	membershipType CHAR(10) NOT NULL,
	membershipPurchaseDate DATE NOT NULL,
	membershipExpirationDate DATE
);

-- TRIGGER
DELIMITER //
DROP TRIGGER IF EXISTS trigger_deleteMemberTBL//
CREATE TRIGGER trigger_deleteMemberTBL AFTER DELETE ON memberTBL FOR EACH ROW
BEGIN
	INSERT INTO deleteMemberTBL VALUES (OLD.memberIntNumber, OLD.memberName, OLD.birthDate, OLD.telNumber, OLD.email, OLD.membershipType, OLD.membershipPurchaseDate, OLD.membershipExpirationDate);
END //
DELIMITER ;

-- FUNCTION ON
SET GLOBAL log_bin_trust_function_creators = 1;

-- FUNCTION (1)
DELIMITER //
DROP FUNCTION IF EXISTS function_GetByMonthly //
CREATE FUNCTION function_GetByMonthly(
    expirationMonth INT
) RETURNS CHAR(5)
BEGIN
	DECLARE func_Expiration CHAR(2);
	DECLARE searchQuery CHAR(10);
	DECLARE countValue CHAR(2);
    
    IF(expirationMonth < 10)
		THEN SET func_Expiration = CONCAT('0', expirationMonth);
	ELSE
		SET func_Expiration = expirationMonth;
	END IF;
    SELECT CONCAT ('%-', func_Expiration, '-%') INTO searchQuery;
	SELECT COUNT(*) INTO countValue FROM memberTBL WHERE membershipPurchaseDate LIKE searchQuery;
    RETURN countValue;
END //
DELIMITER ;

-- FUNCTION (2)
DROP FUNCTION IF EXISTS function_GetByType;
DELIMITER //
CREATE FUNCTION function_GetByType(
    membershipType_Stats INT
) RETURNS CHAR(5)
BEGIN
	DECLARE func_membershipType CHAR(10);
	DECLARE countValue CHAR(2);

    CASE membershipType_Stats
		WHEN 0 THEN SET func_membershipType = 'VIP';
		WHEN 1 THEN SET func_membershipType = '스탠다드+';
		WHEN 2 THEN SET func_membershipType = '스탠다드';
		ELSE SET func_membershipType = '라이트';
	END CASE;
	
	SELECT COUNT(*) INTO countValue FROM memberTBL WHERE membershipType = func_membershipType;
	RETURN countValue;
END //
DELIMITER ;

-- INSERT TEST DATA
INSERT INTO staffTBL(staffName, password) VALUES ('신채은', 'sce1234');
CALL procedure_insert_memberTBL ('김민지', '1996-10-12', '010-1111-1111', 'kmj1234@gmail.com', 'VIP', '2022-02-12');
CALL procedure_insert_memberTBL ('라민지', '2001-01-29', '010-2222-2222', 'rmj1234@gmail.com', '스탠다드', '2022-04-24');
CALL procedure_insert_memberTBL ('단민지', '1999-04-25', '010-3333-3333', 'dmj1234@gmail.com', '라이트', '2022-02-19');
CALL procedure_insert_memberTBL ('이민지', '1994-10-04', '010-4444-4444', 'lmj1234@gmail.com', '스탠다드', '2022-10-14');
CALL procedure_insert_memberTBL ('마민지', '1988-05-03', '010-5555-5555', 'mmj1234@gmail.com', '스탠다드', '2022-07-21');
CALL procedure_insert_memberTBL ('신민지', '1974-12-22', '010-6666-6666', 'smj1234@gmail.com', '스탠다드+', '2021-06-03');
CALL procedure_insert_memberTBL ('나민지', '1967-01-01', '010-7777-7777', 'nmj1234@gmail.com', '스탠다드+', '2021-12-01');
CALL procedure_insert_memberTBL ('정민지', '1984-08-15', '010-8888-8888', 'jmj1234@gmail.com', '라이트', '2022-03-14');
CALL procedure_insert_memberTBL ('박민지', '1993-03-12', '010-9999-9999', 'pmj1234@gmail.com', '스탠다드', '2021-10-03');