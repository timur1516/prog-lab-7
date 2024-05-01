CREATE TYPE color AS ENUM
    ('RED', 'BLUE', 'YELLOW', 'BROWN');
CREATE TYPE country AS ENUM
    ('UNITED_KINGDOM', 'USA', 'VATICAN');
CREATE TYPE status AS ENUM
    ('FIRED', 'HIRED', 'REGULAR', 'PROBATION');

CREATE TABLE IF NOT EXISTS coordinates
(
    id bigserial NOT NULL,
    x double precision NOT NULL,
    y double precision NOT NULL,
    CONSTRAINT coordinates_pkey PRIMARY KEY (id),
    CONSTRAINT coordinates_x_check CHECK (x <= 657::double precision)
);

CREATE TABLE IF NOT EXISTS person
(
    id bigserial NOT NULL,
    height bigint NOT NULL,
    eyecolor color,
    nationality country,
    CONSTRAINT person_pkey PRIMARY KEY (id),
    CONSTRAINT person_height_check CHECK (height > 0)
);

CREATE TABLE IF NOT EXISTS user_info
(
    id bigserial NOT NULL,
    username text COLLATE pg_catalog."default" NOT NULL,
    password text COLLATE pg_catalog."default" NOT NULL,
    salt text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT user_info_pkey PRIMARY KEY (id),
    CONSTRAINT user_info_username_key UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS worker
(
    id bigserial NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    coordinates_id bigint NOT NULL,
    creationdate timestamp with time zone NOT NULL DEFAULT now(),
    salary integer NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    status status NOT NULL,
    person_id bigint,
    user_id integer NOT NULL,
    CONSTRAINT worker_pkey PRIMARY KEY (id),
    CONSTRAINT worker_coordinates_id_key UNIQUE (coordinates_id),
    CONSTRAINT worker_person_id_key UNIQUE (person_id),
    CONSTRAINT user_id_fkey FOREIGN KEY (user_id)
        REFERENCES user_info (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT worker_coordinates_id_fkey FOREIGN KEY (coordinates_id)
        REFERENCES coordinates (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT,
    CONSTRAINT worker_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES person (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE SET NULL,
    CONSTRAINT worker_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES user_info (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT worker_name_check CHECK (name <> ''::text),
    CONSTRAINT worker_salary_check CHECK (salary > 0)
);

CREATE OR REPLACE FUNCTION drop_worker_cascade()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
    DELETE FROM Coordinates
    WHERE Coordinates.id = OLD.coordinates_id;
    DELETE FROM Person
    WHERE Person.id = OLD.person_id;
    RETURN OLD;
END;
$BODY$;

CREATE OR REPLACE TRIGGER drop_worker_trigger
    AFTER DELETE
    ON worker
    FOR EACH ROW
    EXECUTE FUNCTION drop_worker_cascade();
	
CREATE OR REPLACE PROCEDURE add_worker(
	IN worker_name text,
	IN coordinates_x double precision,
	IN coordinates_y double precision,
	IN worker_salary integer,
	IN worker_startdate timestamp without time zone,
	IN worker_enddate timestamp without time zone,
	IN worker_status status,
	IN person_height bigint,
	IN person_eyecolor color,
	IN person_nationality country,
	IN user_info_username text)
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
	 new_coordinates_id Worker.coordinates_id%TYPE;
	 new_person_id Worker.person_id%TYPE;
	 new_user_id User_info.id%TYPE;
BEGIN
	  SELECT INTO new_user_id User_info.id
	  FROM User_info
	  WHERE User_info.username = user_info_username;
	  
    INSERT INTO Coordinates (x, y)
    VALUES (coordinates_x, coordinates_y)
    RETURNING id INTO new_coordinates_id;
    
    new_person_id := NULL;
    IF person_height IS NOT NULL THEN
	    INSERT INTO Person (height, eyecolor, nationality)
	    VALUES (person_height, person_eyecolor, person_nationality)
	    RETURNING id INTO new_person_id;
	  END IF;
	  
	  INSERT INTO Worker(name, coordinates_id, salary, startdate, enddate, status, person_id, user_id)
	  VALUES (worker_name, new_coordinates_id, worker_salary, worker_startdate, worker_enddate, worker_status, new_person_id, new_user_id);
END;
$BODY$;

CREATE OR REPLACE PROCEDURE update_worker(
	IN worker_name text,
	IN coordinates_x double precision,
	IN coordinates_y double precision,
	IN worker_salary integer,
	IN worker_startdate timestamp without time zone,
	IN worker_enddate timestamp without time zone,
	IN worker_status status,
	IN person_height bigint,
	IN person_eyecolor color,
	IN person_nationality country,
	IN user_info_username text,
	IN worker_id bigint)
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
	 old_coordinates_id Worker.coordinates_id%TYPE;
	 old_person_id Worker.person_id%TYPE;
	 old_user_id User_info.id%TYPE;
BEGIN
	SELECT INTO old_user_id User_info.id
	FROM User_info
	WHERE User_info.username = user_info_username;
	
	--Проверка на наличие прав
	IF old_user_id <> (SELECT user_id FROM Worker WHERE Worker.id = worker_id) THEN
		RETURN;
	END IF;
	
	SELECT INTO old_coordinates_id Worker.coordinates_id
	FROM Worker
	WHERE Worker.id = worker_id;
	
	SELECT INTO old_person_id Worker.person_id
	FROM Worker
	WHERE Worker.id = worker_id;
	
	UPDATE Worker SET (name, salary, startdate, enddate, status) =
		(worker_name, worker_salary, worker_startdate, worker_enddate, worker_status)
	WHERE Worker.id = worker_id;

	
    UPDATE Coordinates SET (x, y) = (coordinates_x, coordinates_y)
    WHERE Coordinates.id = old_coordinates_id;
    
    IF person_height IS NOT NULL THEN
		UPDATE Person SET (height, eyecolor, nationality) = (person_height, person_eyecolor, person_nationality)
		WHERE Person.id = old_person_id;
	ELSE
		DELETE FROM Person WHERE Person.id = old_person_id;
		
		UPDATE Worker SET person_id = NULL
		WHERE Worker.id = worker_id; 
	END IF;
END;
$BODY$;