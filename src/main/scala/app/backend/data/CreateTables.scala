//package app.backend.data
//
//import com.augustnagro.magnum.magzio.*
//
//import app.tables
//
//def createTables(xa: Transactor) =
//  xa.transact {
//    val usersTable =
//      sql"""
//          CREATE TABLE IF NOT EXISTS ${tables.User.table}(
//            ${tables.User.table.uid}          VARCHAR(36)  NOT NULL,
//            ${tables.User.username}     VARCHAR(255) NOT NULL,
//            ${tables.User.passwordHash} VARCHAR(255) NOT NULL,
//            PRIMARY KEY(${tables.User.table.uid})
//           """
//  }