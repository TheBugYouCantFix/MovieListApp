package app.backend.db

import com.augustnagro.magnum.magzio.*

import app.tables.*

def createTables(xa: Transactor) =
  xa.transact {
    val userTable =
      sql"""
          CREATE TABLE IF NOT EXISTS "${Users.table}"(
            ${Users.table.uid}          SERIAL  NOT NULL,
            ${Users.table.username}     VARCHAR(255) UNIQUE NOT NULL,
            ${Users.table.passwordHash} VARCHAR(255) NOT NULL,
            PRIMARY KEY(${Users.table.uid})
            );
           """

    val movieTable =
      sql"""
           CREATE TABLE IF NOT EXISTS ${Movies.table}(
            ${Movies.table.movieId} SERIAL NOT NULL,
            ${Movies.table.uid} INT NOT NULL,
            ${Movies.table.name} VARCHAR(50) NOT NULL,
            ${Movies.table.rating} INT,
            ${Movies.table.review} VARCHAR(300),
            PRIMARY KEY(${Movies.table.movieId}),
            FOREIGN KEY (${Movies.table.uid}) REFERENCES "${Users.table}"(${Users.table.uid}),
            CONSTRAINT unique_movie_name_per_user UNIQUE (${Movies.table.uid}, ${Movies.table.name})
            );
        """

    userTable.update.run()
    movieTable.update.run()
  }