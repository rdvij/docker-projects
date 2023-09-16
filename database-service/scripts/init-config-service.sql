SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Schema for Configurations Service
CREATE SCHEMA IF NOT EXISTS config_service AUTHORIZATION postgres; 

CREATE TABLE IF NOT EXISTS config_service.remote_server_type (
    type_id integer PRIMARY KEY,
    type_name varchar(10) UNIQUE NOT NULL
);

ALTER TABLE config_service.remote_server_type OWNER TO postgres;

CREATE SEQUENCE config_service.remote_server_type_seq
    INCREMENT 1
    START 2001
    MINVALUE 1
    OWNED BY config_service.remote_server_type.type_id;

ALTER SEQUENCE config_service.remote_server_type_seq
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS config_service.remote_server_configuration (
    config_id integer PRIMARY KEY,
    remote_server_type varchar(10) references config_service.remote_server_type(type_name),
    remote_location_url character varying (250) NOT NULL,
    remote_user character varying(50) NOT NULL,
    remote_password character varying(50) NOT NULL,
    remote_token character varying(250),
    remote_poll_freq integer NOT NULL, -- In minutes, seconds support not required
    remote_active integer CHECK (remote_active = 0 OR remote_active =1),
    notification_recipient character varying (50),
    regex varchar (50) NOT NULL,
    sub_path varchar (50),
    created_by character varying(50) NOT NULL,
    updated_by character varying(50),
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_at timestamp with time zone
);

ALTER TABLE config_service.remote_server_configuration OWNER TO postgres;

CREATE SEQUENCE config_service.remote_server_configuration_seq
    INCREMENT 1
    START 1001
    MINVALUE 1
    OWNED BY config_service.remote_server_configuration.config_id;

ALTER SEQUENCE config_service.remote_server_configuration_seq
    OWNER TO postgres;
    
INSERT INTO config_service.remote_server_type(type_id,type_name)
	VALUES (nextval('config_service.remote_server_type_seq'),'AWS');
	
INSERT INTO config_service.remote_server_type(type_id,type_name)
	VALUES (nextval('config_service.remote_server_type_seq'),'SFTP');	    

