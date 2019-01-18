CREATE TABLE reservation (
    id uuid,
    name char[],
    during tsrange,
    EXCLUDE USING GIST (during WITH &&)
);