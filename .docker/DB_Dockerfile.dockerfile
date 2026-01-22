# Dockerfile
# Uses the official MySQL image; default settings otherwise.
FROM mysql:8.0

# Any .sql/.sql.gz/.sh dropped here runs ONCE on initial DB creation.
# We name it late in sort order so MySQL’s built-ins (user/db creation) run first.
COPY init.sql /docker-entrypoint-initdb.d/99-custom.sql