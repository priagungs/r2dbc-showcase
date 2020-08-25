DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS hobby CASCADE;

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE hobby (
    id SERIAL PRIMARY KEY,
    name VARCHAR,
    person_id INTEGER,
    CONSTRAINT fk_hobby_person
        FOREIGN KEY(person_id)
        REFERENCES person(id)
);

INSERT INTO person(name) VALUES ('Agung');
INSERT INTO hobby(name, person_id) VALUES ('swimming', 1), ('watching tv', 1);