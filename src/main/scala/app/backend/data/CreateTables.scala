package app.backend.data

import com.augustnagro.magnum.magzio.*

import app.tables

def createTables(xa: Transactor) =
  xa.transact {
    val userTable =
      sql"""
          CREATE TABLE IF NOT EXISTS "${tables.User.table}"(
            ${tables.User.table.uid}          SERIAL  NOT NULL,
            ${tables.User.table.username}     VARCHAR(255) NOT NULL,
            ${tables.User.table.passwordHash} VARCHAR(255) NOT NULL,
            PRIMARY KEY(${tables.User.table.uid})
            );
           """

    val movieTable =
      sql"""
           CREATE TABLE IF NOT EXISTS ${tables.Movie.table}(
            ${tables.Movie.table.movieId} SERIAL NOT NULL,
            ${tables.Movie.table.uid} VARCHAR(36) NOT NULL,
            ${tables.Movie.table.name} VARCHAR(50) NOT NULL,
            ${tables.Movie.table.rating} INT,
            PRIMARY KEY(${tables.Movie.table.movieId}),
            FOREIGN KEY (${tables.Movie.table.uid}) REFERENCES "${tables.User.table}"(${tables.User.table.uid})
            );
        """

    userTable.update.run()
    movieTable.update.run()
  }