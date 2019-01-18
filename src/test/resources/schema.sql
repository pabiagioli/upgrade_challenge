CREATE TABLE public.reservations
(
    id uuid NOT NULL,
    during tsrange NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT overlapper EXCLUDE USING gist (
        tsrange(lower(during),upper(during),'()') WITH &&)

)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.reservations
    OWNER to postgres;