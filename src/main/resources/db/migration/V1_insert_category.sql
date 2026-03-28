INSERT INTO categories (name) VALUES
("Kedisiplinan"),
("Kerajinan"),
("Ibadah")
ON CONFLICT (id) DO NOTHING;