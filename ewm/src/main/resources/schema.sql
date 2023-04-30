DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS requests CASCADE;


    CREATE TABLE IF NOT EXISTS users
    ( id BIGINT GENERATED ALWAYS AS IDENTITY (MINVALUE  0 START WITH 0 INCREMENT BY 1) NOT NULL ,
    name VARCHAR(100),
    email VARCHAR(320) NOT NULL ,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT u_email UNIQUE(email));

    CREATE TABLE IF NOT EXISTS categories
    ( id BIGINT GENERATED ALWAYS AS IDENTITY (MINVALUE  0 START WITH 0 INCREMENT BY 1) NOT NULL ,
    name VARCHAR(100),
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT u_name UNIQUE(name));

CREATE TABLE IF NOT EXISTS locations
( id BIGINT GENERATED ALWAYS AS IDENTITY (MINVALUE  0 START WITH 0 INCREMENT BY 1) NOT NULL ,
  lat FLOAT,
  lon FLOAT,
  CONSTRAINT pk_locations PRIMARY KEY (id));

    CREATE TABLE IF NOT EXISTS events
    ( id BIGINT GENERATED ALWAYS AS IDENTITY (MINVALUE  0 START WITH 0 INCREMENT BY 1) NOT NULL ,
      annotation VARCHAR(500) NOT NULL ,
      category_id BIGINT NOT NULL ,
      confirmed_requests BIGINT ,
      description VARCHAR(1000),
      event_date TIMESTAMP NOT NULL,
      created_on TIMESTAMP NOT NULL,
      initiator_id BIGINT NOT NULL ,
      location_id BIGINT NOT NULL ,
      paid BOOLEAN NOT NULL ,
      participant_limit BIGINT NOT NULL ,
      published_on TIMESTAMP ,
      request_moderation BOOLEAN ,
      state VARCHAR(30),
      title VARCHAR(320) NOT NULL ,
      views BIGINT ,
      CONSTRAINT pk_events PRIMARY KEY (id),
      CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users (id) on delete cascade ,
      CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES locations (id) on delete cascade,
      CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (id) on delete cascade);

CREATE TABLE IF NOT EXISTS compilations
( id BIGINT GENERATED ALWAYS AS IDENTITY (MINVALUE  0 START WITH 0 INCREMENT BY 1) NOT NULL ,
  is_pinned BOOLEAN,
  title VARCHAR (320) NOT NULL ,
  CONSTRAINT pk_compilations PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS compilations_events
(   event_id       BIGINT,
    compilation_id BIGINT,
    CONSTRAINT fk_to_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_to_events FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT uq_compilations_events UNIQUE (compilation_id, event_id));

CREATE TABLE IF NOT EXISTS requests
( id BIGINT GENERATED ALWAYS AS IDENTITY (MINVALUE  0 START WITH 0 INCREMENT BY 1) NOT NULL ,
  created TIMESTAMP NOT NULL,
  event_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL ,
  status VARCHAR(100),
  CONSTRAINT pk_requests PRIMARY KEY (id),
  CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id),
  CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id));