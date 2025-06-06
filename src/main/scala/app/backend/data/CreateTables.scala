package app.backend.data

import com.augustnagro.magnum.magzio.*

import app.tables

def createTables(xa: Transactor) =
  xa.transact {
    val userTable =
      sql"""
          CREATE TABLE IF NOT EXISTS "${tables.Users.table}"(
            ${tables.Users.table.uid}          SERIAL  NOT NULL,
            ${tables.Users.table.username}     VARCHAR(255) UNIQUE NOT NULL,
            ${tables.Users.table.passwordHash} VARCHAR(255) NOT NULL,
            PRIMARY KEY(${tables.Users.table.uid})
            );
           """

    val movieTable =
      sql"""
           CREATE TABLE IF NOT EXISTS ${tables.Movies.table}(
            ${tables.Movies.table.movieId} SERIAL NOT NULL,
            ${tables.Movies.table.uid} INT NOT NULL,
            ${tables.Movies.table.name} VARCHAR(50) NOT NULL,
            ${tables.Movies.table.rating} INT,
            PRIMARY KEY(${tables.Movies.table.movieId}),
            FOREIGN KEY (${tables.Movies.table.uid}) REFERENCES "${tables.Users.table}"(${tables.Users.table.uid})
            );
        """

    userTable.update.run()
    movieTable.update.run()
  }